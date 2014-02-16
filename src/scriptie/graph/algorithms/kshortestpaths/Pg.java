package scriptie.graph.algorithms.kshortestpaths;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.datastructures.Entry;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.HtHeapNode;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.GraphDotGraph;
import scriptie.output.dot.DotGraphable;

public class Pg<V, E extends DebugDefaultWeightedEdge>
		extends
		DefaultDirectedWeightedGraph<IPgGraphNode<V, E>, DebugDefaultWeightedEdge>
		implements DotGraphable {

	public IPgGraphNode<V, E> rootNode;
	private KShortestPaths<V, E> supervisor;

	/**
	 * Constructs P(G)
	 * 
	 * @param dg
	 *            D(G) from which P(G) should be constructed
	 * @param s
	 *            P(G) contains nodes reachable from s
	 */
	public Pg(KShortestPaths<V, E> supervisor, Dg<V, E> dg, V s) {
		super(DebugDefaultWeightedEdge.class);
		this.supervisor = supervisor;

		HtHeapNode<Double, V, E> htHeapS = dg.hv.get(s);
		PgDgNode<V, E> pgGraphS = null;
		// add all vertices of D(G) to P(G)
		supervisor.logMessage("P(G): add all vertices of D(G) to P(G)");
		int totalCount = dg.vertexSet().size();
		int count = 0;
		for (HtHeapNode<Double, V, E> vertex : dg.vertexSet()) {
			if (count % 100 == 0) {
				supervisor.logMessage(Util.getProgressString(
						"P(G): add all vertices of D(G) to P(G)", totalCount,
						count));
			}
			count++;
			
			PgDgNode<V, E> newNode = new PgDgNode<V, E>(vertex);
			vertex.correspondingPgDgNode = newNode;
			this.addVertex(newNode);
			// Get reference to PgDgNode corresponding to s;
			if (vertex == htHeapS) {
				pgGraphS = newNode;
			}
		}
		

		// try {
		// this.getDotGraph().toFile("output/debug/1PGNodesTest.dot");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// Add r(s)
		supervisor.logMessage("P(G): Add r(s)");
		rootNode = new PgHNode<V, E>(s);
		this.addVertex(rootNode);

		// try {
		// this.getDotGraph().toFile("output/debug/2PGRootsAddedTest.dot");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// Add edges from D(G)
		supervisor.logMessage("P(G): Add edges from D(G)");
		totalCount = dg.edgeSet().size();
		count = 0;
		for (DefaultEdge edge : dg.edgeSet()) {
			if (count % 100 == 0) {
				supervisor.logMessage(Util.getProgressString(
						"P(G): Add edges from D(G)", totalCount, count));
			}
			count++;
			HtHeapNode<Double, V, E> sourceVertex = dg.getEdgeSource(edge);
			HtHeapNode<Double, V, E> targetVertex = dg.getEdgeTarget(edge);

			PgHeapEdge newEdge = new PgHeapEdge();
			this.addEdge(getCorrespondingPgDgNode(sourceVertex),
					getCorrespondingPgDgNode(targetVertex), newEdge);
			this.setEdgeWeight(newEdge,
					targetVertex.getKey() - sourceVertex.getKey());
		}

		// try {
		// this.getDotGraph().toFile(
		// "output/debug/3PGEdges from D(G) Test.dot");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// Add edges from G-T
		supervisor.logMessage("P(G): Add edges from G-T");
		count = 0;
		totalCount = -1;
		for (PgDgNode<V, E> vertex : (Iterable<PgDgNode<V, E>>) Iterables
				.filter(this.vertexSet(), new PgDgNode<V, E>().getClass())) {
			if (count % 100 == 0) {
				supervisor.logMessage(Util.getProgressString(
						"P(G): Add edges from G-T", totalCount, count));
			}
			count++;

			E correspondingEdge =
					vertex.correspondingHtHeapNode.wrappedEntry.getValue().correspondingEdge;
			V w = supervisor.graph.getEdgeTarget(correspondingEdge);
			HtHeapNode<Double, V, E> correspondingHtNode = dg.hv.get(w);
			if (correspondingHtNode != null) {
				PgCrossEdge newEdge = new PgCrossEdge();
				this.addEdge(vertex,
						getCorrespondingPgDgNode(correspondingHtNode), newEdge);
				// Get the delta of the edge corresponding to h(w)
				this.setEdgeWeight(newEdge,
						supervisor.delta(correspondingHtNode.wrappedEntry
								.getValue().correspondingEdge));
			}
		}

		// try {
		// this.getDotGraph().toFile(
		// "output/debug/4PGEdgesCorrespondingG-TTest.dot");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// Add r -> h(s)
		supervisor.logMessage("P(G): Add r -> h(s)");
		if (pgGraphS != null) {
			DebugDefaultWeightedEdge rootEdge = new DebugDefaultWeightedEdge();
			this.addEdge(rootNode, pgGraphS, rootEdge);
			this.setEdgeWeight(rootEdge, supervisor.delta(htHeapS.wrappedEntry
					.getValue().correspondingEdge));
		}
		else {
			supervisor.logMessage("s is not connected to the shortest path tree: s= " + s);
			//throw new IllegalArgumentException("argument s is out of range");
		}
		
		// try {
		// this.getDotGraph().toFile(
		// "output/debug/5PGAddRootConnectionTest.dot");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private PgDgNode<V, E> getCorrespondingPgDgNode(
			HtHeapNode<Double, V, E> nodeToFind) {
		// @SuppressWarnings("unchecked")
		// // Always the case. Can't be done type safe
		// Iterable<PgDgNode<V, E>> dgNodes =
		// (Iterable<PgDgNode<V, E>>) Iterables.filter(this.vertexSet(),
		// new PgDgNode<V, E>().getClass());

		// TODO: more efficient
		// return Iterables.find(dgNodes, new Predicate<PgDgNode<V, E>>() {
		// @Override
		// public boolean apply(PgDgNode<V, E> input) {
		// return input.correspondingHtHeapNode == nodeToFind;
		// }
		// }, null);
		if (nodeToFind.correspondingPgDgNode != null) {
			return nodeToFind.correspondingPgDgNode;
		} else {
			throw new NullPointerException("nodeToFind.correspondingPgDgNode");
		}
		// return correspondingPgDgNodes.get(nodeToFind);
	}

	@Override
	public DotGraph getDotGraph() {
		GraphDotGraph<IPgGraphNode<V, E>, DebugDefaultWeightedEdge> dot =
				new GraphDotGraph<IPgGraphNode<V, E>, DebugDefaultWeightedEdge>(
						"P(G)", this);
		return dot.getDotGraph();
	}

	@Override
	public String toString() {
		return String.format("\tGraph: %s", super.toString());
	}
}
