package scriptie.graph.algorithms.kshortestpaths;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.HtHeapNode;
import scriptie.output.dot.LatexRepresentationable;

/**
 * IPGraphNode built from a Dg node
 * 
 * @author tim
 * 
 * @param <V>
 *            Vertex in original Graph
 * @param <E>
 *            Edge in original Graph
 */
public class PgDgNode<V, E extends DebugDefaultWeightedEdge> implements
		IPgGraphNode<V, E>, LatexRepresentationable {

	public HtHeapNode<Double, V, E> correspondingHtHeapNode;

	public PgDgNode() {
	}

	public PgDgNode(HtHeapNode<Double, V, E> correspondingHtHeapNode) {
		this.correspondingHtHeapNode = correspondingHtHeapNode;
	}

	@Override
	public String toString() {
		return correspondingHtHeapNode != null ? correspondingHtHeapNode
				.toString() : "Empty PgGraphNode";
	}

	@Override
	public String getLatexRepresentation() {
		return correspondingHtHeapNode != null ? String
				.format("%s, %s", Util.latexDecimalFormat
						.format(correspondingHtHeapNode.getKey()),
						correspondingHtHeapNode.getValue().correspondingEdge
								.getLatexRepresentationFromTo()) : this
				.toString();
	}
}
