(ns cljnake.views
  (:require [goog.events :as gevents]
            [re-frame.core :as rf]
            [cljnake.events :as events]
            [cljnake.subs :as subs]
            [cljnake.utils :as utils]))

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

(defn score-panel []
  (let [score (rf/subscribe [::subs/score])]
    [:div.score "Score: " @score]))

(defn start-game-panel []
  (if @(rf/subscribe [::subs/first-run?])
    [:div.start-game [:i "Press Enter key to start"]]
    [:div]))

(defn game-over-panel []
  (let [game-over? (rf/subscribe [::subs/game-over?])
        score      (rf/subscribe [::subs/score])]
    [:div.game-over
     {:style {:display (if @game-over? "block" "none")}}
     [:div.game-over-label "GAME OVER"]
     [:div.score-label (str "Score: " @score)]
     [:button.btn.btn-primary
      {:type "input"
       :on-click #(do
                    (rf/dispatch [::events/initialize-db])
                    (rf/dispatch [::events/start-game]))}
      "Play Again"]]))

(defn game-panel []
  [:div
   [:h1.heading "cljnake 🐍"]
   [score-panel]
   [board-panel]
   [start-game-panel]
   [game-over-panel]])

;; key press handler
(defonce key-handler
  (gevents/listen
   js/window
   "keydown"
   (fn [e]
     (let [key-pressed      (.-keyCode e)
           keys-of-interest (set (keys utils/keycode->event))]
       (when (contains? keys-of-interest key-pressed)
         (rf/dispatch [::events/key-pressed
                       (get utils/keycode->event
                            key-pressed)]))))))
