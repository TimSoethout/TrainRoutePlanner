package scriptie.graph.algorithms.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.joda.time.DateTime;

import scriptie.Listener;
import scriptie.TrainTimeTableInitializer;
import scriptie.graph.algorithms.kshortestpaths.KShortestPaths;
import scriptie.graph.timetable.Station;
import scriptie.graph.timetable.TimeTableDiGraph;
import scriptie.graph.timetable.TripVertex;
import scriptie.graph.timetable.TripWeightedEdge;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class Performance {

	public static void main(String[] args) {
		WarmUp();
		for (Result<TripWeightedEdge> result : runTests()) {
			// write results to file
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			try {
				String outputFileName = "output/AlgorithmsPerformanceTests.csv";
				boolean exists = new File(outputFileName).exists();
				fileWriter = new FileWriter(outputFileName, true);
				bufferedWriter = new BufferedWriter(fileWriter);

				if (!exists) {
					bufferedWriter
							.write("Name;Algorithm;Vertices;Edges;CPU;Memory;PathLength\n");
				}
				bufferedWriter.write(String.format("%s;%s;%s;%s;%s;%s;%s\n",
						result.name, result.algorithm, result.numberOfVertices,
						result.numberOfEdges, result.runTimeCpu,
						result.memoryUsage, result.bestPathLength));

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

	public static void WarmUp() {
		runTests();
	}

	public static List<Result<TripWeightedEdge>> runTests() {
		List<Result<TripWeightedEdge>> results =
				new LinkedList<Result<TripWeightedEdge>>();
		for (int i = 0; i < 100; i++) {
			results.addAll(runTestIteration("Voorbeeld", "data/voorbeeld.csv"));
			results.addAll(runTestIteration("Quarter",
					"data/TREIN_ACTIVITEITDIquarter.csv"));
			results.addAll(runTestIteration("Half",
					"data/TREIN_ACTIVITEITDIhalf.csv"));
			// results.addAll(runTestIteration("Full",
			// "data/TREIN_ACTIVITEITDI.csv"));
		}
		return results;
	}

	// Tests
	public static Collection<Result<TripWeightedEdge>> runTestIteration(
			String name, String fileName) {
		TimeTableDiGraph graph = generateTimeTableGraph(fileName);
		List<Result<TripWeightedEdge>> results =
				new LinkedList<Result<TripWeightedEdge>>();
		results.add(runTest(name, graph, Algorithms.Dijkstra));
		results.add(runTest(name, graph, Algorithms.BellmanFord));
		results.add(runTest(name, graph, Algorithms.KShortestPaths));

		return results;

	}

	private static TimeTableDiGraph generateTimeTableGraph(String fileName) {

		Map<String, Station> stationAbbreviates = null;

		try {
			stationAbbreviates = TrainTimeTableInitializer.getStationMapping();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		TimeTableDiGraph graph =
				new TimeTableDiGraph(fileName, stationAbbreviates);
		// graph.debugInformation = true;

		Station fromStation =
				getRandomExistingStation(graph, stationAbbreviates);
		Station toStation = getRandomExistingStation(graph, stationAbbreviates);

		Random random = new Random();
		// TripVertex departure =
		graph.addDepartureVertex(fromStation,
				new DateTime().withHourOfDay(random.nextInt(24))
						.withMinuteOfHour(random.nextInt(60)));
		TripVertex arrival = graph.addArrivalVertex(toStation);

		graph.generateEdges();
		return graph;
	}

	private static Station getRandomExistingStation(TimeTableDiGraph graph,
			Map<String, Station> stationAbbreviates) {

		HashSet<Station> availableStations =
				Sets.newHashSet(Iterables.transform(graph.vertexSet(),
						new Function<TripVertex, Station>() {
							@Override
							public Station apply(TripVertex input) {
								return input.Station;
							}
						}));

		return Iterables.get(availableStations,
				new Random().nextInt(availableStations.size()));
	}

	public enum Algorithms {
		Dijkstra, BellmanFord, KShortestPaths
	}

	public static Result<TripWeightedEdge> runTest(String name,
			TimeTableDiGraph graph, Algorithms algorithm) {
		Result<TripWeightedEdge> result = new Result<TripWeightedEdge>();
		result.name = name;
		result.algorithm = algorithm.toString();

		// Garbage collection for correct memory usage
		Runtime.getRuntime().gc();

		// start timing
		ThreadMXBean mx = ManagementFactory.getThreadMXBean();
		long startTime = mx.getCurrentThreadCpuTime();
		long startMemory =
				ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
						.getUsed();

		switch (algorithm) {
		case Dijkstra:
			result.bestPathLength =
					DijkstraShortestPath.findPathBetween(graph,
							graph.departureVertex, graph.arrivalVertex).size();
			break;
		case BellmanFord:
			result.bestPathLength =
					BellmanFordShortestPath.findPathBetween(graph,
							graph.departureVertex, graph.arrivalVertex).size();
			break;
		case KShortestPaths:
			KShortestPaths<TripVertex, TripWeightedEdge> kShortestPaths =
					new KShortestPaths<TripVertex, TripWeightedEdge>(graph,
							graph.departureVertex, graph.arrivalVertex);
			GraphPath<TripVertex, TripWeightedEdge> paths =
					Iterables.getOnlyElement(kShortestPaths.getPaths(1), null);
			result.bestPathLength =
					paths != null ? paths.getEdgeList().size() : null;
			break;
		}

		// end timing
		long endMemory =
				ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
						.getUsed();
		long endTime = mx.getCurrentThreadCpuTime();

		result.runTimeCpu = endTime - startTime;
		result.memoryUsage = endMemory - startMemory;
		result.numberOfVertices = graph.vertexSet().size();
		result.numberOfEdges = graph.edgeSet().size();

		System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s\n", result.name,
				result.algorithm, result.numberOfVertices,
				result.numberOfEdges, result.runTimeCpu, result.memoryUsage,
				result.bestPathLength));

		return result;
	}
}
