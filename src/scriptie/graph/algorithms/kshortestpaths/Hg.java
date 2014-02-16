package scriptie.graph.algorithms.kshortestpaths;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jgraph.graph.DefaultGraphModel.EmptyIterator;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.ITree;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.GraphDotGraph;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import net.datastructures.EmptyListException;
import net.datastructures.EmptyPriorityQueueException;
import net.datastructures.Entry;
import net.datastructures.HeapPriorityQueue;
import net.datastructures.InvalidKeyException;
import net.datastructures.PriorityQueue;

public class Hg<V, E extends DebugDefaultWeightedEdge> extends
		AbstractIterator<HgHeapNode<V, E>> implements
		PriorityQueue<Double, HgHeapNode<V, E>>, Iterable<HgHeapNode<V, E>> {

	Pg<V, E> pg;
	HgHeapNode<V, E> root;
	IPgGraphNode<V, E> currentParent;
	HeapPriorityQueue<Double, HgHeapNode<V, E>> currentChildren;

	public Hg(Pg<V, E> pg) {
		this.pg = pg;
		this.currentChildren =
				new HeapPriorityQueue<Double, HgHeapNode<V, E>>();
		// Set pgRootNode as current node;
		this.root =
				new HgHeapNode<V, E>(
						pg.rootNode,
						new GraphPathImpl<IPgGraphNode<V, E>, DebugDefaultWeightedEdge>(
								pg,
								pg.rootNode,
								pg.rootNode,
								Collections
										.<DebugDefaultWeightedEdge> emptyList(),
								0));
		// currentChildren.insert(root.getKey(), root);
	}

	@Override
	public boolean isEmpty() {
		return currentChildren.isEmpty()
				&& pg.outDegreeOf(root.pgGraphNode) == 0;
	}

	@Override
	public Entry<Double, HgHeapNode<V, E>> min()
			throws EmptyPriorityQueueException {
		return root;
	}

	@Override
	public Entry<Double, HgHeapNode<V, E>> removeMin()
			throws EmptyPriorityQueueException {
		// get the current root and save a reference to return it once
		// the new root has been set
		Entry<Double, HgHeapNode<V, E>> retVal = min();
		if (!currentChildren.isEmpty()) {
			root = currentChildren.removeMin().getValue();
			// Add the children of the new root
			addChildrenToCurrentChildren(root);
		} else {
			if (addChildrenToCurrentChildren(retVal.getValue())) {
				return removeMin();
			} else {
				// end reached
				endOfData();
			}
		}
		return retVal;
	}

	private boolean addChildrenToCurrentChildren(HgHeapNode<V, E> parent) {
		boolean itemsAdded = false;
		for (DebugDefaultWeightedEdge edge : pg
				.outgoingEdgesOf(parent.pgGraphNode)) {
			IPgGraphNode<V, E> vertex =
					Graphs.getOppositeVertex(pg, edge, parent.pgGraphNode);

			List<DebugDefaultWeightedEdge> newEdgeList =
					Lists.newArrayList(parent.pgPathUntilHere.getEdgeList());

			newEdgeList.add(edge);
			GraphPath<IPgGraphNode<V, E>, DebugDefaultWeightedEdge> newPath =
					new GraphPathImpl<IPgGraphNode<V, E>, DebugDefaultWeightedEdge>(
							parent.pgPathUntilHere.getGraph(),
							parent.pgPathUntilHere.getStartVertex(), vertex,
							newEdgeList, parent.pgPathUntilHere.getWeight()
									+ pg.getEdgeWeight(edge));

			HgHeapNode<V, E> newNode = new HgHeapNode<V, E>(vertex, newPath);
			currentChildren.insert(newNode.getKey(), newNode);
			itemsAdded = true;
		}
		return itemsAdded;
	}

	@Override
	protected HgHeapNode<V, E> computeNext() {
		return removeMin().getValue();
	}

	@Override
	public Iterator<HgHeapNode<V, E>> iterator() {
		return this;
	}

	// Not supported

	/**
	 * Not Supported
	 * 
	 * @deprecated Not Supported
	 */
	@Override
	@Deprecated
	public Entry<Double, HgHeapNode<V, E>> insert(Double key,
			HgHeapNode<V, E> value) throws InvalidKeyException {
		throw new NotImplementedException();
	}

	/**
	 * Not Supported, do not use.
	 * 
	 * @deprecated Not Supported
	 */
	@Override
	@Deprecated
	public int size() {
		throw new NotImplementedException();
	}
}
