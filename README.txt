README


1. Design Decision and Graph Datastructure.

"It is better to have 100 functions operate on one data structure than to have 10 functions operate on 10 data structures."
  - Alan J. Perlis

Clojure community has always being proud of the idiomatic concept of one simple data structure and many functions manipulating the data rather than many data structures with many functions to operate on them.

The graph is modeled as a simple map of map of edges with the distance (weight) as integer value of the inner map.
See below.

 {:A {:B 5 :D 5 :E 7} 
  :B {:C 4} 
  :C {:D 8 :E 2} 
  :D {:C 8 :E 6} 
  :E {:B 3} }

One of the most important functions are to trace the root node to destination node with provision for cyclic trip.
We need to specif the max number of 'hop' to prevent infinite loop from the cyclic trip.
For example CDC, CEBC, CEBCDC, CDCEBC, CDEBC, CEBCEBC, CEBCEBCEBC.

The other function is to count the distance or weight betweenn nodes.

With these two important functions working, we can then easily obtain data about the trips and distance by combining the basic functions.


2. How to Run 

This project depends on lein. See https://leiningen.org.
From the root of the project, run 'lein repl' and then execute '(run)'.
The '(run)' function will simulate the graph processing and answers the ten questions.

By default the program expects 'input.txt' to be available in the project root directory which contains the graph data in the format of:
 
 Graph: AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7


3. Test Case
The test cases are located in test/tiny-graph/core_test.clj and contains all the tests to validate the public functions.

4. TODOS
- Use of atom and mutable is can and should be removed. Atom was used to simplified state tracking and focus on the graph algorithm
