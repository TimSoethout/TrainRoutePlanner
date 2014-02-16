package scriptie.graph.algorithms.kshortestpaths;

import net.datastructures.Entry;
import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;
import scriptie.datastructures.HtHeap;
import scriptie.datastructures.HtHeapNode;
import scriptie.datastructures.ITree;
import scriptie.datastructures.ShortestPathTreeNode;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.DotGraphable;
import scriptie.output.dot.TreeDotGraph;

import com.google.common.base.Function;

public class Ht<V, E extends DebugDefaultWeightedEdge> implements DotGraphable,
		ITree<HtHeapNode<Double, V, E>> {
	public HtHeap<V, E> heap;
	private V correspondingVertex;

	public Ht(KShortestPaths<V, E> kShortestPaths, V correspondingVertex) {
		heap = new HtHeap<V, E>();
		this.correspondingVertex = correspondingVertex;

		// Get the next(v) (next node on path v-t)
		ShortestPathTreeNode<V, E> nextv =
				kShortestPaths.shortestPathTree
						.getParent(kShortestPaths.shortestPathTree.treeNodes
								.get(correspondingVertex));

		if (nextv != null) {
			Ht<V, E> htnextv = kShortestPaths.getHt(nextv.correspondingVertex);

			// For now clone the heap to not destruct the other ht's
			try {
				heap = htnextv.heap.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		// insert the outroot(next(v))
		Hout<V, E> parent = kShortestPaths.getHout(correspondingVertex);
		Entry<Double, HoutHeapNodeValue<V, E>> outrootv = parent.getRootNode();

		// TODO: is this correct, can this happen?
		if (outrootv != null) {
			insert(outrootv);
		}

		/*
		 * // debug try { this.getDotGraph().toFile( "output/debug/" + "DEBUGHt"
		 * + correspondingVertex.toString() + "#" + new
		 * java.util.Date().getTime() + "Test.dot"); } catch (IOException e) {
		 * e.printStackTrace(); } // enddebug
		 */

	}

	private void insert(Entry<Double, HoutHeapNodeValue<V, E>> outrootv) {
		heap.insert(outrootv.getKey(), outrootv.getValue());
	}

	@Override
	public String toString() {
		return heap.toString();
	}

	@Override
	public DotGraph getDotGraph() {
		return new TreeDotGraph<HtHeapNode<Double, V, E>>(String.format(
				"Ht(%s)", correspondingVertex), this,
				new Function<HtHeapNode<Double, V, E>, String>() {
					@Override
					public String apply(HtHeapNode<Double, V, E> input) {
						return input.getValue().correspondingEdge.toString();
					}
				}, Util.getEnsureMathString(String.format("H_T(%s)",
						correspondingVertex)),
				new Function<HtHeapNode<Double, V, E>, String>() {
					@Override
					public String apply(HtHeapNode<Double, V, E> input) {
						return String.format("%s, %s", Util.latexDecimalFormat
								.format(input.getKey()),
								input.getValue().correspondingEdge
										.getLatexRepresentationFromTo());
					}
				});
	}

	@Override
	public Iterable<HtHeapNode<Double, V, E>> getChildren(
			HtHeapNode<Double, V, E> node) {
		return heap.getChildren(node);
	}

	@Override
	public HtHeapNode<Double, V, E> getRootNode() {
		return heap.isEmpty() ? null : heap.getRootNode();
	}

}