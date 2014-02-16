package scriptie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.Map;

import net.datastructures.Entry;
import net.datastructures.HeapPriorityQueue;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.common.base.Function;

import scriptie.graph.timetable.Station;
import scriptie.graph.timetable.TimeTableDiGraph;
import scriptie.graph.timetable.TripVertex;
import scriptie.output.dot.LatexRepresentationable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Util {

	public static DecimalFormat latexDecimalFormat;

	static {
		latexDecimalFormat = new DecimalFormat();
		latexDecimalFormat.setMinimumFractionDigits(0);
		DecimalFormatSymbols dfs = latexDecimalFormat.getDecimalFormatSymbols();
		dfs.setInfinity("\\infty");
		latexDecimalFormat.setDecimalFormatSymbols(dfs);
	}

	/**
	 * Capitalizes String.
	 * 
	 * @param in
	 *            String to convert
	 * @return Returns first letter uppercase, the rest lowercase
	 */
	public static String convertStringFirstToUpper(String in) {
		return in.substring(0, 1).toUpperCase() + in.substring(1).toLowerCase();
	}

	/**
	 * Makes Periode absolute.
	 * 
	 * @param period
	 * @return
	 */
	public static Period makeAbsolute(Period period) {
		Period absoluteDifference =
				period.toStandardDuration().isShorterThan(null) ? period.minus(
						period).minus(period) : period;
		return absoluteDifference;
	}

	/**
	 * Parses Time from String.
	 * 
	 * @param time
	 *            String of form HHmm
	 * @return DateTime corresponding with time
	 */
	public static DateTime parseTime(String time) {
		int length = time.length();
		int hour =
				(length > 2) ? Integer.parseInt(time.substring(0, length - 2)) % 24
						: 0;

		int minutePos = length > 1 ? length - 2 : 0;
		int minute = Integer.parseInt(time.substring(minutePos, length));

		return new DateTime().withHourOfDay(hour).withMinuteOfHour(minute);
	}

	public static String escapeXml(String input) {
		return input.replace("<", "&lt;").replace(">", "&gt;")
				.replace("&", "&amp;");
	}

	/**
	 * Checks if o is class T, if so casts o to T, otherwise returns null.
	 * Usage: MyType a = as(MyType.class, new MyType()); // 'a' is not null
	 * MyType b = as(MyType.class, ""); // b is null
	 * 
	 * @param <T>
	 *            Class to check for.
	 * @param t
	 *            Class to check for.
	 * @param o
	 *            Object to check.
	 * @return The casted object or null.
	 */
	public static <T> T as(Class<T> t, Object o) {
		return t.isInstance(o) ? t.cast(o) : null;
	}

	public static <T> Iterator<T> getIterator(
			final HeapPriorityQueue<? extends Object, T> queue) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return !queue.isEmpty();
			}

			@Override
			public T next() {
				return queue.removeMin().getValue();
			}

			@Override
			public void remove() {
				throw new NotImplementedException();
			}
		};
	}

	public static String getProgressString(String name, int total,
			int currentCount) {
		return String.format("\r%s Progress: %s/%s (%s%%)", name, currentCount,
				total, currentCount * 100 / total);
	}

	/**
	 * Returns latex representation string of object is an instance of the
	 * LatexRepresentation class
	 * 
	 * @param object
	 * @return Latex representation
	 */
	public static String getLatexRepresentation(Object object) {
		LatexRepresentationable latex =
				Util.as(LatexRepresentationable.class, object);

		return (latex != null) ? latex.getLatexRepresentation() : object
				.toString();

	}

	public static String getEnsureMathString(String inside) {
		return String.format("\\ensuremath{%s}", inside);
	}

	/**
	 * Cast to specific T version because java sucks
	 * 
	 * @param <T>
	 * @param function
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Function<T, String> toGeneric(
			Function<Object, String> function) {
		return (Function<T, String>) function;
	}

	// public static final Function<Entry<?, ?>, String> keyfromtoFunction =
	// new Function<Entry<?, ?>, String>() {
	// public String apply(Entry<?, ?> input) {
	// return String.format("%s, %s", Util.latexDecimalFormat
	// .format(input.getKey()),
	// input.getValue()
	// .getLatexRepresentationFromTo());
	// }
	// };

	// public static Function<Entry,String> keyfromtoFunction = new
	// Function<Entry,String> {
	// public String apply(Entry<?, ?> input) {
	// return input.toString();
	// }
	// }

	/**
	 * Counts the lines in a file.
	 * 
	 * @param fileName
	 *            Path and name for the file to be count.
	 * @return The number of lines
	 */
	public static int countLines(String fileName) {
		int retVal = 0;
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader(fileName));
			lnr.skip(Long.MAX_VALUE);
			retVal = lnr.getLineNumber();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return retVal;
	}
}
