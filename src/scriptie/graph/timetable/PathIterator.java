package scriptie.graph.timetable;

import java.util.Iterator;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.traverse.ClosestFirstIterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PathIterator<V, E> implements Iterator<GraphPath<V, E>>,
		Iterable<GraphPath<V, E>> {

	private ClosestFirstIterator<V, E> closestFirstIterator;
	private Graph<V, E> graph;

	public PathIterator(Graph<V, E> g, V startVertex) {
		this.graph = g;
		this.closestFirstIterator =
				new ClosestFirstIterator<V, E>(g, startVertex);
	}

	@Override
	public Iterator<GraphPath<V, E>> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return closestFirstIterator.hasNext();
	}

	@Override
	public GraphPath<V, E> next() {
		V next = closestFirstIterator.next();
		return null;
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

}
