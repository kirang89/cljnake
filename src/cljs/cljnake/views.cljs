(ns cljnake.views
  (:require [re-frame.core :as rf]
            [cljnake.subs :as subs]))

(defn board-row [row-idx width snake-pos]
  (into [:tr]
        (for [column-idx (range width)]
          [:td.cell])))

(defn board-panel []
  (let [board (rf/subscribe [::subs/board])
        snake (rf/subscribe [::subs/snake-body])
        food  (rf/subscribe [::subs/food])]
    (fn []
      (let [snake-pos        (into #{} @snake)
            {:keys
             [width height]} @board
            rows             (for [idx (range height)]
                               [board-row idx width snake-pos])]
        [:table.stage {:style {:height 377 :width 527}}
         (into [:tbody]
               (for [y (range height)]
                 (into [:tr]
                       (for [x (range width) :let [pos [x y]]]
                         (cond
                           (contains? snake-pos pos) [:td.snake-on-cell]
                           (= @food pos)             [:td.point]
                           :else                     [:td.cell])))))]))))

(defn snake-panel []
  (let [snake (rf/subscribe [::subs/snake-body])]
    [:div (str "Snake " @snake)]))

(defn score-panel []
  (let [score (rf/subscribe [::subs/score])]
    [:div.score "Score: " @score]))

(defn game-panel []
  [:div
   [:h1.heading "cljnake 🐍"]
   [score-panel]
   [board-panel]
   [snake-panel]])
