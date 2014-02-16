package scriptie.graph.timetable.costs;

import scriptie.graph.timetable.TripWeightedEdge.Wait;

public class HopEdgeCost extends EdgeCost {

	private boolean transfer;

	public HopEdgeCost(double edgeCostWeight, Wait wait) {
		this(edgeCostWeight, wait == Wait.Transfer);
	}

	public HopEdgeCost(double edgeCostWeight, boolean transfer) {
		super(edgeCostWeight);
		this.transfer = transfer;
	}

	@Override
	public int getEdgeCost() {
		return transfer ? 1 : 0;
	}

	@Override
	public String toString() {
		return String.valueOf(getEdgeCost());
	}

}
