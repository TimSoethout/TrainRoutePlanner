package scriptie.output.dot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;

import scriptie.Util;
import sun.net.www.content.text.Generic;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

public class GraphDotGraph<V, E> implements DotGraphable {

	private DotGraph dotGraph;
	private Graph<V, E> graph;
	public boolean renderEdgeToString;
	public Map<GraphPath<V, E>, String> graphPathsToColor;
	public Function<V, String> renderNodeLabelFunction;
	public Function<V, String> renderLatexNodeLabelFunction;
	public Function<E, String> renderEdgeLabelFunction;
	public Function<E, String> renderLatexEdgeLabelFunction;
	public boolean colorNodes;
	public boolean colorEdges;

	public GraphDotGraph(String label, Graph<V, E> graph) {
		dotGraph =
				new DotGraph(label, graph.vertexSet().size(), graph.edgeSet()
						.size(), 0);
		this.graph = graph;
		renderEdgeToString = false;
		graphPathsToColor = Collections.<GraphPath<V, E>, String> emptyMap();
		renderNodeLabelFunction = Util.toGeneric(Functions.toStringFunction());
		renderLatexNodeLabelFunction = null;
		renderEdgeLabelFunction = Util.toGeneric(Functions.toStringFunction());
		renderLatexEdgeLabelFunction = null;
		colorNodes = true;
		colorEdges = true;
	}

	@Override
	public DotGraph getDotGraph() {
		dotGraph.processClusters = false;

		if (renderNodeLabelFunction == null) {
			renderNodeLabelFunction = Util.toGeneric(Functions.toStringFunction());
		}

		Map<V, String> nodesToColor = new LinkedHashMap<V, String>();
		Map<E, String> edgesToColor = new LinkedHashMap<E, String>();

		for (Entry<GraphPath<V, E>, String> graphPathColorEntry : graphPathsToColor
				.entrySet()) {
			String color = graphPathColorEntry.getValue();
			if (colorNodes) {
				for (V vertex : Graphs.getPathVertexList(graphPathColorEntry
						.getKey())) {
					nodesToColor.put(vertex, color);
				}
			}
			if (colorEdges) {
				for (E edge : graphPathColorEntry.getKey().getEdgeList()) {
					edgesToColor.put(edge, color);
				}
			}
		}

		for (V vertex : graph.vertexSet()) {
			DotNode node =
					DotGraph.createNewNode(vertex, renderNodeLabelFunction,
							renderLatexNodeLabelFunction);

			if (nodesToColor.containsKey(vertex)) {
				node.attributes.put("color", nodesToColor.get(vertex));
			}
			dotGraph.addNode(node);
		}
		for (E edge : graph.edgeSet()) {
			String edgeIdentifier = "";
			if (WeightedGraph.class.isInstance(graph)) {
				edgeIdentifier = Double.toString(graph.getEdgeWeight(edge));
			}
			if (renderEdgeToString) {
				edgeIdentifier += " " + edge.toString();
			}
			String sourceIdentifier =
					Integer.toString(graph.getEdgeSource(edge).hashCode());
			String targetIdentifier =
					Integer.toString(graph.getEdgeTarget(edge).hashCode());

			DotEdge dotEdge =
					DotGraph.createNewEdge(edge, sourceIdentifier,
							targetIdentifier, renderEdgeLabelFunction,
							renderLatexEdgeLabelFunction);
			// new DotEdge(edgeIdentifier, sourceIdentifier, targetIdentifier);

			if (edgesToColor.containsKey(edge)) {
				dotEdge.attributes.put("color", edgesToColor.get(edge));
			}

			dotGraph.addEdge(dotEdge);

		}
		return dotGraph;
	}
}