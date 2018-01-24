(ns cljnake.utils)

(defn rand-pos [{:keys [width height]} {:keys [body] :as snake}]
  (let [board-positions (into #{}
                              (for [x (range width) y (range height)]
                                [x y]))
        snake-body      (into #{} body)
        available-positions (remove snake-body board-positions)]
    (rand-nth available-positions)))
