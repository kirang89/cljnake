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
   (js/setInterval #(rf/dispatch [::next-state]) 150)))

(rf/reg-event-db
 ::change-snake-direction
 (fn [db [_ direction]]
   (prn "move snake " direction)
   db))

(rf/reg-event-fx
 ::key-pressed
 (fn [{:keys [db]} [_ key]]
   (merge {:db (assoc db :game-running? true)}
          (if (and (= key :enter) (not (:game-running? db))) ; Pressing enter after starting game should do nothing
            {::start-game nil}
            {:dispatch [::cljnake.events/change-snake-direction key]}))))

(rf/reg-event-db
 ::next-state
 (fn [db _]
   (let [{:keys [direction body]} (:snake db)
         new-snake-pos            (utils/move-snake body direction)]
     (assoc-in db [:snake :body] new-snake-pos))))
