package scriptie.datastructures;

import scriptie.graph.algorithms.kshortestpaths.HoutHeapNodeValue;
import net.datastructures.CompleteBinaryTree;
import net.datastructures.Entry;
import net.datastructures.InvalidPositionException;
import net.datastructures.NonEmptyTreeException;
import net.datastructures.Position;
import net.datastructures.PositionList;
import net.datastructures.TreePosition;

public class LinkedTree<E> extends net.datastructures.LinkedTree<E> {
	/** Adds a root node to an empty tree */
	public Position<E> addRoot(TreePosition<E> e) throws NonEmptyTreeException {
		if (!isEmpty())
			throw new NonEmptyTreeException("Tree already has a root");
		size = 1; // not per se correct, but workable
		root = e;
		return root;
	}

	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * Override because of bug in net.datastructures.LinkedTree. Always throws
	 * Exception in original version.
	 */
	@Override
	protected void preorderPositions(Position<E> v,
			PositionList<Position<E>> pos) throws InvalidPositionException {
		pos.addLast(v);
		if (!isExternal(v)) {
			for (Position<E> w : children(v))
				preorderPositions(w, pos); // recurse on each child
		}
	}

	/**
	 * Override to prevent InvalidPositionException being thrown. Now returns an
	 * empty Iterable if no children.
	 */
	@Override
	public Iterable<Position<E>> children(Position<E> v)
			throws InvalidPositionException {
		TreePosition<E> vv = checkPosition(v);
		return vv.getChildren();
	}

	public Position<E> lookupPosition(E entry) {
		for (Position<E> child : this.positions()) {
			if (child.element().equals(entry)) {
				return child;
			}
		}
		throw new IndexOutOfBoundsException(String.format(
				"Item %s not found in tree %s", entry, this));
	}
}
