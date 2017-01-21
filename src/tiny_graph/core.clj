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
  ;; (println " 3 " node1 " - " node2)
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
  [edge start end]
  (let [distance (get-in edge [start end])]
    (if-not (nil? distance)
      {start {end distance}}
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

(def bucket (atom []))

(defn trace
  [g start end max-distance parent]
  (println " * __ " start "-" end ",   max=" max-distance ",p==" parent)

  (let [nodes (start g)]

    (if (has-edge-node? nodes end)
      ; found
      (do 
        (if (> max-distance (apply distance graph (conj parent start end)))
          (do 
            (swap! bucket conj (conj parent start end))
            (trace g end end max-distance (conj parent start))
            (println " -----------------" )
            (doseq [[k v] (dissoc nodes end)] (trace g k end max-distance (conj parent start)))
            )
          )
        )

      ; not found
      (do
        (println " XXXX=" start "=>" end " pe=" parent " , nodes=" nodes ) 
        (doseq [[k v] nodes] (trace g k end max-distance (conj parent start)))
        )
      )
    )
  )

;;
; {{{
(defn trace-with-max-hop
  [g start end max-hop parent]
  (println  " 1  __ start=" start "- end=" end ",   max=" max-hop ",p==" parent  "hop=" max-hop  "\n")

  (let [nodes (start g)]

    (if (> max-hop 0)

      (if (has-edge-node? nodes end)

        ; found
        (do 
          
          (swap! bucket conj (conj parent start end))
          
          (trace-with-max-hop g end end (dec max-hop) (conj parent start))

          (doseq [[k v] (dissoc nodes end)] (trace-with-max-hop g k end (dec max-hop) (conj parent start)))

          (println "\n")
          
          )

        ; not found
        (do 
          (println " XXXX=" start "=>" end " pe=" parent " , nodes=" nodes " hop="  max-hop) 
          (doseq [[k v] nodes] (trace-with-max-hop g k end (dec max-hop) (conj parent start)))
          (println "--XXX")
        )

        )

        (println "Done max hop" max-hop)
      )
    )
  )
; }}}

; {{{
(defn trace_2
  [g start end max-hop]

  (println "> " start "-" end ",   max=" max-hop)

  (let [nodes (start g)]
    (if (has-edge-node? nodes end)
      (list start end)
      (for [[k v] nodes]  (-> (trace_2 g k end (dec max-hop)) flatten (into [start])))
      )
    )
  )
; }}}

; {{{
(defn traverse
  [g start end max-hop]
  (println start "->" end ", m=" max-hop)

  (if (has-edge-node? (start @g) end)
    (do 
      (let [current-hit (hop @g start end) 
            next-hit (traverse g end end (dec max-hop))]
        (println "X " start "| " current-hit " --" next-hit)
        (concat current-hit next-hit)
        )
      )

    (if (> max-hop 0)
      (for [[k v] (start @g)] (traverse g k end (dec max-hop)))
      ()
      )
    )
  )
; }}}

;; (defn traverse
;;   [g start end max-hop]
;;     (println start "->" end ", m=" max-hop)
;;     (if (has-edge-node? (start @g) end)
;;       (hop @g start end)
;;
;;       (if (> max-hop 0)
;;         (for [[k v] (start @g)] (traverse g k end (dec max-hop)))
;;         ())
;;       )
;;   )

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

