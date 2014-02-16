package scriptie.graph.timetable;

import java.io.Serializable;
import java.util.SortedSet;

import scriptie.Util;

public class Station implements Serializable {
	public Station(int id, String name, String abbreviation) {
		this.ID = id;
		this.Name = name;
		this.Abbreviation = Util.convertStringFirstToUpper(abbreviation);
		StationLine = new StationLine();
	}

	public Station(String name, String abbreviation) {
		this(-1, name, abbreviation);
	}

	public int ID;
	public String Name;
	public String Abbreviation;
	public SortedSet<TripVertex> StationLine;

	@Override
	public String toString() {
		return Abbreviation;
	}

	public boolean equals(Station obj) {
		return this.Abbreviation.equals(obj.Abbreviation);
	}
}