package scriptie.datastructures;

public class QueueEntry<V, E> {
	/**
	 * Best spanning tree edge to vertex seen so far.
	 */
	public E spanningTreeEdge;

	/**
	 * The vertex reached.
	 */
	public V correspondingVertex;

	// /**
	// * True once spanningTreeEdge is guaranteed to be the true minimum.
	// */
	// boolean frozen;

	QueueEntry(V correspondingVertex, E spanningTreeEdge) {
		this.correspondingVertex = correspondingVertex;
		this.spanningTreeEdge = spanningTreeEdge;
	}
}
