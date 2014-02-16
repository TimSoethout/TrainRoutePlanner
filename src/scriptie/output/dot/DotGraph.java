package scriptie.output.dot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import scriptie.Util;
import scriptie.datastructures.ITree;

public class DotGraph extends DotItem {
	public DotGraph(String label) {
		super(label);
		Nodes = new LinkedList<DotNode>();
		Clusters = new LinkedList<DotSubGraph>();
		Edges = new LinkedList<DotEdge>();
	}

	public DotGraph(String label, int nrNodes, int nrEdges, int nrClusters) {
		super(label);
		Nodes = new ArrayList<DotNode>(nrNodes);
		Clusters = new ArrayList<DotSubGraph>(nrClusters);
		Edges = new ArrayList<DotEdge>(nrEdges);
	}

	public boolean processClusters = true;

	public List<DotNode> Nodes;
	public List<DotSubGraph> Clusters;
	public List<DotEdge> Edges;

	private static <I> DotNode createNewNode(I input,
			Function<I, String> renderNodeLabel) {
		return createNewNode(input, renderNodeLabel, null);
	}

	/**
	 * Helper function for inserting the right rendering in new nodes.
	 * 
	 * @param <I>
	 * @param input
	 *            content of node
	 * @param renderNodeLabel
	 *            Can not be null
	 * @param renderLatexNodeLabel
	 *            Null if not applicatble
	 * @return the new node containing the correct default and latex rendering
	 */
	protected static <I> DotNode createNewNode(I input,
			Function<I, String> renderNodeLabel,
			Function<I, String> renderLatexNodeLabel) {
		if (renderNodeLabel == null) {
			throw new IllegalArgumentException("renderNodeLabel");
		}
		String nodeLabel = renderNodeLabel.apply(input);

		DotNode newNode =
				new DotNode(nodeLabel, Integer.toString(input.hashCode()));

		setLatexLabel(newNode, input, renderNodeLabel, renderLatexNodeLabel);
		return newNode;
	}

	/**
	 * Helper function for inserting the right rendering in new edges.
	 * 
	 * @param <I>
	 * @param input
	 *            content of edge
	 * @param renderEdgeLabel
	 *            Can not be null
	 * @param renderLatexEdgeLabel
	 *            Null if not applicatble
	 * @return the new edge containing the correct default and latex rendering
	 */
	protected static <I> DotEdge createNewEdge(I input, String fromIdentifier,
			String toIdentifier, Function<I, String> renderEdgeLabel,
			Function<I, String> renderLatexEdgeLabel) {
		if (renderEdgeLabel == null) {
			throw new IllegalArgumentException("renderNodeLabel");
		}
		String edgeLabel = renderEdgeLabel.apply(input);

		DotEdge newEdge = new DotEdge(edgeLabel, fromIdentifier, toIdentifier);

		setLatexLabel(newEdge, input, renderEdgeLabel, renderLatexEdgeLabel);
		return newEdge;
	}

	private static <I> void setLatexLabel(DotItem item, I input,
			Function<I, String> renderNodeLabel,
			Function<I, String> renderLatexNodeLabel) {
		// If no specific logic for renderLatexNodeLabel is provided, use
		// possible LatexRepresentation if present.
		if (renderLatexNodeLabel == null) {
			LatexRepresentationable latex =
					Util.as(LatexRepresentationable.class, input);
			if (latex != null) {
				item.attributes.put("texlbl", latex.getLatexRepresentation());
			}
		} else {
			item.attributes.put("texlbl", renderLatexNodeLabel.apply(input));
		}
	}

	public void addNode(DotNode node) {
		Nodes.add(node);
	}

	public void addCluster(DotSubGraph node) {
		Clusters.add(node);
	}

	public void addEdge(DotEdge edge) {
		Edges.add(edge);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("digraph \"%s\"\n", getLabel()));
		sb.append("{\n");

