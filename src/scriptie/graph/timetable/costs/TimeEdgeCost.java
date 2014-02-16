package scriptie.graph.timetable.costs;


import org.joda.time.Period;

import com.eaio.util.text.HumanTime;

public class TimeEdgeCost extends EdgeCost {
	private static final long serialVersionUID = 1L;
	private Period time;
	
	public TimeEdgeCost(double weight, Period time) {
		super(weight);
		this.time = time;
	}

	@Override
	public int getEdgeCost() {
		return (int) time.toStandardDuration().getMillis();
	}
	
	@Override
	public String toString() {
		return HumanTime.approximately(time.toStandardDuration().getMillis());
	}
}
