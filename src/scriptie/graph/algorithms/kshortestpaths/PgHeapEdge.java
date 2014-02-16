package scriptie.graph.algorithms.kshortestpaths;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.Util;
import scriptie.datastructures.DebugDefaultWeightedEdge;

public class PgHeapEdge extends DebugDefaultWeightedEdge {
	@Override
	public String toString() {	
		return "h";
	}
	
//	@Override
//	public String getLatexRepresentation() {			
//		return " ";//String.format("\\ensuremath{%s \\rightarrow %s}", Util.getLatexRepresentation(getSource()), Util.getLatexRepresentation(getTarget()));
//	}
}
