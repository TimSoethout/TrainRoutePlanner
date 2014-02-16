package scriptie.graph.timetable;

import scriptie.graph.timetable.costs.EdgeCostCollection;

public class PathsTripWeightedEdge extends TripWeightedEdge {

	public PathsTripWeightedEdge(EdgeCostCollection costs,
			scriptie.graph.timetable.TripWeightedEdge.Wait wait) {
		super(costs, wait);
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

}
