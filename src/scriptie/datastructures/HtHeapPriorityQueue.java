package scriptie.datastructures;

import java.util.Comparator;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import scriptie.graph.algorithms.kshortestpaths.HoutHeapNodeValue;

import net.datastructures.*;

//begin#fragment HeapPriorityQueue
/**
 * Realization of a priority queue by means of a heap. A complete binary tree
 * realized by means of an array list is used to represent the heap.
 * //end#fragment HeapPriorityQueue
 * 
 * @author Roberto Tamassia, Michael Goodrich, Eric Zamore //begin#fragment
 *         HeapPriorityQueue
 */
public class HtHeapPriorityQueue<K, V, E extends DebugDefaultWeightedEdge>
		implements PriorityQueue<K, HoutHeapNodeValue<V, E>> {
	protected CompleteBinaryTree<HtHeapNode<K, V, E>> heap; // underlying heap
	protected Comparator<K> comp; // comparator for the keys

	/** Creates an empty heap with the default comparator */
	public HtHeapPriorityQueue() {
		heap = new ArrayListCompleteBinaryTree<HtHeapNode<K, V, E>>(); // use an
																		// array
		// list
		comp = new DefaultComparator<K>(); // use the default comparator
	}

	/** Creates an empty heap with the given comparator */
	public HtHeapPriorityQueue(Comparator<K> c) {
		heap = new ArrayListCompleteBinaryTree<HtHeapNode<K, V, E>>();
		comp = c;
	}

	// end#fragment HeapPriorityQueue
	/**
	 * Sets the comparator used for comparing items in the heap.
	 * 
	 * @throws IllegalStateException
	 *             if priority queue is not empty
	 */
	public void setComparator(Comparator<K> c) throws IllegalStateException {
		if (!isEmpty()) // this is only allowed if the priority queue is empty
			throw new IllegalStateException("Priority queue is not empty");
		comp = c;
	}

	// begin#fragment HeapPriorityQueue
	/** Returns the size of the heap */
	public int size() {
		return heap.size();
	}

	/** Returns whether the heap is empty */
	public boolean isEmpty() {
		return heap.size() == 0;
	}

	// end#fragment HeapPriorityQueue
	// begin#fragment mainMethods
	/** Returns but does not remove an entry with minimum key */
	public HtHeapNode<K, V, E> min() throws EmptyPriorityQueueException {
		if (isEmpty())
			throw new EmptyPriorityQueueException("Priority queue is empty");
		return heap.root().element();
	}

	/** Inserts a key-value pair and returns the entry created */
	public HtHeapNode<K, V, E> insert(K k, HoutHeapNodeValue<V, E> x)
			throws InvalidKeyException {
		checkKey(k); // may throw an InvalidKeyException
		HtHeapNode<K, V, E> entry = new HtHeapNode<K, V, E>(k, x);
		Position<HtHeapNode<K, V, E>> position = heap.add(entry);
		upHeap(position);
		markUpwards(position);
		return entry;
	}

	private void markUpwards(Position<HtHeapNode<K, V, E>> entry) {
		entry.element().marked = true;
		if (!heap.isRoot(entry)) {
			Position<HtHeapNode<K, V, E>> parent = heap.parent(entry);
			markUpwards(parent);
		}
	}

	/** Removes and returns an entry with minimum key */
	public HtHeapNode<K, V, E> removeMin() throws EmptyPriorityQueueException {
		if (isEmpty())
			throw new EmptyPriorityQueueException("Priority queue is empty");
		HtHeapNode<K, V, E> min = heap.root().element();
		if (size() == 1)
			heap.remove();
		else {
			heap.replace(heap.root(), heap.remove());
			downHeap(heap.root());
		}
		return min;
	}

	/** Determines whether a given key is valid */
	protected void checkKey(K key) throws InvalidKeyException {
		try {
			comp.compare(key, key);
		} catch (Exception e) {
			throw new InvalidKeyException("Invalid key");
		}
	}

	// end#fragment mainMethods
	// begin#fragment auxiliary
	/** Performs up-heap bubbling */
	protected void upHeap(Position<HtHeapNode<K, V, E>> v) {
		// inserted node is always marked.
		v.element().marked = true;

		Position<HtHeapNode<K, V, E>> u;
		while (!heap.isRoot(v)) {
			u = heap.parent(v);
			if (comp.compare(u.element().getKey(), v.element().getKey()) <= 0)
				break;
			swap(u, v);
			v = u;
		}
	}

	/** Performs down-heap bubbling */
	protected void downHeap(Position<HtHeapNode<K, V, E>> r) {
		while (heap.isInternal(r)) {
			Position<HtHeapNode<K, V, E>> s; // the position of the smaller
												// child
			if (!heap.hasRight(r)) {
				s = heap.left(r);
			} else {
				if (comp.compare(heap.left(r).element().getKey(), heap.right(r)
						.element().getKey()) <= 0) {
					s = heap.left(r);
				} else {
					s = heap.right(r);
				}
			}
			if (comp.compare(s.element().getKey(), r.element().getKey()) < 0) {
				swap(r, s);
				r = s;
			} else
				break;
		}
	}

	/** Swaps the entries of the two given positions */
	protected void swap(Position<HtHeapNode<K, V, E>> x,
			Position<HtHeapNode<K, V, E>> y) {
		// Mark elements for updating
		x.element().marked = true;
		y.element().marked = true;

		HtHeapNode<K, V, E> temp = x.element();
		heap.replace(x, y.element());
		heap.replace(y, temp);
	}

	/** Text visualization for debugging purposes */
	public String toString() {
		return heap.toString();
	}
	// end#fragment auxiliary
}
