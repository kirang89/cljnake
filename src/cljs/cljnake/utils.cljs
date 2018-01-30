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

(defn ate? [snake food-pos]
  "Returns true if snake's head position matches the
  food position, false otherwise."
  (= (last (:body snake)) food-pos))

(defn out-of-bound? [{:keys [width height]} [x y]]
  "Returns true if a position is out of bounds of the board,
  false otherwise."
  (or (or (< x -1) (> x width))
      (or (< y -1) (> y height))))

(defn collided-with-self? [{:keys [body]}]
  "Returns true if snake collided with it's body, false otherwise."
  (let [head          (last body)
        headless-body (set (drop-last body))]
    (contains? headless-body head)))

(defn collided? [board {:keys [body] :as snake}]
  "Returns true if snake collided with board, false otherwise."
  (let [head                (last body)
        next-head-positions (map #(mapv + (second %1) head)
                                 direction->offset)
        board-collision?    (some (partial out-of-bound? board)
                                  next-head-positions)]
    (or board-collision?
        (collided-with-self? snake))))

(defn tail-offset [{:keys [direction]}]
  "Returns the offset for the direction in which a snake
   tail is expected to grow."
  (case direction
    :up    (:down direction->offset)
    :down  (:up direction->offset)
    :left  (:right direction->offset)
    :right (:left direction->offset)))
