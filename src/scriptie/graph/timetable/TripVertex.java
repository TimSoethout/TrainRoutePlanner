package scriptie.graph.timetable;

import java.io.Serializable;

import org.joda.time.*;

import scriptie.TrainTimeTableInitializer;
import scriptie.Util;

public class TripVertex implements Serializable {

	private static final long serialVersionUID = 1L;

	public TripVertex(Station station, int routeNumber, String track,
			Kind kind, String time) {
		this(station, routeNumber, track, kind, Util.parseTime(time));
	}

	public TripVertex(Station station, int routeNumber, String track,
			Kind kind, DateTime time) {
		Station = station;
		RouteNumber = routeNumber;
		Track = track;
		Kind = kind;
		Time = time;
	}

	public Station Station;
	public int RouteNumber;
	public String Track;
	public Kind Kind;
	public DateTime Time;

	public enum Kind {
		/**
		 * Departure node
		 */
		Departure,
		/**
		 * Arrival node
		 */
		Arrival
	}
	
	public String getRouteNumberStringRepresentation() {
		String retVal = Integer.toString(RouteNumber);
		switch(RouteNumber) {
		case TimeTableDiGraph.UNIQUE_STATIONLINE_ROUTENR:
			retVal = "st";
			break;
		case TimeTableDiGraph.UNIQUE_STARTVERTEX_ROUTENR:
			retVal = "ad";
			break;
		case TimeTableDiGraph.UNIQUE_ENDVERTEX_ROUTENR:
			retVal = "aa";
			break;
		}
		return retVal;
	}

	@Override
	public String toString() {
		return String.format("<%s, %s, %s, %s>", Station.toString(),
				getRouteNumberStringRepresentation(), Kind.toString().substring(0, 1).toLowerCase(), Time.toString("HH:mm"));
	}	

	public String getUniqueID() {
		return String.format("s%sn%sk%st%s",Station.Abbreviation, RouteNumber, Time.getMillis(), Kind
				.toString().substring(0, 1));
	}
}
