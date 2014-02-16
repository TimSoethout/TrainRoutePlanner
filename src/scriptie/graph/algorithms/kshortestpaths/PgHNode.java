package scriptie.graph.algorithms.kshortestpaths;

import org.jgrapht.graph.DefaultWeightedEdge;

public class PgHNode<V, E extends DefaultWeightedEdge> implements
		IPgGraphNode<V, E> {

	public V correspondingVertex;

	public PgHNode(V correspondingVertex) {
		this.correspondingVertex = correspondingVertex;
	}

	@Override
	public String toString() {
		return correspondingVertex.toString();
	}
}
