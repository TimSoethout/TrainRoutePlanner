package scriptie.graph.timetable;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

public class CostsGraphListener implements
		GraphListener<TripVertex, TripWeightedEdge> {

	private TimeTableDiGraph graph;

	public CostsGraphListener(TimeTableDiGraph graph) {
		this.graph = graph;
	}

	@Override
	public void edgeAdded(GraphEdgeChangeEvent<TripVertex, TripWeightedEdge> e) {
		TripWeightedEdge edge = e.getEdge();
		graph.setEdgeWeight(edge, edge.getWeight());
	}

	@Override
	public void edgeRemoved(GraphEdgeChangeEvent<TripVertex, TripWeightedEdge> e) {
	}

	@Override
	public void vertexAdded(GraphVertexChangeEvent<TripVertex> e) {
	}

	@Override
	public void vertexRemoved(GraphVertexChangeEvent<TripVertex> e) {
	}

}
