package scriptie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.AbstractLinkedIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import scriptie.graph.algorithms.kshortestpaths.HgHeapNode;
import scriptie.graph.algorithms.kshortestpaths.KShortestPaths;
import scriptie.graph.timetable.PathsTripWeightedEdge;
import scriptie.graph.timetable.Station;
import scriptie.graph.timetable.TimeTableDiGraph;
import scriptie.graph.timetable.TripVertex;
import scriptie.graph.timetable.TripWeightedEdge;
import scriptie.graph.timetable.TripWeightedEdge.Wait;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.GraphDotGraph;

public class TrainTimeTableInitializer {

	/**
	 * visualize: dot output.dot -Tpdf -ooutput.pdf
	 * 
	 * dot output.dot -Tsvg -O
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Map<String, Station> stationAbbreviates = getStationMapping();
		// System.out.println(stationAbbreviates);

		String outputFileName = "output";
		String dataFile = "data/TREIN_ACTIVITEITDI.csv";
		Station departureStation = stationAbbreviates.get("asd");
		DateTime departureTime =
				new DateTime().withHourOfDay(12).withMinuteOfHour(0);
		Station arrivalStation = stationAbbreviates.get("ut");
		boolean makeDot = false;
		boolean eppsteinDebug = false;
		switch (args.length) {
		case 7:
			eppsteinDebug = Boolean.parseBoolean(args[6]);
		case 6:
			makeDot = Boolean.parseBoolean(args[5]);
		case 5:
			arrivalStation = stationAbbreviates.get(args[4]);
			if (arrivalStation == null) {
				throw new IllegalArgumentException(
						"Illegal station abbreviation given: " + args[4]);
			}
		case 4:
			departureStation = stationAbbreviates.get(args[2]);
			if (departureStation == null) {
				throw new IllegalArgumentException(
						"Illegal station abbreviation given: " + args[2]);
			}
			departureTime = Util.parseTime(args[3]);
		case 2:
			outputFileName = args[1];
		case 1:
			dataFile = args[0];
			break;
		default:
			// throw new IllegalArgumentException(
			// "Illegal number of command line arguments given.");
		}

		TimeTableDiGraph graph =
				new TimeTableDiGraph(dataFile, stationAbbreviates);

		final TripVertex departure =
				graph.addDepartureVertex(departureStation, departureTime);
		TripVertex arrival = graph.addArrivalVertex(arrivalStation);

		graph.generateEdges();

		// Dijkstra
		// List<TripWeightedEdge> path = doDijkstra(graph, departure, arrival);
		//
		// List<Station> stationsPassed = new ArrayList<Station>();
		// List<Station> tranfersPassed = new ArrayList<Station>();
		// stationsPassed.add(departure.Station);
		// //
		// Station prevStation = null;
		// for (TripWeightedEdge edge : path) {
		// TripVertex from = graph.getEdgeSource(edge);
		// TripVertex to = graph.getEdgeTarget(edge);
		// double weight = graph.getEdgeWeight(edge);
		// // if (from.RouteNumber != -2 && to.RouteNumber != -2) {
		// System.out.println(String.format("From\t%s\tto\t%s\t(Cost %s)",
		// from, to, weight));
		// // }
		// //
		// if (prevStation != null && prevStation != to.Station) {
		// stationsPassed.add(to.Station);
		// }
		// if (edge.Wait == Wait.Transfer) {
		// tranfersPassed.add(to.Station);
		// }
		// prevStation = to.Station;
		// }
		//
		// System.out.println("Station passed: " + stationsPassed);
		// System.out.println("Transferred at: " + tranfersPassed);
		//
		// ClosestFirstIterator<TripVertex, TripWeightedEdge> iterator =
		// new ClosestFirstIterator<TripVertex, TripWeightedEdge>(graph,
		// departure);
		// while (iterator.hasNext()) {
		// System.out.println(iterator.next());
		// }

		// System.out.println("Station lines:");
		// for (Station i : stationAbbreviates.values()) {
		// if (i.StationLine.size() > 0) {
		// System.out.println(i.StationLine);
		// }
		// }

		// dgml output
		// graph.saveAsDgml(outputFileName + ".dgml");
		// DotGraph dotGraph = graph.generateDot();
		// dotGraph.processClusters = true;
		// dotGraph.saveFile(outputFileName + ".dgml", dotGraph.toDgmlString());

		if (makeDot) {
			graph.saveAsDot("output/" + outputFileName + ".dot");
			// DotGraph dotGraph = graph.generateDot();
			// dotGraph.processClusters = true;
			// dotGraph.saveFile(outputFileName + ".dot");
		}
		// dotGraph.saveFile(outputFileName + ".dot");
		// dotGraph.toFile(outputFileName + ".dot");

		// graph.toWalrusGraphFile(outputFileName + ".graph");

		System.out.println("Begin calculating 10 shortest TripTree paths");
		final KShortestPaths<TripVertex, TripWeightedEdge> kshortest =
				new KShortestPaths<TripVertex, TripWeightedEdge>(graph,
						departure, arrival);
		System.out.println(String.format("10 TripTree paths from %s to %s",
				departure, arrival));

		final AbstractLinkedIterator<Integer> counter =
				new AbstractLinkedIterator<Integer>(1) {
					@Override
					protected Integer computeNext(Integer previous) {
						return ++previous;
					}
				};

		Iterable<GraphPath<TripVertex, TripWeightedEdge>> paths =
				kshortest.getPaths(10);

		System.out
				.println(Iterables
						.transform(
								paths,
								new Function<GraphPath<TripVertex, TripWeightedEdge>, String>() {
									@Override
									public String apply(
											GraphPath<TripVertex, TripWeightedEdge> input) {

										return "\n"
												+ counter.next()
												+ "\t\t Path Cost: "
												+ input.getWeight()
												+ "\n\t\tCorresponding path in G, "
												+ "Edges: "
												+ input.getEdgeList()
												+ "\n\t\tVertices: "
												+ Graphs.getPathVertexList(input)
												+ "\n\t";
									}
								}));

		System.out.println("Finished calculating 10 shortest TripTree paths");

		// output all paths in single dot
		Map<GraphPath<TripVertex, PathsTripWeightedEdge>, String> graphPathsColor =
				new LinkedHashMap<GraphPath<TripVertex, PathsTripWeightedEdge>, String>();

		// Colors for paths
		Iterator<String> colorsIterator =
				Iterators.forArray(new String[] { "red", "blue", "green",
						"yellow", "orange", "purple", "brown", "gray", "pink",
						"cyan" });

		// Graph with only the path nodes and edges
		DirectedWeightedMultigraph<TripVertex, PathsTripWeightedEdge> pathsGraph =
				new DirectedWeightedMultigraph<TripVertex, PathsTripWeightedEdge>(
						PathsTripWeightedEdge.class);

		// Fill the graph with nodes and new edges for each path
		for (GraphPath<TripVertex, TripWeightedEdge> path : paths) {
			List<PathsTripWeightedEdge> pathsEdges =
					new LinkedList<PathsTripWeightedEdge>();
			for (TripWeightedEdge e : path.getEdgeList()) {
				TripVertex s = graph.getEdgeSource(e);
				TripVertex t = graph.getEdgeTarget(e);
				pathsGraph.addVertex(s);
				pathsGraph.addVertex(t);
				PathsTripWeightedEdge pathsEdge =
						new PathsTripWeightedEdge(e.Costs, e.Wait);
				pathsGraph.addEdge(s, t, pathsEdge);
				pathsEdges.add(pathsEdge);
			}
			GraphPath<TripVertex, PathsTripWeightedEdge> graphPath =
					new GraphPathImpl<TripVertex, PathsTripWeightedEdge>(
							pathsGraph, path.getStartVertex(),
							path.getEndVertex(), pathsEdges, path.getWeight());
			graphPathsColor.put(graphPath, colorsIterator.next());
		}

		try {
			GraphDotGraph<TripVertex, PathsTripWeightedEdge> dot =
					new GraphDotGraph<TripVertex, PathsTripWeightedEdge>(
							outputFileName + "Paths", pathsGraph);
			dot.graphPathsToColor = graphPathsColor;
			dot.colorNodes = false;
			dot.renderEdgeLabelFunction =
					new Function<PathsTripWeightedEdge, String>() {
						@Override
						public String apply(PathsTripWeightedEdge input) {
							return String.format("%s %s", input.Wait.toString()
									.substring(0, 1).toLowerCase(),
									input.Costs.getTotalCosts());
						}
					};
			dot.getDotGraph().toFile("output/" + outputFileName + "Paths.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (eppsteinDebug) {
			KShortestPathsTester.printGraphInfo(graph, outputFileName,
					departure, arrival, 10);
		}

		// // reduce graph for output
		// Station uto = stationAbbreviates.get("uto");
		// Station ut = stationAbbreviates.get("ut");
		// List<TripVertex> toBeRemoved = new LinkedList<TripVertex>();
		// for (TripVertex v : graph.vertexSet()) {
		// if (v.Time.isBefore(new
		// DateTime().withHourOfDay(11).withMinuteOfHour(45)) ||
		// v.Time.isAfter(new DateTime().withHourOfDay(14).withMinuteOfHour(15))
		// ||
		// (v.Station != uto && v.Station != ut)) {
		// toBeRemoved.add(v);
		// }
		// }
		// graph.removeAllVertices(toBeRemoved);
		//
		// // save reduced graph
		// graph.saveAsDot("output/" + outputFileName + ".dot");
	}

	@SuppressWarnings("unused")
	private static List<TripWeightedEdge> doDijkstra(TimeTableDiGraph graph,
			TripVertex departure, TripVertex arrival) {
		DijkstraShortestPath<TripVertex, TripWeightedEdge> dijkstra =
				new DijkstraShortestPath<TripVertex, TripWeightedEdge>(graph,
						departure, arrival);

		System.out.println("Total path lenght: " + dijkstra.getPathLength());
		List<TripWeightedEdge> path = dijkstra.getPath().getEdgeList();
		return path;
	}

	@SuppressWarnings("unused")
	private static void saveGraph(TimeTableDiGraph graph, String filePath) {
		// Create a stream for writing.
		FileOutputStream fos;
		ObjectOutputStream outStream;
		try {
			fos = new FileOutputStream(filePath);

			outStream = new ObjectOutputStream(fos);

			// Save each object.
			System.out.println("Begin serializing graph");
			outStream.writeObject(graph);

			// Finally, we call the flush() method for our object, which forces
			// the data to
			// get written to the stream:
			outStream.flush();
			System.out.println("Finished serializing graph");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NotSerializableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private static TimeTableDiGraph readGraph(String filePath) {
		TimeTableDiGraph graph = null;

		FileInputStream fip;
		ObjectInputStream inStream;
		try {
			fip = new FileInputStream(filePath);

			inStream = new ObjectInputStream(fip);

			graph = (TimeTableDiGraph) inStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return graph;
	}

	public static Map<String, Station> getStationMapping()
			throws FileNotFoundException {
		Map<String, Station> dic = new HashMap<String, Station>();

		File file = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			file = new File("data/stations.csv");
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);

			// skip header
			bufferedReader.readLine();
			String line = null;
			int i = 0;
			while ((line = bufferedReader.readLine()) != null) {
				String[] items = line.split(";");
				// Naam;Verkorting
				Station station = new Station(i, items[0], items[1]);

				dic.put(items[1].toLowerCase(), station);
				i++;
			}

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dic;
	}

}
