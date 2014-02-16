package scriptie.graph.timetable;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.graph.timetable.costs.EdgeCostCollection;

public class TripWeightedEdge extends DebugDefaultWeightedEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TripWeightedEdge(EdgeCostCollection costs, Wait wait) {
		Costs = costs;
		Wait = wait;
	}

	public EdgeCostCollection Costs;
	public Wait Wait;

	@Override
	protected double getWeight() {
		return Costs.getTotalCosts();
	}

	public enum Wait {
		Transfer, Wait
	}

	@Override
	public String toString() {
		return String.format("%s, %s:%s", Wait.toString().substring(0, 1)
				.toLowerCase(), getWeight(), Costs);
	}
}
