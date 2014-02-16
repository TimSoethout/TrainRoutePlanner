package scriptie.graph.timetable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.joda.time.DateTime;
import org.joda.time.Period;

import scriptie.TimeTableFileLineIterator;
import scriptie.Util;
import scriptie.graph.timetable.TripVertex.Kind;
import scriptie.graph.timetable.TripWeightedEdge.Wait;
import scriptie.graph.timetable.comparers.TripVertexSortComparator;
import scriptie.graph.timetable.costs.EdgeCost;
import scriptie.graph.timetable.costs.EdgeCostCollection;
import scriptie.graph.timetable.costs.HopEdgeCost;
import scriptie.graph.timetable.costs.NoCostEdgeCost;
import scriptie.graph.timetable.costs.TimeEdgeCost;
import scriptie.output.dot.DotEdge;
import scriptie.output.dot.DotGraph;
import scriptie.output.dot.DotNode;
import scriptie.output.dot.DotNodeCluster;
import scriptie.output.dot.DotNode.Shape;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

public class TimeTableDiGraph extends
		ListenableDirectedWeightedGraph<TripVertex, TripWeightedEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Station> stationAbbreviates;
	public boolean debugInformation = false;

	public static final int UNIQUE_STATIONLINE_ROUTENR = -2;
	public static final int UNIQUE_STARTVERTEX_ROUTENR = -3;
	public static final int UNIQUE_ENDVERTEX_ROUTENR = -4;
	public static final String UNIQUE_UNDEFINED_TRACKNR = "-1";

	public static final double TIME_EDGE_COST_DIVIDER = 1.0 / 60000;
	public static final double HOP_EDGE_COST_DIVIDER = 1 / 10;
	public Period minimalTransferTime;

	public TripVertex departureVertex;
	public TripVertex arrivalVertex;

	private TimeTableDiGraph(Map<String, Station> stationAbbreviates) {
		this(stationAbbreviates, new Period().withMinutes(5));
	}

	public TimeTableDiGraph(Map<String, Station> stationAbbreviates,
			Period minimalTransferTime) {
		super(TripWeightedEdge.class);
		this.stationAbbreviates = stationAbbreviates;
		// this.graph = new DefaultDirectedGraph<TripVertex, TripWeightedEdge>(
		// new TripWeightedEdgeFactory());
		this.minimalTransferTime = minimalTransferTime;
		this.addGraphListener(new CostsGraphListener(this));
	}

	public TimeTableDiGraph(String fileLocation,
			Map<String, Station> stationAbbreviates) {
		this(stationAbbreviates);

		TimeTableFileLineIterator lineIterator =
				new TimeTableFileLineIterator(fileLocation);

		this.insertNodes(lineIterator);
	}

	public void insertNodes(Iterable<String> lines) {
		if (debugInformation) {
			System.out.println("Inserting nodes");
		}
		int lineNr = 0;
		for (String line : lines) {
			String[] items = line.split(";");
			// Ignore empty lines and comments
			if (Strings.isNullOrEmpty(line) || line.startsWith("//")) {
				continue;
			}
			// "TreinNr";"AVK";"Spoor";"Dienstregelpunt";"Tijd"
			Station station =
					stationAbbreviates.get(items[3].trim().toLowerCase());
			if (station == null) {
				station = new Station(items[3].trim(), items[3].trim());
				stationAbbreviates.put(items[3].trim().toLowerCase(), station);
			}

			int routeNumber = Integer.parseInt(items[0]);
			String spoor = items[2].trim().toLowerCase();
			boolean kNode = false;
			Kind k;
			switch (items[1].charAt(0)) {
			case 'A':
				k = Kind.Arrival;
				break;
			case 'V':
				k = Kind.Departure;
				break;
			case 'K': // only one time is given since arrival and
				// departure
				// occur close to each other
				// Generate 2 nodes (A and D), at the same time with a
				// vertex
				// between it.
				kNode = true;
			default:
				k = Kind.Departure;
				break;
			}
			String time = items[4]; // "HH:mm"

			TripVertex vertex =
					new TripVertex(station, routeNumber, spoor, k, time);
			this.addVertex(vertex);
			if (kNode) {
				TripVertex arrivalVertex =
						new TripVertex(station, routeNumber, spoor,
								Kind.Arrival, time);
				this.addVertex(arrivalVertex);
				// graph.insertEdge(insertedArrivalVertex,
				// insertedVertex,
				// o)
			}

			if (debugInformation) {
				lineNr++;
				if (lineNr % 1000 == 0) {
					System.out.println("NodeNr: " + lineNr);
				}
			}
		}
		if (debugInformation) {
			System.out.println("Finished inserting nodes");
		}
	}

	public void generateEdges() {
		if (debugInformation) {
			System.out.println("Begin generating edges");
		}

		Set<TripVertex> sortedVertices =
				ImmutableSortedSet.orderedBy(new TripVertexSortComparator())
						.addAll(this.vertexSet()).build();

		TripVertex prevVertex = null;
		for (TripVertex vertex : sortedVertices) {

			insertStationLineVertex(vertex);

			// skip first
			if (prevVertex != null
					&& vertex.RouteNumber == prevVertex.RouteNumber) {

				EdgeCostCollection costs =
						getEdgeCosts(prevVertex, vertex, Wait.Wait);

				this.addEdge(prevVertex, vertex, new TripWeightedEdge(costs,
						Wait.Wait));

			}
			prevVertex = vertex;
		}

		// All Station lines
		int count = 0;
		int totalSize = stationAbbreviates.values().size();
		for (Station station : stationAbbreviates.values()) {
			if (debugInformation) {
				System.out.print(Util.getProgressString("Station Lines",
						totalSize, count++));
			}
			TripVertex previousStationLineVertex = null;
			for (TripVertex stationLineVertex : station.StationLine) {
				if (previousStationLineVertex != null) {

					EdgeCostCollection costs =
							getEdgeCosts(previousStationLineVertex,
									stationLineVertex, Wait.Wait);

					this.addEdge(previousStationLineVertex, stationLineVertex,
							new TripWeightedEdge(costs, Wait.Wait));
				}
				previousStationLineVertex = stationLineVertex;
			}
		}
		if (debugInformation) {
			System.out.println("Finished generating edges");
		}
	}

	private void insertStationLineVertex(TripVertex vertex) {

		DateTime time =
				vertex.Kind == Kind.Arrival ? vertex.Time
						.plus(minimalTransferTime) : vertex.Time;
		TripVertex stationLineVertex =
				new TripVertex(vertex.Station, UNIQUE_STATIONLINE_ROUTENR,
						vertex.Track, vertex.Kind, time);
		this.addVertex(stationLineVertex);

		vertex.Station.StationLine.add(stationLineVertex);

		Wait wait = null;
		TripVertex from = null;
		TripVertex to = null;

		switch (vertex.Kind) {
		case Arrival:
			wait = Wait.Wait;
			from = vertex;
			to = stationLineVertex;
			break;
		case Departure:
			wait = Wait.Transfer;
			from = stationLineVertex;
			to = vertex;
			break;
		}

		EdgeCostCollection costs = getEdgeCosts(from, to, wait);

		this.addEdge(from, to, new TripWeightedEdge(costs, wait));
	}

	public TripVertex addDepartureVertex(Station station, DateTime time) {
		departureVertex =
				new TripVertex(station, UNIQUE_STARTVERTEX_ROUTENR,
						UNIQUE_UNDEFINED_TRACKNR, Kind.Departure, time);
		this.addVertex(departureVertex);
		station.StationLine.add(departureVertex);
		return departureVertex;
	}

	public TripVertex addArrivalVertex(Station station) {
		// Arbitrary times to get something in the future
		DateTime time =
				new DateTime().withHourOfDay(0).withMinuteOfHour(0).plusDays(1);
		arrivalVertex =
				new TripVertex(station, UNIQUE_ENDVERTEX_ROUTENR,
						UNIQUE_UNDEFINED_TRACKNR, Kind.Arrival, time);
		this.addVertex(arrivalVertex);
		// station.StationLine.add(vertex);

		for (TripVertex v : this.vertexSet()) {
			if (v.Station == station && v.Kind == Kind.Arrival
					&& v != arrivalVertex) {
				EdgeCostCollection costs = new EdgeCostCollection();
				costs.add(new NoCostEdgeCost());
				TripWeightedEdge edge = new TripWeightedEdge(costs, Wait.Wait);
				this.addEdge(v, arrivalVertex, edge);
			}
		}

		return arrivalVertex;
	}

	@SuppressWarnings("unused")
	private EdgeCostCollection getEdgeCosts(TripVertex from, TripVertex to,
			TripWeightedEdge edge) {
		return getEdgeCosts(from, to, edge.Wait);
	}

	private EdgeCostCollection getEdgeCosts(TripVertex from, TripVertex to,
			Wait wait) {
		EdgeCostCollection costs = new EdgeCostCollection();

		Period difference = new Period(from.Time, to.Time);
		Period absoluteDifference = Util.makeAbsolute(difference);
		EdgeCost timeCost =
				new TimeEdgeCost(TIME_EDGE_COST_DIVIDER, absoluteDifference);

		costs.add(timeCost);

		EdgeCost hopCost = new HopEdgeCost(HOP_EDGE_COST_DIVIDER, wait);
		costs.add(hopCost);

		// this.setEdgeWeight(edge, costs.getTotalCosts());
		return costs;
	}

	public void saveAsDgml(String fileName) throws IOException {
		System.out.println("Begin saving DGML");
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;

		int NrVertices = this.vertexSet().size();
		int NrEdges = this.edgeSet().size();

		try {
			fileWriter = new FileWriter(fileName);
			bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(String
					.format("<?xml version='1.0' encoding='utf-8'?>\n"));
			bufferedWriter
					.write("<DirectedGraph xmlns='http://schemas.microsoft.com/vs/2009/dgml'>\n");

			bufferedWriter.write("<Nodes>\n");
			int counter = 0;
			for (TripVertex vertex : this.vertexSet()) {
				// System.out.println("Label " + v.toString() );
				Shape s = null;
				switch (vertex.Kind) {
				case Arrival:
					s = Shape.house;
					break;
				case Departure:
					s = Shape.invhouse;
					break;
				}

				DotNode node =
						new DotNode(vertex.toString(), vertex.getUniqueID(), s);
				bufferedWriter.write(String.format(
						"<Node Id='%s' Label='%s' />\n",
						Util.escapeXml(node.NodeIdentifier.toString()),
						Util.escapeXml(node.getLabel())));

				counter++;
				if (counter % (NrVertices / 10) == 0) {
					System.out.println("Nodes processed: " + counter + "/"
							+ NrVertices);
				}
			}
			bufferedWriter.write("</Nodes>\n");

			bufferedWriter.write("<Links>\n");

			counter = 0;
			for (TripWeightedEdge edge : this.edgeSet()) {
				// System.out.println("Edge " + e.toString() );
				TripVertex from = this.getEdgeSource(edge);
				TripVertex to = this.getEdgeTarget(edge);

				DotEdge dotEdge =
						new DotEdge(edge.toString(), from.getUniqueID(),
								to.getUniqueID());
				bufferedWriter.write(String.format(
						"<Link Source='%s' Target='%s' />\n",
						Util.escapeXml(dotEdge.FromNodeIdentifier),
						Util.escapeXml(dotEdge.ToNodeIdentifier)));

				counter++;
				if (counter % (NrEdges / 10) == 0) {
					System.out.println("Edges processed: " + counter + "/"
							+ NrEdges);
				}
			}
			bufferedWriter.write("</Links>\n");

			bufferedWriter.write("<Properties>\n");
			bufferedWriter.write("</Properties>\n");

			bufferedWriter.write("</DirectedGraph>");
		} finally {
			// Close the output stream
			bufferedWriter.close();
			fileWriter.close();
		}
		System.out.println("Finished saving DGML");
	}

	public void saveAsDot(String fileName) throws IOException {
		System.out.println("Begin saving dot");
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;

		int NrVertices = this.vertexSet().size();
		int NrEdges = this.edgeSet().size();

		// Create file
		try {
			fileWriter = new FileWriter(fileName);
			bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter
					.write(String.format("digraph \"%s\"\n", "directDot"));
			bufferedWriter.write("{\n");

			int counter = 0;
			for (TripVertex vertex : this.vertexSet()) {
				// System.out.println("Label " + v.toString() );
				Shape s = null;
				switch (vertex.Kind) {
				case Arrival:
					s = Shape.house;
					break;
				case Departure:
					s = Shape.invhouse;
					break;
				}

				bufferedWriter.write(new DotNode(vertex.toString(), vertex
						.getUniqueID(), s).toString());

				counter++;
				if (counter % (Math.max(NrEdges / 10, 1)) == 0) {
					System.out.println("Nodes processed: " + counter + "/"
							+ NrVertices);
				}
			}

			counter = 0;
			for (TripWeightedEdge edge : this.edgeSet()) {
				// System.out.println("Edge " + e.toString() );
				TripVertex from = this.getEdgeSource(edge);
				TripVertex to = this.getEdgeTarget(edge);

				bufferedWriter.write(new DotEdge(edge.toString(), from
						.getUniqueID(), to.getUniqueID()).toString());

				counter++;
				if (counter % (Math.max(NrEdges / 10, 1)) == 0) {
					System.out.println("Edges processed: " + counter + "/"
							+ NrEdges);
				}
			}

			bufferedWriter.write("}");

		} finally {
			// Close the output stream
			bufferedWriter.close();
			fileWriter.close();
		}
		System.out.println("Finished saving dot");
	}

	public DotGraph generateDot() {
		System.out.println("Begin generating dot");

		int NrVertices = this.vertexSet().size();
		int NrEdges = this.edgeSet().size();

		DotGraph dotGraph = new DotGraph("timetable", NrVertices, NrEdges, 0);

		// Vertices

		// Clusters per Station
		// HashMap<Station, DotNodeCluster> stations = new HashMap<Station,
		// DotNodeCluster>();
		// for (Vertex<VertexLabel> v : graph.vertices()) {
		// VertexLabel vertex = v.element();
		//
		// Shape s = null;
		// switch (vertex.Kind) {
		// case Arrival:
		// s = Shape.house;
		// break;
		// case Departure:
		// s = Shape.invhouse;
		// break;
		// }
		// DotNodeCluster stationCluster = stations.get(vertex.Station);
		// if (stationCluster == null) {
		// stationCluster = new DotNodeCluster(vertex.Station.Name);
		// stations.put(vertex.Station, stationCluster);
		// }
		//
		// stationCluster.addNode(new DotNode(vertex.toString(), vertex
		// .getUniqueID(), s));
		// }
		// dotGraph.Clusters.addAll(stations.values());

		// Clusters per Station Line
		HashMap<Station, DotNodeCluster> stationLines =
				new HashMap<Station, DotNodeCluster>();
		for (Station station : stationAbbreviates.values()) {
			for (TripVertex vertex : station.StationLine) {
				DotNodeCluster stationCluster =
						stationLines.get(vertex.Station);
				if (stationCluster == null) {
					stationCluster =
							new DotNodeCluster(vertex.Station.Name
									+ " Station Line");
					stationLines.put(vertex.Station, stationCluster);
				}

				Shape s = null;
				switch (vertex.Kind) {
				case Arrival:
					s = Shape.house;
					break;
				case Departure:
					s = Shape.invhouse;
					break;
				}

				stationCluster.addNode(new DotNode(vertex.toString(), vertex
						.getUniqueID(), s, stationCluster));

			}
		}
		dotGraph.Clusters.addAll(stationLines.values());

		// vertices
		int counter = 0;
		for (TripVertex vertex : this.vertexSet()) {
			// System.out.println("Label " + v.toString() );
			Shape s = null;
			switch (vertex.Kind) {
			case Arrival:
				s = Shape.house;
				break;
			case Departure:
				s = Shape.invhouse;
				break;
			}

			dotGraph.addNode(new DotNode(vertex.toString(), vertex
					.getUniqueID(), s));

			counter++;
			if (counter % 1000 == 0) {
				System.out.println("Nodes processed: " + counter + "/"
						+ NrVertices);
			}
		}

		// edges
		counter = 0;
		for (TripWeightedEdge edge : this.edgeSet()) {
			// System.out.println("Edge " + e.toString() );
			TripVertex from = this.getEdgeSource(edge);
			TripVertex to = this.getEdgeTarget(edge);

			dotGraph.addEdge(new DotEdge(edge.toString(), from.getUniqueID(),
					to.getUniqueID()));

			// debug
			counter++;
			if (counter % 1000 == 0) {
				System.out.println("Edges processed: " + counter + "/"
						+ NrEdges);
			}
		}

		System.out.println("Finished generating dot");
		return dotGraph;
	}

	// /**
	// * Convert this graph to Walrus .graph file. NOT WORKING ATM
	// *
	// * @param fileName
	// * @throws IOException
	// */
	// public void toWalrusGraphFile(String fileName) throws IOException {
	//
	// int NrVertices = graph.vertexSet().size();
	// int NrEdges = graph.edgeSet().size();
	//
	// Map<Vertex<VertexLabel>, Integer> VertexIndices = new
	// Hashtable<Vertex<VertexLabel>, Integer>(
	// NrVertices);
	//
	// int i = 0;
	// for (Vertex<VertexLabel> vertice : graph.vertices()) {
	// VertexIndices.put(vertice, i);
	// i++;
	// }
	//
	// FileWriter fileWriter = null;
	// BufferedWriter bufferedWriter = null;
	// // Create file
	// try {
	// fileWriter = new FileWriter(fileName);
	// bufferedWriter = new BufferedWriter(fileWriter);
	//
	// bufferedWriter.write(String.format("Graph\n"));
	// bufferedWriter.write("{\n");
	// bufferedWriter.write("\"Time Table\"; ## name\n");
	// bufferedWriter
	// .write("\"Time Table of a Tuesday in 2009\"; ## description\n");
	// bufferedWriter.write(String.format("@numNodes=%s;\n", NrVertices));
	// bufferedWriter.write(String.format("@numLinks=%s;\n", NrEdges));
	// bufferedWriter.write("@numPaths=0;\n");
	// bufferedWriter.write("@numPathLinks=0;\n");
	//
	// bufferedWriter.write("[\n");
	//
	// boolean first = true;
	// for (Edge<EdgeLabel> edge : graph.edges()) {
	// if (!first) {
	// bufferedWriter.write(",");
	// }
	// first = false;
	//
	// // EdgeLabel edgeLabel = edge.element();
	// Vertex<VertexLabel>[] endVertices = graph.endVertices(edge);
	// int from = VertexIndices.get(endVertices[0]);
	// int to = VertexIndices.get(endVertices[1]);
	//
	// bufferedWriter.write(String.format("{ %s; %s; }", from, to));
	// }
	// bufferedWriter.write("];\n");
	//
	// bufferedWriter.write(" ;                   ## path list\n");
	// bufferedWriter.write(" ;               ## enum-def\n");
	//
	// bufferedWriter
	// .write("@attributeDefinitions=[         ## attr-def      {         @name=$root;         @type=bool;         @default=|| false ||;         @nodeValues=[ { @id=0; @value=T; } ];         @linkValues=;         @pathValues=;      }, {         @name=$tree_link;         @type=bool;         @default=|| false ||;         @nodeValues=;         @linkValues=[\n");
	//
	// first = true;
	// for (int n = 0; n <= NrEdges; n++) {
	// if (!first) {
	// bufferedWriter.write(",");
	// }
	// first = false;
	//
	// bufferedWriter.write(String
	// .format("{ @id=%s; @value=T; }\n", n));
	// }
	// bufferedWriter.write("];         @pathValues=;      }   ];");
	//
	// bufferedWriter
	// .write("@qualifiers=[            ## qualifer lists     {         @type=$spanning_tree;         @name=$sample_spanning_tree;         @description=;         @attributes=[            { @attribute=0; @alias=$root; },            { @attribute=1; @alias=$tree_link; }         ];      }   ];     ");
	// bufferedWriter
	// .write("; ; ; ;             ## visualization hints\n");
	// bufferedWriter.write("; ; ; ; ;           ## interface hints\n");
	//
	// // for (DotNode node : Nodes) {
	// // bufferedWriter.write(node.toString());
	// // }
	// // if (processClusters) {
	// // for (DotNodeCluster cluster : Clusters) {
	// // bufferedWriter.write(cluster.toString());
	// // }
	// // }
	// // for (DotEdge edge : Edges) {
	// // bufferedWriter.write(edge.toString());
	// // }
	// //
	// bufferedWriter.write("}");
	//
	// } finally {
	// // Close the output stream
	// bufferedWriter.close();
	// fileWriter.close();
	// }
	// }
	// private void writeObject(java.io.ObjectOutputStream out) throws
	// IOException {
	// for (Vertex<VertexLabel> v : graph.vertices()) {
	// out.writeObject(v.element());
	// }
	// for (Edge<EdgeLabel> v : graph.edges()) {
	// out.writeObject(v.element());
	// }
	// }
	//
	// private void readObject(java.io.ObjectInputStream in) throws IOException,
	// ClassNotFoundException {
	// }

}
