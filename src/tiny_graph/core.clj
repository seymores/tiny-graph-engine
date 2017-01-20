(ns tiny-graph.core)

(def graph (atom {}))

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
  (println " 3 " node1 " - " node2)
  (let [n1 (keyword node1)
        n2 (keyword node2)
        distance-val (get-in @graph [n1 n2])]
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

(defn hop
  [g start end]
  (let [distance (get-in @g [start end])]
    (if-not (nil? distance)
      {end distance}
      nil)))

;  {
;    :A {:B 5 :D 5 :E 7} 
;    :B {:C 4} 
;    :C {:D 8 :E 2} 
;    :D {:C 8 :E 6} 
;    :E {:B 3}
;   }

(defn has-edge-node? 
  [node e]
  (contains? node e))

(defn route
  "Traverse from root to goal"
  [g start goal max-hop]
  (if (has-edge-node? (start @g) goal)
    (hop g start goal)
    ()
  )
  )


;; ///////////////////////////////////////////////////////////////////////////////

(add-node graph "A" "B" "C" "D" "E")
(add-edge graph "A" "B" 5)
(add-edge graph "B" "C" 4)
(add-edge graph "C" "D" 8)
(add-edge graph "D" "C" 8)
(add-edge graph "D" "E" 6)
(add-edge graph "A" "D" 5)
(add-edge graph "C" "E" 2)
(add-edge graph "E" "B" 3)
(add-edge graph "A" "E" 7)

;; (route graph "A" "B" "C")
;; (route graph "A" "D")
;; (route graph "A" "D" "C")
;; (route graph "A" "E" "B" "C" "D")
;; (route graph "A" "E" "D")
