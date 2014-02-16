package scriptie.output.dot;

import java.util.ArrayList;
import java.util.List;

public class DotNodeCluster extends DotSubGraph {

	public DotNodeCluster(String label) {
		super(label);
		attributes.put("style", "dotted");
		attributes.put("labeljust", "l");
	}

	List<DotNode> Nodes = new ArrayList<DotNode>();

	public void addNode(DotNode node) {
		Nodes.add(node);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("	subgraph \"cluster_%s\" {\n", getLabel()));
		sb.append("\t\t" + getAttributesString().replace(',', ';') + ";\n");


		for (DotNode node : Nodes) {
			sb.append(String.format("\t\t\"%s\";\n", node.NodeIdentifier));
		}

		sb.append("	}\n");

		return sb.toString();
	}
}
