package scriptie.datastructures;

import net.datastructures.Entry;
import net.datastructures.HeapPriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.Util;
import scriptie.graph.algorithms.kshortestpaths.HoutHeapNodeValue;
import scriptie.graph.algorithms.kshortestpaths.Ht;
import scriptie.graph.algorithms.kshortestpaths.PgDgNode;
import scriptie.output.dot.LatexRepresentationable;

public class HtHeapNode<K, V, E extends DebugDefaultWeightedEdge> implements
		Entry<K, HoutHeapNodeValue<V, E>>, LatexRepresentationable {

	public HtHeapNode(final K key, final HoutHeapNodeValue<V, E> value) {
		this.wrappedEntry = new Entry<K, HoutHeapNodeValue<V, E>>() {

			@Override
			public K getKey() {
				return key;
			}

			@Override
			public HoutHeapNodeValue<V, E> getValue() {
				return value;
			}
		};
		this.marked = false;
	}

	public HtHeapNode(Entry<K, HoutHeapNodeValue<V, E>> wrappedEntry) {
		this.wrappedEntry = wrappedEntry;
	}

	/**
	 * Entry from Hout which is wrapped.
	 */
	public Entry<K, HoutHeapNodeValue<V, E>> wrappedEntry;
	/**
	 * Specifies if the HtHeapNode is marked (see Eppstein).
	 */
	public boolean marked = false;
	/**
	 * Links to the HtHeapNode in which it was originally created.
	 */
	public HtHeapNode<K, V, E> originalHtHeapNode = null;

	@Override
	public K getKey() {
		return wrappedEntry.getKey();
	}

	@Override
	public HoutHeapNodeValue<V, E> getValue() {
		return wrappedEntry.getValue();
	}

	@Override
	public String toString() {
		return "(" + wrappedEntry.getKey() + "," + wrappedEntry.getValue()
				+ ")" + (marked ? "*" : "");
	}

	public PgDgNode<V, E> correspondingPgDgNode;

	@Override
	public String getLatexRepresentation() {
		Double key = Util.as(Double.class, wrappedEntry.getKey());
		return String.format("\\ensuremath{%s, %s %s}", Util.latexDecimalFormat.format(key), wrappedEntry
				.getValue().getLatexRepresentation(), (marked ? "*" : ""));
	}
}
