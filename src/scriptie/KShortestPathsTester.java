package scriptie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.ClosestFirstIterator;

import scriptie.datastructures.ShortestPathTree;
import scriptie.datastructures.ITree;
import scriptie.datastructures.ShortestPathTreeNode;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.graph.algorithms.kshortestpaths.Dg;
import scriptie.graph.algorithms.kshortestpaths.Hg;
import scriptie.graph.algorithms.kshortestpaths.HgHeapNode;
import scriptie.graph.algorithms.kshortestpaths.Hout;
import scriptie.graph.algorithms.kshortestpaths.Ht;
import scriptie.graph.algorithms.kshortestpaths.IPgGraphNode;
import scriptie.graph.algorithms.kshortestpaths.KShortestPaths;
import scriptie.graph.algorithms.kshortestpaths.Pg;
import scriptie.graph.timetable.TripVertex;
import scriptie.graph.timetable.TripWeightedEdge;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.GraphDotGraph;
import scriptie.output.dot.TreeDotGraph;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractLinkedIterator;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class KShortestPathsTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// eppSteinGraph1();
		// eppsteinHout();
		// scriptieGraph1();
		// scriptieGraph2();
		eppsteinHout2();
	}

	private static void eppSteinGraph1() {
		final DefaultDirectedWeightedGraph<Integer, DebugDefaultWeightedEdge> graph =
				new DefaultDirectedWeightedGraph<Integer, DebugDefaultWeightedEdge>(
						DebugDefaultWeightedEdge.class);
		for (int i = 0; i < 12; i++) {
			graph.addVertex(i);
		}

		addEdge(graph, 0, 1, 2);
		addEdge(graph, 1, 2, 20);
		addEdge(graph, 2, 3, 14);
		addEdge(graph, 0, 4, 13);
		addEdge(graph, 1, 5, 27);
		addEdge(graph, 2, 6, 14);
		addEdge(graph, 3, 7, 15);

		addEdge(graph, 4, 5, 9);
		addEdge(graph, 5, 6, 10);
		addEdge(graph, 6, 7, 25);
		addEdge(graph, 4, 8, 15);
		addEdge(graph, 5, 9, 20);
		addEdge(graph, 6, 10, 12);
		addEdge(graph, 7, 11, 7);

		addEdge(graph, 8, 9, 18);
		addEdge(graph, 9, 10, 8);
		addEdge(graph, 10, 11, 11);

		printGraphInfo(graph, "ShortestPathTree", 0, 11, 10);
	}

	private static void scriptieGraph1() {
		final DefaultDirectedWeightedGraph<Character, DebugDefaultWeightedEdge> graph =
				new DefaultDirectedWeightedGraph<Character, DebugDefaultWeightedEdge>(
						DebugDefaultWeightedEdge.class);

		graph.addVertex('s');
		graph.addVertex('t');
		graph.addVertex('a');
		graph.addVertex('b');
		graph.addVertex('c');
		graph.addVertex('d');
		graph.addVertex('e');
		graph.addVertex('f');
		graph.addVertex('g');

		addEdge(graph, 's', 'a', 1);
		addEdge(graph, 'a', 'b', 2);

		addEdge(graph, 's', 'c', 3);
		addEdge(graph, 'a', 'd', 4);
		addEdge(graph, 'b', 'e', 5);

		addEdge(graph, 'c', 'd', 6);
		addEdge(graph, 'd', 'e', 7);

		addEdge(graph, 'c', 'f', 8);
		addEdge(graph, 'd', 'g', 9);
		addEdge(graph, 'e', 't', 10);

		addEdge(graph, 'f', 'g', 11);
		addEdge(graph, 'g', 't', 12);

		printGraphInfo(graph, "Scriptie", 's', 't', 5);
	}

	private static void scriptieGraph2() {
		final DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge> graph =
				new DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge>(
						DebugDefaultWeightedEdge.class);

		graph.addVertex('s');
		graph.addVertex('t');
		graph.addVertex('a');
		graph.addVertex('b');
		graph.addVertex('c');
		graph.addVertex('d');

		addEdge(graph, 's', 'a', 1);
		addEdge(graph, 's', 'a', 11);
		addEdge(graph, 's', 'b', 3);
		addEdge(graph, 's', 'd', 9);
		addEdge(graph, 's', 'b', 5);

		addEdge(graph, 'a', 'd', 6);

		addEdge(graph, 'b', 'a', 12);
		addEdge(graph, 'b', 'b', 2);
		addEdge(graph, 'b', 'd', 7);
		addEdge(graph, 'b', 'd', 8);

		addEdge(graph, 'c', 't', 10);

		addEdge(graph, 'd', 't', 4);
		addEdge(graph, 'd', 't', 13);

		printGraphInfo(graph, "Scriptie2", 's', 't', 10);
	}

	public static <V, E extends DebugDefaultWeightedEdge> void printGraphInfo(
			final DirectedGraph<V, E> graph, final String graphOutputName,
			final V from, V to, int k) {
		System.out.println("------------ " + graphOutputName);

		System.out.println("Graph: " + graph);
		try {
			new GraphDotGraph<V, E>(graphOutputName + "TestGraph", graph)
					.getDotGraph().toFile(
							"output/" + graphOutputName + "TestGraph.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}

		final KShortestPaths<V, E> kshortest =
				new KShortestPaths<V, E>(graph, from, to);

		ShortestPathTree<V, E> tree = kshortest.shortestPathTree;
		System.out.println("Shortest Path Tree: " + tree);

		try {
			new TreeDotGraph<ShortestPathTreeNode<V, E>>(graphOutputName
					+ "TreeTest", (ITree<ShortestPathTreeNode<V, E>>) tree)
					.toFile("output/" + graphOutputName + "TreeTest.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}
		GraphPath<V, E> shortestPath = tree.getShortestPath(from);
		System.out.println("\tShortest Path: " + shortestPath);

		System.out.println("out(v):");
		List<DotGraph> houts = new ArrayList<DotGraph>();
		List<DotGraph> hts = new ArrayList<DotGraph>();

		for (ShortestPathTreeNode<V, E> shortestPathTreeNode : tree
				.getTreeNodes()) {
			V i = shortestPathTreeNode.correspondingVertex;
			System.out.println(i);
			Iterable<E> outs = kshortest.out(i);

			if (outs.iterator().hasNext()) {
				System.out.println("\t" + outs
						+ Iterables.transform(outs, new Function<E, Double>() {
							@Override
							public Double apply(E from) {
								return KShortestPaths.delta(graph,
										kshortest.shortestPathTree, from);
							}
						}));

				Hout<V, E> hout = new Hout<V, E>(kshortest, i);
				houts.add(hout.getDotGraph());
				System.out.print("\tHout: ");
				do {
					System.out.print(hout.getRootNode().getKey() + " ");
				} while (hout.removeMin() != null);
				System.out.println();
			} else {
				System.out.println("\tEmpty Hout");
			}
			Ht<V, E> ht = kshortest.getHt(i);
			System.out.println("\tHt: " + ht.toString());
			hts.add(ht.getDotGraph());
		}

		// Write Hout to dot file
		try {
			DotGraph.mergeDotGraphs(houts.toArray(new DotGraph[0])).toFile(
					"output/" + graphOutputName + "Hout" + "Test.dot");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Write Hts to dot file
		try {
			DotGraph.mergeDotGraphs(hts.toArray(new DotGraph[0])).toFile(
					"output/" + graphOutputName + "Ht" + "Test.dot");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("D(G): ");
		Dg<V, E> dg = new Dg<V, E>(graph, from, to, kshortest);

		System.out.println(dg.toString());
		try {
			dg.getDotGraph().toFile("output/" + graphOutputName + "DGTest.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("P(G): ");
		Pg<V, E> pg = new Pg<V, E>(kshortest, dg, from);

		System.out.println(pg.toString());
		try {
			pg.getDotGraph().toFile("output/" + graphOutputName + "PGTest.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("\t5 paths in P(G) and correspondence to G: ");
		// // Iterator<IPgGraphNode<V, E>> pgIterator =
		// // Iterators.limit(new ClosestFirstIterator<IPgGraphNode<V, E>,
		// // DefaultWeightedEdge>(
		// // pg, pg.rootNode),5);
		// //
		// // while(pgIterator.hasNext()) {
		// // IPgGraphNode<V, E> currentNode = pgIterator.next();
		// //
		// // }
		// LinkedList<DefaultWeightedEdge> pathEdges =
		// new LinkedList<DefaultWeightedEdge>();
		// IPgGraphNode<V, E> currentVertex = pg.rootNode;
		// for (int i = 0; i <= 5; i++) {
		// DefaultWeightedEdge edge =
		// Iterables.getFirst(pg.outgoingEdgesOf(currentVertex), null);
		// pathEdges.add(edge);
		// currentVertex = Graphs.getOppositeVertex(pg, edge, currentVertex);
		// GraphPath<IPgGraphNode<V, E>, DefaultWeightedEdge> path =
		// new GraphPathImpl<IPgGraphNode<V, E>, DefaultWeightedEdge>(
		// pg, pg.rootNode, currentVertex, pathEdges, 0);
		// System.out.print("\tP(G) path:\t");
		// for (DefaultWeightedEdge pathEdge : path.getEdgeList()) {
		// System.out.print(" " + pathEdge + " ");
		// System.out.print(pg.getEdgeSource(pathEdge) + " -> "
		// + pg.getEdgeTarget(pathEdge));
		// }
		// System.out.println();
		//
		// System.out.print("\tG (sidetracks):\t");
		// Iterable<E> pathSeq = kshortest.getPathSeqFromPg(path);
		// for (E pathEdge : pathSeq) {
		// System.out.print(" " + pathEdge + " ");
		// System.out.print(graph.getEdgeSource(pathEdge) + " -> "
		// + graph.getEdgeTarget(pathEdge)
		// + graph.getEdgeWeight(pathEdge));
		// }
		// System.out.println();
		//
		// System.out
		// .println("\tG:\t\t"
		// + kshortest.shortestPathTree.getShortestPath(from,
		// pathSeq));
		// }

		System.out.println(String.format("H(G) (first %s): ", k));
		final Hg<V, E> hg = new Hg<V, E>(pg);

		final AbstractLinkedIterator<Integer> counter =
				new AbstractLinkedIterator<Integer>(0) {
					@Override
					protected Integer computeNext(Integer previous) {
						return ++previous;
					}
				};

		System.out.println(Iterables.transform(Iterables.limit(hg, k),
				new Function<HgHeapNode<V, E>, String>() {
					@Override
					public String apply(HgHeapNode<V, E> input) {
						GraphPath<V, E> path =
								kshortest.shortestPathTree.getShortestPath(
										from,
										kshortest
												.getPathSeqFromPg(input.pgPathUntilHere));
						try {
							// nasty side effects (counter) to dump
							// debug graphs
							// to dot
							int count = counter.next();
							GraphDotGraph<V, E> dot =
									new GraphDotGraph<V, E>(graphOutputName
											+ "ColorPath" + count, graph);
							dot.graphPathsToColor =
									Collections.singletonMap(path, "red");
							dot.getDotGraph().toFile(
									"output/" + graphOutputName + "ColorPath"
											+ count + ".dot");
						} catch (IOException e) {
							e.printStackTrace();
						}

						return input + "\n\t\t Corresponding path in G: "
								+ path + "\n\t\t Path Cost: "
								+ path.getWeight() + "\n\t";
					}
				}));
	}

	private static void eppsteinHout() {
		final DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge> graph =
				new DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge>(
						DebugDefaultWeightedEdge.class);

		graph.addVertex('p');
		graph.addVertex('q');
		graph.addVertex('r');
		graph.addVertex('s');
		graph.addVertex('t');

		addEdge(graph, 'p', 'q', 0); // p->q
		addEdge(graph, 'q', 'r', 0); // q->r
		addEdge(graph, 'r', 't', 0); // r->t
		addEdge(graph, 's', 'r', 0); // s->r

		addEdge(graph, 'p', 'q', 1);
		addEdge(graph, 'p', 'q', 6);
		addEdge(graph, 'p', 'q', 12);
		addEdge(graph, 'p', 'q', 14);

		addEdge(graph, 'q', 'r', 13);

		addEdge(graph, 'r', 't', 17);
		addEdge(graph, 'r', 't', 19);

		addEdge(graph, 's', 'r', 3);
		addEdge(graph, 's', 'r', 7);

		addEdge(graph, 't', 't', 4);
		addEdge(graph, 't', 't', 8);
		addEdge(graph, 't', 't', 10);

		printGraphInfo(graph, "HoutTest", 'p', 't', 10);
	}

	private static void eppsteinHout2() {
		final DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge> graph =
				new DirectedWeightedMultigraph<Character, DebugDefaultWeightedEdge>(
						DebugDefaultWeightedEdge.class);

		graph.addVertex('p');
		graph.addVertex('q');
		graph.addVertex('r');
		graph.addVertex('s');
		graph.addVertex('t');

		graph.addVertex('v');
		graph.addVertex('w');

		addEdge(graph, 'p', 'q', 0); // p->q
		addEdge(graph, 'q', 'r', 0); // q->r
		addEdge(graph, 'r', 't', 0); // r->t
		addEdge(graph, 's', 'r', 0); // s->r

		addEdge(graph, 'p', 'q', 1);
		addEdge(graph, 'p', 'q', 6);
		addEdge(graph, 'p', 'q', 12);
		addEdge(graph, 'p', 'q', 14);

		addEdge(graph, 'q', 'r', 13);

		addEdge(graph, 'r', 't', 17);
		addEdge(graph, 'r', 't', 19);

		addEdge(graph, 's', 'r', 3);
		addEdge(graph, 's', 'r', 7);

		addEdge(graph, 't', 't', 4);
		addEdge(graph, 't', 't', 8);
		addEdge(graph, 't', 't', 10);

		addEdge(graph, 'v', 'w', 20);
		addEdge(graph, 'v', 'w', 21);

		printGraphInfo(graph, "HoutTest2", 'w', 't', 10);
	}

	private static <V, E> void addEdge(WeightedGraph<V, E> graph, V from, V to,
			double cost) {
		E e = graph.addEdge(from, to);
		graph.setEdgeWeight(e, cost);
	}
}
