package scriptie.graph.algorithms.kshortestpaths;

import java.util.HashMap;
import java.util.Map;

import net.datastructures.Entry;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.HtHeapNode;
import scriptie.datastructures.ShortestPathTreeNode;
import scriptie.output.dot.DotEdge;
import scriptie.output.dot.DotEdge.ArrowShape;
import scriptie.output.dot.DotEdge.LineStyle;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.DotNode;
import scriptie.output.dot.DotNode.Shape;
import scriptie.output.dot.DotNodeCluster;
import scriptie.output.dot.GraphDotGraph;
import scriptie.output.dot.DotGraphable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class Dg<V, E extends DebugDefaultWeightedEdge> extends
		DefaultDirectedGraph<HtHeapNode<Double, V, E>, DefaultEdge> implements
		DotGraphable {

	// private DirectedGraph<V, E> graph;
	// private ShortestPathTree<V, E> shortestPathTree;
	private KShortestPaths<V, E> supervisor;
	// private Entry<Double, HoutHeapNodeValue<V, E>> rootNode;
	public HtHeapNode<Double, V, E> fromNode;
	public HtHeapNode<Double, V, E> toNode;

	// private LinkedTree<Entry<Double, HoutHeapNode<V, E>>> tree =
	// new LinkedTree<Entry<Double, HoutHeapNode<V, E>>>();
	public Map<V, HtHeapNode<Double, V, E>> hv =
			new HashMap<V, HtHeapNode<Double, V, E>>();

	public Dg(DirectedGraph<V, E> graph, V fromVertex, V toVertex,
			KShortestPaths<V, E> supervisor) {
		super(DefaultEdge.class);

		this.supervisor = supervisor;

		addCorrespondingTreeNode(fromVertex);
	}

	private void addCorrespondingTreeNode(V vertex) {
		Iterable<ShortestPathTreeNode<V, E>> treeNodes =
				supervisor.shortestPathTree.getTreeNodes();
		int totalCount = Iterables.size(treeNodes);
		int count = 0;
		for (ShortestPathTreeNode<V, E> shortestPathNode : treeNodes) {
			if ((count % 100) == 0) {
				supervisor.logMessage(Util.getProgressString("D(G)", totalCount,
						count));
			}
			count++;
			// Get corresponding ht
			Ht<V, E> htv =
					supervisor.getHt(shortestPathNode.correspondingVertex);

			// Get the root of it
			HtHeapNode<Double, V, E> htvRoot = htv.getRootNode();

			if (htvRoot != null) {
				// Put reference in h(v)
				hv.put(shortestPathNode.correspondingVertex, htvRoot);

				// Insert htvRoot and children in D(G)
				this.addVertex(htvRoot);
				addChildrenRecursively(htv, htvRoot);
			}

			// TODO: Make sure that less duplicate nodes are created (?)
		}
	}

	public void addChildrenRecursively(Ht<V, E> ht,
			HtHeapNode<Double, V, E> currentNode) {
		addHoutChildrenRecursively(currentNode);
		// Add nodes from Ht

		for (HtHeapNode<Double, V, E> child : ht.getChildren(currentNode)) {
			// check if not already exists
			if (child.marked) { // add new node
				this.addVertex(child);
				this.addEdge(currentNode, child);
			} else { // lookup existing node
				this.addEdge(currentNode, child.originalHtHeapNode);
			}

			addChildrenRecursively(ht, child);
		}
	}

	private Map<Entry<Double, HoutHeapNodeValue<V, E>>, HtHeapNode<Double, V, E>> alreadyAddedHtHeapNodes =
			new HashMap<Entry<Double, HoutHeapNodeValue<V, E>>, HtHeapNode<Double, V, E>>();

	public void addHoutChildrenRecursively(HtHeapNode<Double, V, E> currentNode) {
		Hout<V, E> houtv = currentNode.getValue().hout;
		for (final Entry<Double, HoutHeapNodeValue<V, E>> child : houtv
				.getChildren(currentNode.wrappedEntry)) {

			HtHeapNode<Double, V, E> childHtNode =
					alreadyAddedHtHeapNodes.get(child);
			// Check if corresponding HoutHeapNodeValue is not already added
			if (childHtNode == null) {
				// Add new HtHeapNode
				childHtNode = new HtHeapNode<Double, V, E>(child);
				this.addVertex(childHtNode);
				alreadyAddedHtHeapNodes.put(child, childHtNode);
			}

			// currentNode could not exist, but a corresponding/parent node can.
			if (this.containsVertex(currentNode)) {
				this.addEdge(currentNode, childHtNode);
			} else {
				this.addEdge(currentNode.originalHtHeapNode, childHtNode);
			}

			addHoutChildrenRecursively(childHtNode);
		}
	}

	@Override
	public String toString() {
		return String.format("\tMap: %s\n\tGraph: %s", hv.toString(),
				super.toString());
	}

	public DotGraph getDotGraph() {
		// String label, Graph<V, E> graph,
		// boolean renderEdgeToString, Function<V, String> renderNodeLabel,
		// Function<V, String> renderLatexNodeLabe
		GraphDotGraph<HtHeapNode<Double, V, E>, DefaultEdge> dot =
				new GraphDotGraph<HtHeapNode<Double, V, E>, DefaultEdge>(
						"D(G)", this);
		dot.renderLatexNodeLabelFunction =
				new Function<HtHeapNode<Double, V, E>, String>() {
					@Override
					public String apply(HtHeapNode<Double, V, E> input) {
						return String.format("%s, %s", Util.latexDecimalFormat
								.format(input.getKey()),
								input.getValue().correspondingEdge
										.getLatexRepresentationFromTo());
					}
				};
		DotGraph dotGraph = dot.getDotGraph();
		dotGraph.processClusters = true;
		dotGraph.attributes.put("rankdir", "LR");

		DotNodeCluster vDotCluster = new DotNodeCluster("v |-> h(v)");
		vDotCluster.attributes.put("texlbl", "\\ensuremath{v \\Mapsto h(v)}");

		// DotNodeCluster hvDotCluster = new DotNodeCluster("H(v)");
		dotGraph.addCluster(vDotCluster);
		for (java.util.Map.Entry<V, HtHeapNode<Double, V, E>> pair : hv
				.entrySet()) {
			String nodeIdentifier = Integer.toString(pair.getKey().hashCode());
			String toNodeIdentifier =
					Integer.toString(pair.getValue().hashCode());
			dotGraph.addNode(new DotNode(pair.getKey().toString(),
					nodeIdentifier, Shape.none, vDotCluster));
			dotGraph.addEdge(new DotEdge("", nodeIdentifier, toNodeIdentifier,
					LineStyle.dotted, ArrowShape.none));

		}
		return dotGraph;
	}
}