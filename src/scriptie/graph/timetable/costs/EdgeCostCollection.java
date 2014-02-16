package scriptie.graph.timetable.costs;

import java.util.ArrayList;

public class EdgeCostCollection extends ArrayList<EdgeCost>  {

	private static final long serialVersionUID = 1L;

	public int getTotalCosts() {
		int total = 0;
		for (EdgeCost cost : this) {
			total += cost.getWeightedEdgeCost();
		}
		return total;
	}
}
