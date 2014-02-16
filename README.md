TrainRoutePlanner
=================

Source code from my Bachelor thesis project written in 2010/2011. (http://dspace.library.uu.nl/handle/1874/237381)

Contains a parser from NS (Dutch royal railways) data exports to a graph model of train timetables. This representations takes into account a couple of metrics such as waiting on stations.
Data sets are not included for legal purposes...

Supports three algorithms to calculate the quickest route from A to B:
- Dijkstra
- Bellman Ford
- k Shortest Paths (O(m + n log n + k)) (http://epubs.siam.org/doi/abs/10.1137/S0097539795290477)

For the latest a complete implementation including intermittent output to graphviz graphs for use in the report is provided.
