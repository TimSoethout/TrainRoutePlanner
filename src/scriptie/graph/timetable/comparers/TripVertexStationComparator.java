package scriptie.graph.timetable.comparers;

import java.io.Serializable;
import java.util.Comparator;

import scriptie.graph.timetable.TripVertex;

public class TripVertexStationComparator implements
		Comparator<TripVertex>, Serializable {

	@Override
	public int compare(TripVertex arg0, TripVertex arg1) {
		Integer int0 = new Integer(arg0.RouteNumber);
		Integer int1 = new Integer(arg1.RouteNumber);
		return int0.compareTo(int1);
	}
}
