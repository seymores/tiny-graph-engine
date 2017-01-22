(ns tiny-graph.trains
  (:require [clojure.string :as string]
            [tiny-graph.core :refer :all]))

(defn- parse-split-line
  "Split the input line into seq of node format chars"
  [text]
  (string/split text #":\s+|\s*(,)\s*"))

(defn- to-node-format
  "First char and second char expected to be node, the rest is taken as weight/distance."
  [text]
    [(-> text first str) (str (get text 1)) (Integer/parseInt (subs text 2 (count text)))])

(defn- add-node-to-graph
  [g data]
  (let [node (to-node-format data)]
  (apply tiny-graph.core/add-edge g node)))

(defn load-input-data-from-file
  [filename]
  (-> filename slurp string/trim-newline parse-split-line rest))

(defn load-data-to-graph
  "Load data from string and construct the graph.
   Data is expected to be a sequence of 3 chars, e.g., 'AB5' 'BC4'.
   Returns the expected atom map (bucket)."
  [data]
  (let [graph (atom {})]
    (doseq [d data] (add-node-to-graph graph d))
    graph))

(defn run
  "Loads data from 'input.txt' to generate a graph, and then use various functions
   from tiny-graph.core to manipulate the graph data structure to answer the questions."
  []
  (let [data (load-input-data-from-file "input.txt")
        graph (load-data-to-graph data)]
      (println "Graph generated: " @graph )
      (println "-----------------------------------")
      (println "Output #1:" (tiny-graph.core/distance @graph :A :B :C))
      (println "Output #2:" (tiny-graph.core/distance @graph :A :D))
      (println "Output #3:" (tiny-graph.core/distance @graph :A :D :C))
      (println "Output #4:" (tiny-graph.core/distance @graph :A :E :B :C :D))
      (println "Output #5:" (tiny-graph.core/distance @graph :A :E :D))
      (println "Output #6:" (tiny-graph.core/trip-count @graph :C :C 3))
      (println "Output #7:" (tiny-graph.core/trip-count-with-exact-stop @graph :A :C 4))
      (println "Output #8:" (tiny-graph.core/trip-distance @graph :A :C))
      (println "Output #9:" (tiny-graph.core/trip-distance @graph :A :B))
      (println "Output #10:" (tiny-graph.core/trips-with-max-distance @graph :C :C 30))
      (println "-----------------------------------")))

(println "** Hint: Execute (run)")
