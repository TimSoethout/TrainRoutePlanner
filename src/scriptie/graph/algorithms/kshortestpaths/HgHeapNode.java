package scriptie.graph.algorithms.kshortestpaths;

import net.datastructures.Entry;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphPathImpl;

import scriptie.datastructures.DebugDefaultWeightedEdge;

public class HgHeapNode<V, E extends DebugDefaultWeightedEdge> implements
		Entry<Double, HgHeapNode<V, E>> {
	public IPgGraphNode<V, E> pgGraphNode;
	public GraphPath<IPgGraphNode<V, E>, DebugDefaultWeightedEdge> pgPathUntilHere;

	public HgHeapNode(IPgGraphNode<V, E> pgGraphNode,
			GraphPath<IPgGraphNode<V, E>, DebugDefaultWeightedEdge> pgPathUntilHere) {
		this.pgGraphNode = pgGraphNode;
		this.pgPathUntilHere = pgPathUntilHere;
	}

	/**
	 * Returns the weight of the corresponding path in P(G)
	 */
	@Override
	public Double getKey() {
		return pgPathUntilHere.getWeight();
	}

	/**
	 * Returns the current value
	 */
	@Override
	public HgHeapNode<V, E> getValue() {
		return this;
	}

	@Override
	public String toString() {
		return "Cost: " + this.pgPathUntilHere.getWeight() + " PgPath: "
				+ pgPathUntilHere.toString();
	}
}
