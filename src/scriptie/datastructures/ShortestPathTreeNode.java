package scriptie.datastructures;

import scriptie.Util;
import scriptie.output.dot.LatexRepresentationable;

public class ShortestPathTreeNode<V, E> implements LatexRepresentationable {

	public ShortestPathTreeNode(V correspondingVertex, E correspondingEdge,
			double cost) {
		this.correspondingVertex = correspondingVertex;
		this.correspondingGraphEdgeToParent = correspondingEdge;
		this.cost = cost;
	}

	/**
	 * Vertex in the original graph
	 */
	public V correspondingVertex;
	/**
	 * Edge used in original graph to get the shortest path
	 */
	public E correspondingGraphEdgeToParent;
	/**
	 * Cost from correspondingVertex to the end point in the original graph
	 */
	public Double cost;

	@Override
	public String toString() {
		return "V: " + correspondingVertex.toString() + " c: "
				+ Double.toString(cost);
	}

	@Override
	public String getLatexRepresentation() {
		return Util.getEnsureMathString(String.format("%s, %s",
				correspondingVertex.toString(),
				Util.latexDecimalFormat.format(cost)));
	}
}