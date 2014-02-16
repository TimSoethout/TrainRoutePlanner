package scriptie.output.dot;

import java.util.ArrayList;
import java.util.List;

public class DotSubGraph extends DotItem {
	
	public DotSubGraph(String label) {
		super(label);
	}

	List<DotNode> Nodes = new ArrayList<DotNode>();

	public void addNode(DotNode node) {
		Nodes.add(node);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("	subgraph \"%s\" {\n", getLabel()));
		//sb.append(String.format("		label=\"%s\";\n", Label));
		sb.append("		color=\"red\";\n");
		sb.append(getAttributesString());

		for (DotNode node : Nodes) {
			sb.append(String.format("\t\"%s\";\n", node.NodeIdentifier));
		}

		sb.append("	}\n");

		return sb.toString();
	}

}
