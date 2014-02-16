package scriptie.graph.timetable.comparers;

import java.io.Serializable;
import java.util.Comparator;

import scriptie.graph.timetable.TripVertex;
import scriptie.graph.timetable.TripVertex.Kind;

public class TripVertexSortComparator implements Comparator<TripVertex>,
		Serializable {

	/**
	 * Compares two TripVertex's by first looking at routenumber, then time, then kind.
	 * Routenumbers are compared over Integer value.
	 * Time by order of time.
	 * Kind with Kind.Arrival bigger than Kind.Wait.
	 */
	@Override
	public int compare(TripVertex arg0, TripVertex arg1) {
		Integer int0 = new Integer(arg0.RouteNumber);
		Integer int1 = new Integer(arg1.RouteNumber);
		int routeNumberCompare = int0.compareTo(int1);
		if (routeNumberCompare == 0) {
			int timeCompare = arg0.Time.compareTo(arg1.Time);
			if (timeCompare == 0) {
				int kindCompare = arg1.Kind == Kind.Arrival ? +1 : -1;
				return kindCompare;
			} else {
				return timeCompare;
			}
		} else {
			return routeNumberCompare;
		}
	}
}
