package scriptie.graph.timetable.comparers;

import java.io.Serializable;
import java.util.Comparator;

import scriptie.graph.timetable.TripVertex;

public class TripVertexTimeComparator implements Comparator<TripVertex>,
		Serializable {

	@Override
	public int compare(TripVertex o1, TripVertex o2) {
		return o1.Time.compareTo(o2.Time);
	}

}
