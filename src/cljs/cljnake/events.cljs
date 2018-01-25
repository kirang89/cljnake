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
 (fn [db _]
   (update-in db [:snake] utils/move-snake)))
