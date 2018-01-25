(ns cljnake.events
  (:require [re-frame.core :as rf]
            [cljnake.db :as db]
            [cljnake.utils :as utils]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-fx
 ::start-game
 (fn []
   (js/setInterval #(rf/dispatch [::tick]) 150)))

(rf/reg-event-db
 ::change-snake-direction
 (fn [{:keys [snake] :as db} [_ direction]]
   (if (utils/can-move? (:direction snake) direction)
     (assoc-in db [:snake :direction] direction)
     db)))

(rf/reg-event-fx
 ::key-pressed
 (fn [{:keys [db]} [_ key]]
   (merge {:db (assoc db :game-running? true)}
          (if (and (= key :enter) (not (:game-running? db))) ; Pressing enter after starting game should do nothing
            {::start-game nil}
            {:dispatch [::cljnake.events/change-snake-direction key]}))))

(rf/reg-event-db
 ::tick
 (fn [{:keys [snake food] :as db} _]
   (let [new-snake (utils/move-snake snake)
         board     (:board db)]
     (cond-> db
       (utils/ate? new-snake food)
       (update :score inc)

       (utils/ate? new-snake food)
       (assoc :food (utils/rand-pos board new-snake))

       true
       (assoc :snake new-snake)))))
