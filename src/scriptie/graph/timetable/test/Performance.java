package scriptie.graph.timetable.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import scriptie.TrainTimeTableInitializer;
import scriptie.Util;
import scriptie.graph.timetable.Station;
import scriptie.graph.timetable.TimeTableDiGraph;
import scriptie.graph.timetable.TripVertex;

public class Performance {

	public static void main(String[] args) {
		WarmUp();
		for (Result result : runTests()) {
			System.out.println(result.name);
			System.out.println(String.format("\tVertices:\t%s\tEdges:\t%s",
					result.numberOfVertices, result.numberOfEdges));
			System.out.println(String.format(
					"\tCPU Time:\t%s\tMemory Usage:\t%s", result.runTimeCpu,
					result.memoryUsage));

			// write results to file
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			try {
				String outputFileName =
						"output/GraphCreationPerformanceTests.csv";
				boolean exists = new File(outputFileName).exists();
				fileWriter = new FileWriter(outputFileName, true);
				bufferedWriter = new BufferedWriter(fileWriter);

				if (!exists) {
					bufferedWriter
							.write("Name;Size;Vertices;Edges;CPU;Memory\n");
				}
				bufferedWriter.write(String.format("%s;%s;%s;%s;%s;%s\n",
						result.name, result.datasetSize,
						result.numberOfVertices, result.numberOfEdges,
						result.runTimeCpu, result.memoryUsage));

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bufferedWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

	public static Iterable<Result> WarmUp() {
		List<Result> results = new LinkedList<Result>();
		for (int i = 0; i < 100; i++) {
			results.add(runFullTest());
		}
		return results;
	}

	public static Iterable<Result> runTests() {
		List<Result> results = new LinkedList<Result>();
		for (int i = 0; i < 100; i++) {
			results.add(runFullTest());
		}
		return results;
	}

	// Tests
	public static Result runVoorbeeldTest() {
		return runTest("Example", "data/voorbeeld.csv", "asd", new DateTime()
				.withHourOfDay(12).withMinuteOfHour(0), "ut");
	}

	public static Result runQuarterTest() {
		return runTest("Quarter", "data/TREIN_ACTIVITEITDIquarter.csv", "asd",
				new DateTime().withHourOfDay(12).withMinuteOfHour(0), "ut");
	}

	public static Result runHalfTest() {
		return runTest("Half", "data/TREIN_ACTIVITEITDIhalf.csv", "asd",
				new DateTime().withHourOfDay(12).withMinuteOfHour(0), "ut");
	}

	public static Result runFullTest() {
		return runTest("Full", "data/TREIN_ACTIVITEITDI.csv", "asd",
				new DateTime().withHourOfDay(12).withMinuteOfHour(0), "ut");
	}

	public static Result runTest(String name, String fileName,
			String fromStationAbbreviation, DateTime fromTime,
			String toStationAbbreviation) {
		Result result = new Result();
		result.name = name;
		result.datasetSize = Util.countLines(fileName);

		// Garbage collection for correct memory usage
		Runtime.getRuntime().gc();

		// start timing
		ThreadMXBean mx = ManagementFactory.getThreadMXBean();
		long startTime = mx.getCurrentThreadCpuTime();
		long startMemory =
				ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
						.getUsed();

		TimeTableDiGraph graph =
				generateTimeTableGraph(fileName, fromStationAbbreviation,
						fromTime, toStationAbbreviation);

		// end timing
		long endMemory =
				ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
						.getUsed();
		long endTime = mx.getCurrentThreadCpuTime();

		result.runTimeCpu = endTime - startTime;
		result.memoryUsage = endMemory - startMemory;
		result.numberOfVertices = graph.vertexSet().size();
		result.numberOfEdges = graph.edgeSet().size();

		return result;
	}

	/**
	 * Creates an instance of TimeTableDiGraph given the parameters.
	 * 
	 * @param fileName
	 *            File containing the dataset
	 * @param fromStationAbbreviation
	 * @param fromTime
	 * @param toStationAbbreviation
	 * @return
	 */
	public static TimeTableDiGraph generateTimeTableGraph(String fileName,
			String fromStationAbbreviation, DateTime fromTime,
			String toStationAbbreviation) {

		Map<String, Station> stationAbbreviates = null;

		try {
			stationAbbreviates = TrainTimeTableInitializer.getStationMapping();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (!stationAbbreviates.containsKey(fromStationAbbreviation)) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid station identifier",
					fromStationAbbreviation));
		}
		if (!stationAbbreviates.containsKey(toStationAbbreviation)) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid station identifier",
					toStationAbbreviation));
		}
		Station fromStation = stationAbbreviates.get(fromStationAbbreviation);
		Station toStation = stationAbbreviates.get(toStationAbbreviation);

		TimeTableDiGraph graph =
				new TimeTableDiGraph(fileName, stationAbbreviates);

		TripVertex departure = graph.addDepartureVertex(fromStation, fromTime);
		TripVertex arrival = graph.addArrivalVertex(toStation);

		graph.generateEdges();
		return graph;
	}
}
