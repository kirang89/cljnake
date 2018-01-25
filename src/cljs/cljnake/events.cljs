(ns cljnake.events
  (:require [re-frame.core :as rf]
            [cljnake.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-fx
 ::start-game
 (fn []
   (prn "start game")
   ;; (js/setInterval #(rf/dispatch [::next-state]) 150)
   ))

(rf/reg-event-db
 ::change-snake-direction
 (fn [db [_ direction]]
   (prn "move snake " direction)
   db))

(rf/reg-event-fx
 ::key-pressed
 (fn [cofx [_ key]]
   (if (= key :enter)
     {::start-game nil}
     {:dispatch [::cljnake.events/change-snake-direction key]})))
