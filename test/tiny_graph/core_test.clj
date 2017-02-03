(ns tiny-graph.core-test
  (:require [clojure.test :refer :all]
            [tiny-graph.core :refer :all]))

; (def graph (add-node {} "A" "B" "C" "D" "E"))
(def edges `(["A" "B" 5] ["B" "C" 4] ["C" "D" 8] ["D" "C" 8] ["D" "E" 6] ["A" "D" 5]
            ["C" "E" 2] ["E" "B" 3] ["A" "E" 7]))

(def graph (apply merge-with into  (map #(apply add-edge {} %) edges)))

; (add-edge graph "A" "B" 5)
; (add-edge graph "B" "C" 4)
; (add-edge graph "C" "D" 8)
; (add-edge graph "D" "C" 8)
; (add-edge graph "D" "E" 6)
; (add-edge graph "A" "D" 5)
; (add-edge graph "C" "E" 2)
; (add-edge graph "E" "B" 3)
; (add-edge graph "A" "E" 7)

; What the graph looks like
;
; {
;    :A {:B 5 :D 5 :E 7}
;    :B {:C 4}
;    :C {:D 8 :E 2}
;    :D {:C 8 :E 6}
;    :E {:B 3}
;   }
;

(deftest test-graph-init
  (testing "Test adding of nodes and edges to graph"
    (is (= 5 (count graph)))))

(deftest test-node-distance-val-calculation
  (testing "Test node distance calculation"
    (is (= 5 (distance-val graph :A :B)))
    (is (= 2 (distance-val graph :C :E)))))

(deftest test-graph-distance-calculation
  (testing "Test node distance count"
    (is (= 5 (distance graph :A :B)))
    (is (= 13 (distance graph :A :D :C)))
    (is (= 22 (distance graph :A :E :B :C :D)))
    (is (= "NO SUCH ROUTE" (distance graph :A :E :D)))))

(deftest test-graph-trip-count
  (testing "Test trip counts between nodes with speficied max stop"
    (is (= 2 (trip-count graph :C :C 3)))
    (is (= 3 (trip-count-with-exact-stop graph :A :C 4)))))

(deftest test-graph-trip-distance
  (testing "Test graph distance operations"
    (is (= 9 (trip-distance graph :A :C)))
    (is (= 9 (trip-distance graph :B :B)))))

(deftest test-graph-trip-routes
  (testing "Test routes operations with distance params"
    (is (= 7 (trips-with-max-distance graph :C :C 30)))))
