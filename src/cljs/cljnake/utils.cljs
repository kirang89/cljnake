(ns cljnake.utils)

(def direction->offset
  {:up    [ 0  -1]
   :down  [ 0   1]
   :left  [-1   0]
   :right [ 1   0]})

(def keycode->event
  {13 :enter
   38 :up
   40 :down
   37 :left
   39 :right})

(defn rand-pos [{:keys [width height]} {:keys [body] :as snake}]
  "Retrieves a random position on the board exclusive of the snake's body."
  (let [board-positions (into #{}
                              (for [x (range width) y (range height)]
                                [x y]))
        snake-body      (into #{} body)
        available-positions (remove snake-body board-positions)]
    (rand-nth available-positions)))

(defn move-snake [{:keys [body direction] :as snake}]
  (let [offset    (direction->offset direction)
        new-head  (mapv + offset (last body))]
    (update snake :body #(-> %
                             rest
                             vec
                             (conj new-head)))))

(defn can-move? [old-dir new-dir]
  "Returns false if snake cannot move from old direction to new, true otherwise."
  (condp = [old-dir new-dir]
    [:right  :left ] false
    [:left   :right] false
    [:up     :down ] false
    [:down   :up   ] false
    [old-dir nil   ] false
    true))
