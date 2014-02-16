package scriptie.datastructures;

import net.datastructures.CompleteBinaryTree;
import net.datastructures.Entry;
import net.datastructures.HeapPriorityQueue;
import net.datastructures.Position;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Heap<K, V> extends HeapPriorityQueue<K, V> implements Cloneable,
		ITree<Entry<K, V>> {
	// public KeyValuePair<K, V> rootNode;
	public Heap() {
	}

	public CompleteBinaryTree<Entry<K, V>> getTree() {

		return this.heap;
	}

	@Override
	public Iterable<Entry<K, V>> getChildren(final Entry<K, V> node) {
		CompleteBinaryTree<Entry<K, V>> tree = this.getTree();

		// Get Position of the Entry
		Position<Entry<K, V>> nodePosition =
				Iterables.find(tree.positions(),
						new Predicate<Position<Entry<K, V>>>() {
							@Override
							public boolean apply(Position<Entry<K, V>> input) {
								return input.element().getValue() == node
										.getValue();
							}
						});
		// Lookup Node children with Position, then convert the list of
		// Positions to Entries
		return Iterables.transform(tree.children(nodePosition),
				new Function<Position<Entry<K, V>>, Entry<K, V>>() {
					@Override
					public Entry<K, V> apply(Position<Entry<K, V>> from) {
						return from.element();
					}
				});
	}

	// public String getTreeString() {
	// StringBuilder builder = new StringBuilder();
	// Position<Entry<K, V>> root= heap.root();
	// builder.append(root);
	// builder.append(heap.children(root));
	//
	// return builder.toString();
	// }

	@Override
	public Heap<K, V> clone() throws CloneNotSupportedException {
		Heap<K, V> clone = new Heap<K, V>();

		for (Position<Entry<K, V>> position : this.getTree().positions()) {
			clone.insert(position.element().getKey(), position.element()
					.getValue());
		}

		return clone;
	}

	@Override
	public Entry<K, V> getRootNode() {
		return heap.root().element();
	}
}
