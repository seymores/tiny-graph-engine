(ns tiny-graph.core)

(def graph (atom {}))

(defn add-node
  [g node]
  (swap! g assoc (keyword node) {}))

(defn add-edge
  [g node1 node2 weight]
  (let [n1 (keyword node1)
        n2 (keyword node2)]    
  (swap! g assoc-in [n1 n2] weight)))

(defn route
  [g start goal]
  ;
  )


