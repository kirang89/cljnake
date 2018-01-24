(ns cljnake.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::board
 (fn [db]
   (:board db)))

(rf/reg-sub
 ::snake-body
 (fn [db]
   (get-in db [:snake :body])))

(rf/reg-sub
 ::score
 (fn [db]
   (:score db)))

(rf/reg-sub
 ::food
 (fn [db]
   (:food db)))
