(ns tiny-graph.core-test
  (:require [clojure.test :refer :all]
            [tiny-graph.core :refer :all]))

(def graph (atom {}))
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

(deftest test-graph-init
  (testing "Test adding of nodes and edges to graph"
    (is (= 5 (count @graph)))))

(deftest test-graph-distance-count
  (testing "Test node distance count"
    (is (= 5 (distance @graph :A :B)))
    (is (= 9 (distance @graph :A :B :C)))
    (is (= 22 (distance @graph :A :E :B :C :D)))
    (is (= "NO SUCH ROUTE" (distance @graph :A :E :D)))))

(deftest test-graph-trip-count
  (testing "Test trip counts between nodes with speficied max stop"

    ; Answers question 6
    (is (= 2 (trip-count @graph :C :C 3)))

    ; Answers question 7
    (is (= 3 (trip-count-with-exact-stop @graph :A :C 4)))))

(deftest test-graph-trip-distance
  (testing "Test graph distance operations"
    ; Answers question 8
    (is (= 9 (trip-distance @graph :A :C)))
    
    ; Answers question 9
    (is (= 9 (trip-distance @graph :B :B)))
    ))

(deftest test-graph-trip-routes
  (testing "Test routes operations with distance params"
    (is (= 7 (trips-with-max-distance @graph :C :C 30)))))
