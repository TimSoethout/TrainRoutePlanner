package scriptie.graph.timetable.costs;

public class NoCostEdgeCost extends EdgeCost {

	public NoCostEdgeCost() {
		this(0);
	}
	
	public NoCostEdgeCost(double edgeCostWeight) {
		super(edgeCostWeight);
	}

	@Override
	public int getEdgeCost() {
		return 0;
	}

	@Override
	public String toString() {
		return "0";
	}

}
