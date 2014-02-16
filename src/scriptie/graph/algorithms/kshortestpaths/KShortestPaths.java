package scriptie.graph.algorithms.kshortestpaths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgraph.graph.DefaultGraphModel.EmptyIterator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphPathImpl;

import scriptie.Listener;
import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.ShortestPathTree;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class KShortestPaths<V, E extends DebugDefaultWeightedEdge> {

	public DirectedGraph<V, E> graph;
	private V startVertex;
	private V endVertex;
	public ShortestPathTree<V, E> shortestPathTree;

	private Map<V, Ht<V, E>> hts;
	private Map<V, Hout<V, E>> houts;

	private Dg<V, E> dg;
	private Pg<V, E> pg;
	private Hg<V, E> hg;

	public Listener listener = null;

	public void logMessage(String message) {
		if (listener != null) {
			listener.logMessage(message);
		}
	}

	public KShortestPaths(DirectedGraph<V, E> graph, V startVertex, V endVertex) {
		this(graph, startVertex, endVertex, null);
	}

	public KShortestPaths(DirectedGraph<V, E> graph, V startVertex,
			V endVertex, Listener listener) {
		this.graph = graph;
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.listener = listener;

		hts = new HashMap<V, Ht<V, E>>();
		houts = new HashMap<V, Hout<V, E>>();
		logMessage("Begin generating Shortest Path Tree");
		shortestPathTree = new ShortestPathTree<V, E>(graph, endVertex);
		logMessage("Finished generating Shortest Path Tree");

		logMessage("Begin generating D(G)");
		dg = new Dg<V, E>(graph, startVertex, endVertex, this);
		logMessage("Finished generating D(G)");
		logMessage("Begin generating P(G)");
		pg = new Pg<V, E>(this, dg, startVertex);
		logMessage("Finished generating P(G)");
		logMessage("Begin generating H(G)");
		hg = new Hg<V, E>(pg);
		logMessage("Finished generating H(G)");
	}

	/**
	 * Returns the k shortest paths in a list
	 * 
	 * @return the k shortest paths
	 */
	public Iterable<GraphPath<V, E>> getPaths(int k) {
		return Iterables.transform(Iterables.limit(hg, k),
				new Function<HgHeapNode<V, E>, GraphPath<V, E>>() {

					@Override
					public GraphPath<V, E> apply(HgHeapNode<V, E> input) {
						// System.out.println("pgpathuntilhere: "
						// + input.pgPathUntilHere + "\n");
						// System.out.println("key: " + input.getKey() + "\n");
						return shortestPathTree.getShortestPath(startVertex,
								getPathSeqFromPg(input.pgPathUntilHere));
					}
				});
	}

	/**
	 * The out(vertex) (see Eppstein)
	 * 
	 * @param vertex
	 * @return out(vertex)
	 */
	public Iterable<E> out(final V vertex) {
		Set<E> edges = graph.outgoingEdgesOf(vertex);
		Iterable<E> filteredEdges = Iterables.filter(edges, new Predicate<E>() {
			private E correspondingEdge = shortestPathTree.treeNodes
					.get(vertex).correspondingGraphEdgeToParent;

			@Override
			public boolean apply(E input) {
				return !input.equals(correspondingEdge);
			}
		});
		return filteredEdges;
		// Iterables.transform(filteredEdges, new Function<E, Double>() {
		// @Override
		// public Double apply(E from) {
		// return new delta(from);
		// }
		// });
	}

	/**
	 * Returns the delta of the edge (see Eppstein)
	 * 
	 * @param edge
	 * @return delta(e)
	 */
	public double delta(E edge) {
		return KShortestPaths.delta(graph, shortestPathTree, edge);
	}

	/**
	 * Returns the delta of the edge (see Eppstein) Static version used for
	 * displaying
	 * 
	 * @param edge
	 * @return delta(e)
	 */
	public static <V, E extends DefaultWeightedEdge> Double delta(
			DirectedGraph<V, E> graph, ShortestPathTree<V, E> shortestPathTree,
			E edge) {
		// delta(e) = l(e) + d(head(e),t) + d(tail(e),t)
		V head = graph.getEdgeTarget(edge);
		V tail = graph.getEdgeSource(edge);
		double headCost = shortestPathTree.treeNodes.get(head).cost;
		double tailCost = shortestPathTree.treeNodes.get(tail).cost;
		return graph.getEdgeWeight(edge) + headCost - tailCost;
	}

	/**
	 * Gets the Ht for given vertex, if it isn't already exist it will be
	 * constructed
	 * 
	 * @param vertex
	 * @return Ht(vertex)
	 */
	public Ht<V, E> getHt(V vertex) {
		if (hts.containsKey(vertex)) {
			return hts.get(vertex);
		} else {

			Ht<V, E> ht = new Ht<V, E>(this, vertex);
			hts.put(vertex, ht);
			return ht;
		}
	}

	/**
	 * Gets the Hout for given vertex, if it isn't already exist it will be
	 * constructed
	 * 
	 * @param vertex
	 * @return Hout(vertex)
	 */
	public Hout<V, E> getHout(V vertex) {
		if (vertex == null) {
			throw new NullPointerException("vertex");
		}
		if (houts.containsKey(vertex)) {
			return houts.get(vertex);
		} else {
			Hout<V, E> ht = new Hout<V, E>(this, vertex);
			houts.put(vertex, ht);
			return ht;
		}
	}

	/**
	 * Construct patseq in G from a path p' in P(G)
	 * 
	 * @param pgPath
	 *            p' (see Eppstein)
	 * @return
	 */
	public Iterable<E> getPathSeqFromPg(
			final GraphPath<IPgGraphNode<V, E>, DebugDefaultWeightedEdge> pgPath) {
		if (pgPath.getEdgeList().isEmpty()) {
			// pgPath is empty, pathseq is also empty
			return Collections.<E> emptyList();
		}

		LinkedList<E> pathseq = new LinkedList<E>();

		Iterables.addAll(pathseq, (Iterables.transform(
				Iterables.filter(pgPath.getEdgeList(), PgCrossEdge.class),
				new Function<DebugDefaultWeightedEdge, E>() {
					@Override
					public E apply(DebugDefaultWeightedEdge input) {
						return ((PgDgNode<V, E>) pgPath.getGraph()
								.getEdgeSource(input)).correspondingHtHeapNode.wrappedEntry
								.getValue().correspondingEdge;
					}
				})));

		pathseq.add(((PgDgNode<V, E>) pgPath.getEndVertex()).correspondingHtHeapNode.wrappedEntry
				.getValue().correspondingEdge);
		return pathseq;
	}
}
