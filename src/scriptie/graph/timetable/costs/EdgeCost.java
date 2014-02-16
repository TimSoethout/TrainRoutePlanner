package scriptie.graph.timetable.costs;

import java.io.Serializable;

public abstract class EdgeCost implements Serializable {
	protected double edgeCostWeight = 0;

	public EdgeCost(double edgeCostWeight) {
		setEdgeCostWeight(edgeCostWeight);
	}

	/**
	 * Get a weight >= 0 and <= 1
	 * 
	 * @return The weight this edge should be given.
	 */
	public double getEdgeCostWeight() {
		return edgeCostWeight;
	}

	public void setEdgeCostWeight(double value) {
		if(value > 1 || value < 0) {
			throw new IllegalArgumentException("value must be between 0 and 1");
		}
		edgeCostWeight = value;
	}

	/**
	 * Gets the edge cost according to the implementation.
	 * 
	 * @return The edge cost.
	 */
	public abstract int getEdgeCost();

	public int getWeightedEdgeCost() {
		return (int) (edgeCostWeight * getEdgeCost());
	}

	@Override
	public abstract String toString();
}
