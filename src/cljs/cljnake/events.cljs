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

(rf/reg-event-db
 ::grow-snake
 (fn [{:keys [board snake] :as db} _]
   (-> db
       (update :score inc)
       (assoc :food (utils/rand-pos board snake))
       (as-> db-inst
           (let [tail        (first {:body snake})
                 new-tail    (mapv + tail (utils/tail-offset snake))
                 grown-snake (into [new-tail] (:body snake))]
             (assoc-in db-inst [:snake :body] grown-snake))))))

(rf/reg-event-fx
 ::tick-handler
 (fn [{{:keys [snake food] :as db} :db} _]
   (let [new-snake (utils/move-snake snake)
         new-db    (assoc db :snake new-snake)]
     (cond-> {}
       true
       (assoc :db new-db)

       (utils/collided? (:board db) new-snake)
       (assoc :dispatch [::stop-game])

       (utils/collided-with-self? new-snake)
       (assoc :dispatch [::stop-game])

       (utils/ate? new-snake food)
       (assoc :dispatch [::grow-snake])))))

(rf/reg-event-fx
 ::start-game
 (fn [{:keys [db]} _]
   {:db    (-> db
               (assoc :game-running? true)
               (assoc :first-run?    false))
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
