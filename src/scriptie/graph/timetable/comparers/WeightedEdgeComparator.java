package scriptie.graph.timetable.comparers;

import java.util.Comparator;

import org.jgrapht.Graph;

public class WeightedEdgeComparator<V,E> implements Comparator<E> {

	private Graph<V,E> graph;
	
	public WeightedEdgeComparator(Graph<V,E> g) {
		this.graph = g;
	}
	
	@Override
	public int compare(E o1, E o2) {
		return Double.compare(graph.getEdgeWeight(o1), graph.getEdgeWeight(o2));
	}

}
