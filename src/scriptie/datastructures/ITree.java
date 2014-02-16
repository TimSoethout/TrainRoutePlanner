package scriptie.datastructures;

public interface ITree<N> {
	/**
	 * Gets the children of the given node in the tree.
	 * @param node
	 * @return The children of node.
	 */
	public Iterable<N> getChildren(N node);
	
	/**
	 * Gets the root node of the tree.
	 * @return the root node of the tree.
	 */
	public N getRootNode();
}
