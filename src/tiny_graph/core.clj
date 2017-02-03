(ns tiny-graph.core)

(defn add-node
  [g & nodes]
  (let [graph (conj g {(first nodes) {}})
        bal (rest nodes)]
    (if-not (empty? bal)
      (apply add-node graph bal)
      graph)))

(defn add-edge
  "Add edge to node"
  [g node1 node2 weight]
  (let [n1 (keyword node1)
        n2 (keyword node2)]
    (assoc-in g [n1 n2] weight)))

(defn distance-val
  "Returns the weight/distance between two nodes"
  [g node1 node2]
  (let [n1 (keyword node1)
        n2 (keyword node2)
        distance-val (get-in g [n1 n2])]
    (if (nil? distance-val)
      (throw (Exception. "NO SUCH ROUTE"))
      distance-val)))

(defn distance
  "Returns the distance/weight for the given node path"
  [g & nodes]
  (let [r (first nodes)
        n (-> nodes rest first)
        b (rest nodes)]
    (try
      (if (> (count b) 1)
        (+ (distance-val g r n) (apply distance g b))
        (distance-val g r n))
      (catch Exception e "NO SUCH ROUTE"))))

(defn- shortest-distance
  "Returns the shortest trip from list of trips"
  [g trips]
  (reduce
   (fn [n1 n2] (if (< (apply distance g n1) (apply distance g n2))
                 n1
                 n2)) trips))

(defn- has-edge-node?
  [node e]
  (contains? node e))

(defn trace-with-distance
  [g start end max-distance parent bucket]
  (let [nodes (start g)]
    (if (has-edge-node? nodes end)
      (when (> max-distance (apply distance g (conj parent start end)))
        (swap! bucket conj (conj parent start end))
        (trace-with-distance g end end max-distance (conj parent start) bucket)
        (doseq [[k v] (dissoc nodes end)] (trace-with-distance g k end max-distance (conj parent start) bucket)))
      (doseq [[k v] nodes] (trace-with-distance g k end max-distance (conj parent start) bucket)))))

; {{{
(defn trace-with-max-hop
  [g start end max-hop parent bucket]
  (let [nodes (start g)]
    (if (pos? max-hop)
      (if (has-edge-node? nodes end)
        (do
          (swap! bucket conj (conj parent start end))
          (trace-with-max-hop g end end (dec max-hop) (conj parent start) bucket)
          (doseq [[k v] (dissoc nodes end)] (trace-with-max-hop g k end (dec max-hop) (conj parent start) bucket)))
        (doseq [[k v] nodes] (trace-with-max-hop g k end (dec max-hop) (conj parent start) bucket))))))
; }}}

(defn trip-count
  "Count the number of trips with specified max stop."
  [g start end max-stop]
  (let [bucket (atom [])]
    (trace-with-max-hop g start end max-stop [] bucket)
    (count @bucket)))

(defn trip-count-with-exact-stop
  "Count the number of trips between nodes with the exact specified number of stops"
  [g start end stop-count]
  (let [bucket (atom [])]
    (trace-with-max-hop g start end stop-count [] bucket)
    (count (filter (fn [a] (= (count a) (inc stop-count))) @bucket))))

(defn trip-distance
  "Returns the shortest distance between between nodes"
  [g start end]
  (let [total-nodes (count g)
        bucket (atom [])]
    (trace-with-max-hop g start end total-nodes [] bucket)
    ;; (println bucket)
    (apply distance g (shortest-distance g @bucket))))

(defn trips-with-max-distance
  "Find trips with specified max distance"
  [g start end max-distance]
  (let [bucket (atom [])]
    (trace-with-distance g start end max-distance [] bucket)
    (count @bucket)))
