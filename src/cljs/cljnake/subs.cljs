(ns cljnake.subs
  (:require [re-frame.core :as rf]))

(defn subscribe [name & keys]
  "Creates a subscription to `key` with given `name`"
  (rf/reg-sub
   name
   (fn [db]
     (get-in db keys))))

(subscribe ::board :board)
(subscribe ::score :score)
(subscribe ::food :food)
(subscribe ::game-running? :game-running?)
(subscribe ::snake-body :snake :body)
