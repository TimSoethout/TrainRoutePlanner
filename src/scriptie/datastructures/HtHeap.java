package scriptie.datastructures;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import scriptie.graph.algorithms.kshortestpaths.HoutHeapNodeValue;
import net.datastructures.CompleteBinaryTree;
import net.datastructures.Entry;
import net.datastructures.HeapPriorityQueue;
import net.datastructures.Position;

public class HtHeap<V, E extends DebugDefaultWeightedEdge> extends
		HtHeapPriorityQueue<Double, V, E> implements Cloneable,
		ITree<HtHeapNode<Double, V, E>> {

	public CompleteBinaryTree<HtHeapNode<Double, V, E>> getTree() {
		return this.heap;
	}

	@Override
	public HtHeap<V, E> clone() throws CloneNotSupportedException {
		HtHeap<V, E> clone = new HtHeap<V, E>();

		for (Position<HtHeapNode<Double, V, E>> position : this.heap
				.positions()) {
			HtHeapNode<Double, V, E> newNode =
					clone.insert(position.element().getKey(), position
							.element().getValue());
			// unmark all positions (clean heap)
			newNode.marked = false;
			// if the original position was marked (i.e. new) update the reference to it.
			// else link to it's original node
			if (position.element().marked) {
				newNode.originalHtHeapNode = position.element();
			} else {
				newNode.originalHtHeapNode = position.element().originalHtHeapNode;
			}
		}

		return clone;
	}

	@Override
	public Iterable<HtHeapNode<Double, V, E>> getChildren(
			final HtHeapNode<Double, V, E> node) {
		// Get Position of the Entry
		Position<HtHeapNode<Double, V, E>> nodePosition =
				Iterables.find(heap.positions(),
						new Predicate<Position<HtHeapNode<Double, V, E>>>() {
							@Override
							public boolean apply(
									Position<HtHeapNode<Double, V, E>> input) {
								return input.element() == node;
							}
						});
		// Lookup Node children with Position, then convert the list of
		// Positions to Entries
		return Iterables
				.transform(
						heap.children(nodePosition),
						new Function<Position<HtHeapNode<Double, V, E>>, HtHeapNode<Double, V, E>>() {
							@Override
							public HtHeapNode<Double, V, E> apply(
									Position<HtHeapNode<Double, V, E>> from) {
								return from.element();
							}
						});
	}

	@Override
	public HtHeapNode<Double, V, E> getRootNode() {
		return heap.root().element();
	}

}
