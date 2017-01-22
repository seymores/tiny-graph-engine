(ns tiny-graph.core)


(defn add-node
  "Add node or nodes to the graph"
  [g & nodes] 
  (doseq [n nodes]
    (swap! g assoc (keyword n) {})))

(defn add-edge
  "Add edges to node"
  [g node1 node2 weight]
  (let [n1 (keyword node1)
        n2 (keyword node2)]    
    (swap! g assoc-in [n1 n2] weight)))

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
  [g & nodes]
  (let [r (first nodes)
        n (-> nodes rest first)
        b (rest nodes)]
    (try 
      (if (> (count b) 1)
        (+ (distance-val g r n) (apply distance g b))
        (distance-val g r n))
      (catch Exception e "NO SUCH ROUTE"))))

(defn shortest-distance
  [g trips]
    (reduce 
        (fn [n1 n2] (if (< (apply distance g n1) (apply distance g n2))
            n1
            n2)) trips ))

(defn has-edge-node? 
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

;;
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
    (count (filter (fn [a] (= (count a) (+ stop-count 1)) ) @bucket))))


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

;; (def graph (atom {}))
;; (add-node graph "A" "B" "C" "D" "E")
;; (add-edge graph "A" "B" 5)
;; (add-edge graph "B" "C" 4)
;; (add-edge graph "C" "D" 8)
;; (add-edge graph "D" "C" 8)
;; (add-edge graph "D" "E" 6)
;; (add-edge graph "A" "D" 5)
;; (add-edge graph "C" "E" 2)
;; (add-edge graph "E" "B" 3)
;; (add-edge graph "A" "E" 7)
;;
;;
;;
;; (println "Output #1" (distance graph :A :B :C))
;; (println "Output #2" (distance graph :A :D))
;; (println "Output #3" (distance graph :A :D :C))
;; (println "Output #4" (distance graph :A :E :B :C :D))
;; (println "Output #5" (distance graph :A :E :D))
;;
;;
;; (trace-with-max-hop @graph :C :C 3 [])
;;
;; (println "Output #6" (count @bucket))
;;
;; (reset! bucket [])
;;
;; (trace-with-max-hop @graph :A :C 4 [])
;; (def result (filter (fn [a] (= (count a) 5) ) @bucket))
;;
;; (println "Output #7" (count result))
;; (reset! bucket [])
;;
;; (trace-with-max-hop @graph :A :C 3 [])
;;   
;; (def result (reduce 
;;         (fn [n1 n2]
;;           (if (< (apply distance graph n1) (apply distance graph n2))
;;             n1
;;             n2
;;           )
;;         )
;;         @bucket
;;    ))
;; (println "Output #8" (apply distance graph result))
;;
;; (reset! bucket [])
;;
;; (trace-with-max-hop @graph :B :B 5 [])
;; (def result (reduce 
;;         (fn [n1 n2]
;;           (if (< (apply distance graph n1) (apply distance graph n2))
;;             n1
;;             n2
;;           )
;;         )
;;         @bucket
;;    ))
;; (println "Output #9" (apply distance graph result))
;;
;;
;; (reset! bucket [])
;;
;; (trace-with-distance @graph :C :C 30 [])
;;
;; (println "Output #10" (count @bucket))
