package scriptie.datastructures;

import net.datastructures.CompleteBinaryTree;
import net.datastructures.Entry;
import net.datastructures.NodePositionList;
import net.datastructures.Position;
import net.datastructures.TreeNode;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.graph.algorithms.kshortestpaths.HoutHeapNodeValue;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class PositionWrapperTreeNode<V, E extends DebugDefaultWeightedEdge>
		extends TreeNode<Entry<Double, HoutHeapNodeValue<V, E>>> {
	// public PositionWrapperTreeNode(Position<E> position) {
	// setElement(position.element());
	// }

	/**
	 * 
	 */
	public PositionWrapperTreeNode(
			final ITree<Entry<Double, HoutHeapNodeValue<V, E>>> tree,
			Entry<Double, HoutHeapNodeValue<V, E>> entry) {
		// First set the entry in the element
		setElement(entry);

		// Create a list for storing the child nodes
		NodePositionList<Position<Entry<Double, HoutHeapNodeValue<V, E>>>> nodePositionList =
				new NodePositionList<Position<Entry<Double, HoutHeapNodeValue<V, E>>>>();

		// loop over all children of the item corresponding to the given
		// entry to construct the rest of the tree
		for (PositionWrapperTreeNode<V, E> item : Iterables
				.transform(
						tree.getChildren(entry),
						new Function<Entry<Double, HoutHeapNodeValue<V, E>>, PositionWrapperTreeNode<V, E>>() {
							@Override
							public PositionWrapperTreeNode<V, E> apply(
									Entry<Double, HoutHeapNodeValue<V, E>> from) {
								return new PositionWrapperTreeNode<V, E>(tree,
										from);
							};
						})) {
			nodePositionList.addLast(item);
		}
		// Add the child nodes
		setChildren(nodePositionList);
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", this.element(), this.getChildren());
	}
}