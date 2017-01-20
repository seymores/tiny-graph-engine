# tiny-graph

Tiny toy directed graph engine.

## Usage

```clojure
(add-node graph "A")
(add-node graph "B")
(add-node graph "C")
(add-node graph "D")
(add-edge graph "A" "B" 5)
(add-edge graph "B" "C" 4)
(add-edge graph "C" "D" 9)

(route "A" "D")
((:B :C 4) (:C :D 9))

```

## License

Copyright © 2017 Ping 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
