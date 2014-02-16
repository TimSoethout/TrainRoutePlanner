package scriptie.output.dot;

import scriptie.datastructures.ITree;

import com.google.common.base.Function;
import com.google.common.base.Functions;

//TODO convert to IDotGraph
public class TreeDotGraph<T> extends DotGraph {

	public TreeDotGraph(String label, ITree<T> tree,
			Function<T, String> renderNodeLabel, String latexLabel,
			Function<T, String> renderLatexNodeLabel) {
		this(label, tree, renderNodeLabel, renderLatexNodeLabel);
		attributes.put("texlbl", latexLabel);
	}

	@SuppressWarnings("unchecked")
	public TreeDotGraph(String label, ITree<T> tree) {
		this(label, tree, (Function<T, String>) Functions.toStringFunction(),
				null);
	}

	public TreeDotGraph(String label, ITree<T> tree,
			Function<T, String> renderNodeLabel,
			Function<T, String> renderLatexNodeLabel) {
		super(label, 1, 1, 0);
		processClusters = false;
		T rootNode = tree.getRootNode();
		if (rootNode != null) {
			DotNode newNode =
					createNewNode(rootNode, renderNodeLabel,
							renderLatexNodeLabel);

			addNode(newNode);
			processTreeChildren(tree, rootNode, renderNodeLabel,
					renderLatexNodeLabel);
		}
	}

	private void processTreeChildren(ITree<T> tree, T currentNode,
			Function<T, String> renderNodeLabel,
			Function<T, String> renderLatexNodeLabel) {
		String currentNodeIdentifier = Integer.toString(currentNode.hashCode());
		for (T childPosition : tree.getChildren(currentNode)) {

			DotNode newNode =
					createNewNode(childPosition, renderNodeLabel,
							renderLatexNodeLabel);

			addNode(newNode);
			addEdge(new DotEdge("", currentNodeIdentifier,
					newNode.NodeIdentifier));
			processTreeChildren(tree, childPosition, renderNodeLabel,
					renderLatexNodeLabel);
		}
	}

}
