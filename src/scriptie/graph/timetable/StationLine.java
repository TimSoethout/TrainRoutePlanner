package scriptie.graph.timetable;

import java.util.TreeSet;

import scriptie.graph.timetable.comparers.TripVertexTimeComparator;

/**
 * Class with sorted list of nodes by time, to generate the stationline edges
 * @author Tim
 *
 */
public class StationLine extends TreeSet<TripVertex> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StationLine() {
		super(new TripVertexTimeComparator());
	}
}
