package scriptie.datastructures;

import java.text.DecimalFormat;

import org.jgrapht.graph.DefaultWeightedEdge;

import scriptie.Util;
import scriptie.output.dot.LatexRepresentationable;

/**
 * DefaultWeightedEdge with extra debug information
 * 
 * @author tim
 * 
 */
public class DebugDefaultWeightedEdge extends DefaultWeightedEdge implements
		LatexRepresentationable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "(" + super.toString() + this.getWeight() + ")";
	}

	@Override
	public String getLatexRepresentation() {
		return Util.getEnsureMathString(Util.latexDecimalFormat.format(this
				.getWeight()));
	}

	public String getLatexRepresentationFromTo() {
		return String.format("\\ensuremath{%s \\xrightarrow{%s} %s}",
				Util.getLatexRepresentation(getSource()),
				Util.latexDecimalFormat.format(this.getWeight()),
				Util.getLatexRepresentation(getTarget()));
	}
}
