package scriptie.graph.algorithms.kshortestpaths;

import java.util.ArrayList;

import net.datastructures.Entry;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.google.common.base.Function;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.Heap;
import scriptie.datastructures.HtHeapNode;
import scriptie.datastructures.ITree;
import scriptie.output.dot.DotEdge;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.DotNode;
import scriptie.output.dot.DotGraphable;
import scriptie.output.dot.TreeDotGraph;

// V - vertex in original graph
// E - edge in original graph
public class Hout<V, E extends DebugDefaultWeightedEdge> implements DotGraphable,
		ITree<Entry<Double, HoutHeapNodeValue<V, E>>> {

	public Hout(KShortestPaths<V, E> kShortestPaths, V vertex) {
		// this.kShortestPaths = kShortestPaths;
		this.correspondingVertex = vertex;
		Iterable<E> outs = kShortestPaths.out(vertex);
		heap = new Heap<Double, HoutHeapNodeValue<V, E>>();
		for (E out : outs) {
			heap.insert(kShortestPaths.delta(out), new HoutHeapNodeValue<V, E>(
					this, out));
		}
		if (!heap.isEmpty()) {
			rootNode = heap.removeMin();
		}
	}

	private Entry<Double, HoutHeapNodeValue<V, E>> rootNode;
	private Heap<Double, HoutHeapNodeValue<V, E>> heap;
	private V correspondingVertex;

	// private KShortestPaths<V, E> kShortestPaths;

	@Override
	public String toString() {
		String retVal =
				rootNode != null ? rootNode.toString() : "" + heap.toString();
		return retVal;
	}

	public Entry<Double, HoutHeapNodeValue<V, E>> removeMin() {
		Entry<Double, HoutHeapNodeValue<V, E>> retVal = rootNode;
		if (!heap.isEmpty()) {
			rootNode = heap.removeMin();
			return retVal;
		}
		return null;
	}

	@Override
	public Iterable<Entry<Double, HoutHeapNodeValue<V, E>>> getChildren(
			final Entry<Double, HoutHeapNodeValue<V, E>> node) {
		// check for value, since Hout and Ht have different Entries
		if (node.getValue() == rootNode.getValue()) {
			ArrayList<Entry<Double, HoutHeapNodeValue<V, E>>> children =
					new ArrayList<Entry<Double, HoutHeapNodeValue<V, E>>>(2);
			if (heap.size() > 0) {
				children.add(heap.min());
			}
			return children;
		} else {
			return heap.getChildren(node);
		}
	}

	@Override
	public DotGraph getDotGraph() {
		return new TreeDotGraph<Entry<Double, HoutHeapNodeValue<V, E>>>(
				String.format("Hout(%s)", correspondingVertex), this,
				new Function<Entry<Double, HoutHeapNodeValue<V, E>>, String>() {
					@Override
					public String apply(
							Entry<Double, HoutHeapNodeValue<V, E>> input) {
						return input.getValue().toString();
					}
				}, Util.getEnsureMathString(String.format("H_{out}(%s)",
						correspondingVertex)),
				new Function<Entry<Double, HoutHeapNodeValue<V, E>>, String>() {
					@Override
					public String apply(
							Entry<Double, HoutHeapNodeValue<V, E>> input) {
						return String.format("%s, %s", Util.latexDecimalFormat
								.format(input.getKey()),
								input.getValue().correspondingEdge
										.getLatexRepresentationFromTo());
					}
				});
	}

	@Override
	public Entry<Double, HoutHeapNodeValue<V, E>> getRootNode() {
		return this.rootNode;
	}
}
