package scriptie.output.dot;

public class DotEdge extends DotItem {

	public DotEdge(String label, String fromNodeIdentifier,
			String toNodeIdentifier) {
		this(label, fromNodeIdentifier, toNodeIdentifier, null, null);
	}

	public DotEdge(String label, String fromNodeIdentifier,
			String toNodeIdentifier, LineStyle lineStyle, ArrowShape arrowShape) {
		super(label);
		FromNodeIdentifier = fromNodeIdentifier;
		ToNodeIdentifier = toNodeIdentifier;
		if (lineStyle != null) {
			attributes.put("style", lineStyle.toString());
		}
		if (arrowShape != null) {
			attributes.put("arrowhead", arrowShape.toString());
		}
	}

	public String FromNodeIdentifier;
	public String ToNodeIdentifier;

	public enum LineStyle {
		dotted
	}

	public enum ArrowShape {
		box, crow, diamond, dot, inv, none, normal, tee, vee
	}

	public LineStyle LineStyle;
	public ArrowShape ArrowShape;

	@Override
	public String toString() {
		return String.format("	\"%s\" -> \"%s\" [%s];\n", FromNodeIdentifier,
				ToNodeIdentifier, getAttributesString());
	}
}
