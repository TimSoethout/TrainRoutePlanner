package scriptie.graph.algorithms.kshortestpaths;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.output.dot.LatexRepresentationable;

public class HoutHeapNodeValue<V, E extends DebugDefaultWeightedEdge>
		implements Cloneable, LatexRepresentationable {
	/**
	 * Edge in original graph corresponding to this HoutHeapNode.
	 */
	public E correspondingEdge;
	/**
	 * Hout in which this node is contained.
	 */
	public Hout<V, E> hout;

	public boolean marked = false;

	public HoutHeapNodeValue(Hout<V, E> hout, E correspondingEdge) {
		this.correspondingEdge = correspondingEdge;
		this.hout = hout;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		HoutHeapNodeValue<V, E> clone =
				new HoutHeapNodeValue<V, E>(hout, correspondingEdge);
		return clone;
	}

	@Override
	public String toString() {
		return correspondingEdge.toString();
	}

	@Override
	public String getLatexRepresentation() {
		return correspondingEdge.getLatexRepresentation();
	}
}