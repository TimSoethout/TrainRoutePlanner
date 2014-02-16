package scriptie.graph.algorithms.test;

import java.util.List;

/**
 * Class to store test results for graph algorithms tests.
 * @author tim
 *
 */
public class Result<E> {
	public String name;
	public String algorithm;
	public int numberOfVertices;
	public int numberOfEdges;
	public long runTimeCpu;
	public long memoryUsage;
	public Integer bestPathLength;
}
