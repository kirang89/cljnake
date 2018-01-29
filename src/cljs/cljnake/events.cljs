(ns cljnake.events
  (:require [re-frame.core :as rf]
            [cljnake.db :as db]
            [cljnake.utils :as utils]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(defonce interval-handler
  (let [intervals (atom {})]
    (fn handler [{:keys [action id event frequency]}]
      (condp = action
        :start (swap! intervals assoc id
                      (js/setInterval
                       #(rf/dispatch event)
                       frequency))
        :stop  (do (js/clearInterval (id @intervals))
                   (swap! intervals dissoc id))
        :clean (doseq [id (keys @intervals)]
                 (handler {:action :stop :id id}))))))

;; when this code is reloaded `:clean` existing intervals
(interval-handler {:action :clean})

(rf/reg-fx
 ::tick
 interval-handler)

(rf/reg-event-db
 ::change-snake-direction
 (fn [{:keys [snake] :as db} [_ direction]]
   (if (utils/can-move? (:direction snake) direction)
     (assoc-in db [:snake :direction] direction)
     db)))

(rf/reg-event-fx
 ::tick-handler
 (fn [{{:keys [snake food] :as db} :db} _]
   (let [new-snake (utils/move-snake snake)
         board     (:board db)
         new-db    (cond-> db
                     (utils/ate? new-snake food)
                     (update :score inc)

                     (utils/ate? new-snake food)
                     (assoc :food (utils/rand-pos board new-snake))

                     true
                     (assoc :snake new-snake))]
     (if (utils/collided? board new-snake)
       {:db new-db
        :dispatch [::stop-game]}
       {:db new-db}))))

(rf/reg-event-fx
 ::start-game
 (fn [{:keys [db]} _]
   {:db    (assoc db :game-running? true)
    ::tick {:action    :start
            :id        :ticker-1
            :event     [::tick-handler]
            :frequency 150}}))

(rf/reg-event-fx
 ::stop-game
 (fn [{:keys [db]} _]
   {:db    (assoc db
                  :game-running? false
                  :game-over? true)
    ::tick {:action :stop
            :id     :ticker-1}}))

(rf/reg-event-fx
 ::key-pressed
 (fn [{:keys [db]} [_ key]]
   (if (and (= key :enter) (not (:game-running? db))) ; Pressing enter after starting game should do nothing
     {:dispatch [::start-game]}
     {:dispatch [::cljnake.events/change-snake-direction key]})))
