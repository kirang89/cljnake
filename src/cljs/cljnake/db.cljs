(ns cljnake.db
  (:require [cljnake.utils :as utils]))

(def board {:width 35 :height 25})

(def snake {:direction :right
            :body      [[1 4] [2 4] [3 4] [4 4]]})

(def default-db
  {:board         board
   :snake         snake
   :food          (utils/rand-pos board snake)
   :score         0
   :first-run?    true
   :game-running? false
   :game-over?    false})
