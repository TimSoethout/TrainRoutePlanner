package scriptie.output.dot;

public class DotNode extends DotItem {

	public DotNode(String label, String nodeIdentifier, Shape shape,
			DotNodeCluster cluster) {
		this(label, nodeIdentifier, shape);
		this.Cluster = cluster;
		cluster.addNode(this);
	}

	public DotNode(String label, String nodeIdentifier, Shape shape) {
		super(label);
		NodeIdentifier = nodeIdentifier;
		if (shape != null) {
			attributes.put("shape", shape.toString());
		}
	}

	public DotNode(String label, String nodeIdentifier) {
		this(label, nodeIdentifier, null);
	}

	public String NodeIdentifier;
	public DotNodeCluster Cluster;

	public enum Shape {
		house, invhouse, triangle, box, polygon, ellipse, oval, circle, point, egg, plaintext, diamond, trapezium, parallelogram, pentagon, hexagon, septagon, octagon, doublecircle, doubleoctagon, tripleoctagon, invtriangle, invtrapezium, Mdiamond, Msquare, Mcircle, rect, rectangle, square, none, note, tab, folder, box3d, component
	}

	// public Shape Shape;

	@Override
	public String toString() {
		String retVal;
		retVal =
				String.format("	\"%s\" [%s];\n", NodeIdentifier,
						getAttributesString());

		return retVal;
	}

}