		for (Entry<String, String> attribute : attributes.entrySet()) {
			sb.append(String.format("\t\"%s\"=\"%s\"\n", attribute.getKey(),
					attribute.getValue()));
		}

		for (DotNode node : Nodes) {
			sb.append(node.toString());
		}
		if (processClusters) {
			for (DotSubGraph cluster : Clusters) {
				sb.append(cluster.toString());
			}
		}
		for (DotEdge edge : Edges) {
			sb.append(edge.toString());
		}

		sb.append("}");

		return sb.toString();
	}

	public String toDgmlString() {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version='1.0' encoding='utf-8'?>\n");
		sb.append("<DirectedGraph xmlns='http://schemas.microsoft.com/vs/2009/dgml'>\n");

		sb.append("<Nodes>\n");
		for (DotNode node : Nodes) {
			// if (processClusters) {
			// sb.append(String.format("<Node Id='%s' Label='%s' Group='%s' />\n",
			// node.NodeIdentifier.toString(), node.Label, node.Cluster.Label
			// ));
			// }
			// else {
			sb.append(String.format("<Node Id='%s' Label='%s' />\n",
					Util.escapeXml(node.NodeIdentifier.toString()),
					Util.escapeXml(node.getLabel())));
			// }
		}
		sb.append("</Nodes>\n");

		sb.append("<Links>\n");
		for (DotEdge edge : Edges) {
			sb.append(String.format("<Link Source='%s' Target='%s' />\n",
					Util.escapeXml(edge.FromNodeIdentifier),
					Util.escapeXml(edge.ToNodeIdentifier)));
		}
		sb.append("</Links>\n");

		sb.append("<Properties>\n");

		sb.append("</Properties>\n");

		sb.append("</DirectedGraph>");

		return sb.toString();

	}

	public void toFile(String fileName) throws IOException {
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		// Create file
		try {
			fileWriter = new FileWriter(fileName);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(String.format("digraph \"%s\"\n", getLabel()));
			bufferedWriter.write("{\n");

			for (Entry<String, String> attribute : attributes.entrySet()) {
				bufferedWriter.write(String.format("\t\"%s\"=\"%s\"\n",
						attribute.getKey(), attribute.getValue()));
			}

			for (DotNode node : Nodes) {
				bufferedWriter.write(node.toString());
			}
			if (processClusters) {
				for (DotSubGraph cluster : Clusters) {
					bufferedWriter.write(cluster.toString());
				}
			}
			for (DotEdge edge : Edges) {
				bufferedWriter.write(edge.toString());
			}

			bufferedWriter.write("}");

		} finally {
			// Close the output stream
			bufferedWriter.close();
			fileWriter.close();
		}

	}

	// public void saveFile(String fileName) throws IOException {
	// this.saveFile(fileName, this.toString());
	// }
	//
	// public void saveFile(String fileName, String content) throws IOException
	// {
	// FileWriter fileWriter = null;
	// BufferedWriter bufferedWriter = null;
	// // Create file
	// try {
	// fileWriter = new FileWriter(fileName);
	// bufferedWriter = new BufferedWriter(fileWriter);
	// bufferedWriter.write(content);
	// } finally {
	// // Close the output stream
	// bufferedWriter.close();
	// fileWriter.close();
	// }
	// }

	public static DotGraph mergeDotGraphs(DotGraph... graphs) {
		DotGraph newGraph = new DotGraph("");
		newGraph.processClusters = true;
		for (DotGraph graph : graphs) {
			DotNodeCluster cluster = new DotNodeCluster(graph.getLabel());
			cluster.Nodes.addAll(graph.Nodes);
			cluster.attributes = graph.attributes;
			newGraph.Clusters.add(cluster);
			newGraph.setLabel(newGraph.getLabel() + graph.getLabel() + " ");
			newGraph.Nodes.addAll(graph.Nodes);
			newGraph.Edges.addAll(graph.Edges);
			newGraph.Clusters.addAll(graph.Clusters);

		}
		return newGraph;
	}
}