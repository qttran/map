package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.google.common.base.Preconditions;
import KDTree.*;
import PathFinding.PathFinder;
import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class MapsEngine {

	public KDTree k;
	public MapsIO fileReader;

	private Set<String> streetNames; //autocomplete will get


	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {

		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
		this.k = buildKDTree(fpNodes);

		streetNames = fileReader.getAllStreetNames();
	}

	// get nearest neighbor (x, y) --> lat, long, id
	/************** for use with CLI ******************/

	//CLI version
	public String getOutputFromIntersection(List<String> streetnames) throws IOException {
		// find nearest start node (kdtree)
		String intersection1 = fileReader.getIntersection(streetnames.get(0), streetnames.get(1));
		LocationNode nearestStartNode = fileReader.getLocationNode(intersection1);

		// find nearest end node (kdtree)
		String intersection2 = fileReader.getIntersection(streetnames.get(2), streetnames.get(3));
		LocationNode nearestEndNode = fileReader.getLocationNode(intersection2);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPathIds(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}

	public String getOutputFromLatLongs(LatLong s, LatLong e) throws IOException {
		// find nearest start node (kdtree)
		Node n1 = k.KDSearch(new Node("", s.lat, s.lon), 1)[0];
		LocationNode nearestStartNode = fileReader.getLocationNode(n1.ID);

		// find nearest end node (kdtree)
		Node n2 = k.KDSearch(new Node("", e.lat, e.lon), 1)[0];
		LocationNode nearestEndNode = fileReader.getLocationNode(n2.ID);


		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPathIds(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}


	/*************** for use with GUI ***************/

	private Map<Point2D.Double, String> recentlySelectedPointsIDs;

	/**
	 * For GUI: get the nearest Point2D.Double lat/long that is a real node
	 * @param pt
	 * @return
	 */
	public Point2D.Double getNearestPoint(Point2D.Double pt) {
		Node c = k.KDSearch(new Node("", pt.x, pt.y),1)[0];
		// can modify coordinates so it has ID? then can add to recentlyselectedpoints ETC.
		Point2D.Double nearestPt = new Point2D.Double(c.x, c.y);
		return nearestPt;

	}


	/**
	 * For GUI: get directions in the form of a set of StreetNodes
	 * @param start Point2D.Double
	 * @param end Point2D.Double
	 * @return Set<StreetNode>
	 */
	public Set<StreetNode> getPathStreetNodes(Point2D.Double start, Point2D.Double end) throws IOException {
		Set<StreetNode> pathSet = new HashSet<>();
		//TODO populate recentlyselectedpointsids

		Node n1 = k.KDSearch(new Node("", start.x, start.y), 1)[0];
		Node n2 = k.KDSearch(new Node("", end.x, end.y), 1)[0];


		LocationNode startNode = fileReader.getLocationNode(n1.ID);
		LocationNode endNode = fileReader.getLocationNode(n2.ID);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);

		List<LatLong[]> resultList = pF.getPathLatLongs(startNode,endNode);

		for(LatLong[] leg : resultList) {
			Preconditions.checkState(leg.length == 2); //should be a pair of latlongs
			pathSet.add(new StreetNode(leg[0].lat, leg[0].lon, leg[1].lat, leg[1].lon, ""));
		}
		System.out.println(pathSet);
		return pathSet;

	}


	/**
	 * takes in "chunks" ( ex "1111.2222" )
	 */
	public Set<StreetNode> getStreetNodesWithin(String topChunk, String bottomChunk) throws IOException{
		Preconditions.checkArgument(topChunk.length()==9);
		Preconditions.checkArgument(bottomChunk.length()==9);
		Preconditions.checkArgument(bottomChunk.substring(0,4).equals(topChunk.substring(0,4)));
		Set<StreetNode> snSet = new HashSet<>();

		Map<String, LocationNode> nodeMap = fileReader.getAllLocationNodesWithin(topChunk, bottomChunk);
		//System.out.println("read all location nodes done");
		Set<String> ids = nodeMap.keySet();

		// for each node
		for(String n : ids) {
			LocationNode node = nodeMap.get(n);
			//System.out.println(node.toString());
			// for each way ID, get opposide node and to snSet
			for(String wID : node.ways) {
				Way w = fileReader.getWay(wID);
				String oppositeNodeID = w.endNodeID;
				LatLong opposite;
				if(ids.contains(oppositeNodeID)) {
					opposite = nodeMap.get(oppositeNodeID).latlong;
				}
				else {
					// node was not already in set, so find it
					LocationNode opp = fileReader.getLocationNode(oppositeNodeID);
					opposite = opp.latlong;
				}
				LatLong start = node.latlong;
				snSet.add(new StreetNode(start.lat,start.lon, opposite.lat,opposite.lon, w.name));

			}
		}

		return snSet;
	}



	private KDTree buildKDTree(String nodeFile) throws IOException {
		Hashtable<String, Node> _stars = new Hashtable<String, Node>();
		ArrayList<Node> _starsToAdd = new ArrayList<Node>();

		HashMap<String, List<Long>> nodeLatLongPointers = new HashMap<>();
		HashMap<String, Long> nodeLatPointers = new HashMap<>();
		int minLat = 99999;
		int maxLat = 0;
		int minLon = 99999;
		int maxLon = 0;


		//Create a KDTree from the file

		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		long bytes = 0;

		// read the first - unnecessary line
		String line = br.readLine();

		bytes += line.getBytes().length + 1;

		String latLong = "9999.9999";
		List<Long> dummy = new LinkedList<>();
		dummy.add((long) 0);
		dummy.add((long) 0);
		
		String lat = "0000";
		nodeLatLongPointers.put(latLong, dummy);

		while (line != null) {
			line = br.readLine();
			if(line==null)
				break;


			String[] list = line.split("\t");
			double x = Double.parseDouble(list[1]);
			double y = Double.parseDouble(list[2]);
			//put in KDTree
			String id = list[0];
			String currLat = list[0].substring(3,7);
			int currLatNum = Integer.parseInt(currLat);
			int currLonNum = Integer.parseInt(list[0].substring(8,12));
			Node coordinate = new Node(id, x, y);
			_stars.put(id, coordinate);
			_starsToAdd.add(coordinate);

			String currLatLong = list[0].substring(3,12) ;

			//set max min lat lons
			minLat = Math.min(minLat, currLatNum);
			maxLat = Math.max(maxLat, currLatNum);
			minLon = Math.min(minLon, currLonNum);
			maxLon = Math.max(maxLon, currLonNum);

			//keep track of latLong pointers:
			if (!latLong.equals(currLatLong)) {
				//update the previous latlong in the hashMap
				Long previous = nodeLatLongPointers.get(latLong).get(0);
				List<Long> toPut = new LinkedList<>();
				toPut.add(previous);
				toPut.add(bytes);
				nodeLatLongPointers.put(latLong, toPut);
				//update the current latlong in the hashMap
				List<Long> toPut2 = new LinkedList<>();
				toPut2.add(bytes);
				toPut2.add(bytes);
				nodeLatLongPointers.put(currLatLong, toPut2);

				//update latLong
				latLong = currLatLong;
			}

			//keep track of lat pointers: 
			if (!lat.equals(currLat)) {
				if(!nodeLatPointers.containsKey(currLatLong)) {
					nodeLatPointers.put(currLat, bytes);
				}
				//update Lat
				lat = currLat;
			}

			bytes += line.getBytes().length +1;
		}

		// put the final coordinate
		if(!nodeLatLongPointers.containsKey(latLong)) {
			List<Long> toPut = new LinkedList<>();
			Long previous = nodeLatLongPointers.get(latLong).get(0);
			toPut.add(previous);
			toPut.add(bytes);
			nodeLatLongPointers.put(latLong,toPut);
		}

		Integer finalLat = Integer.parseInt(lat) + 1;
		lat = finalLat.toString();
		if(!nodeLatPointers.containsKey(lat)) {
			nodeLatPointers.put(lat,bytes);
		}
		br.close();
		
		fileReader.setNodeLatLongPtrs(nodeLatLongPointers); //send hashmap to file reader
		fileReader.setNodeLatPtrs(nodeLatPointers);
		fileReader.setMaxMinLatLong(maxLat, minLat, maxLon, minLon); //send bounding latlons to filereader
		KDTree k = new KDTree(_stars, _starsToAdd);
		return k;
	}



	/**
	 * for testing
	 */
	public Set<StreetNode> getStreetsFromFile(String fileLocation){
		BufferedReader bufferedReader = null;
		String string;
		Set<StreetNode> streetSet;

		streetSet = new HashSet<StreetNode>();
		try {
			bufferedReader = new BufferedReader(new FileReader(fileLocation));
		} catch (FileNotFoundException e) {	
			return null;
		}
		try {
			string = bufferedReader.readLine();
			while(string != null) {
				String[] words = string.split(",");
				Double a = Double.parseDouble(words[0]);
				Double b = Double.parseDouble(words[1]);
				Double c = Double.parseDouble(words[2]);
				Double d = Double.parseDouble(words[3]);
				streetSet.add(new StreetNode(a, b, c, d, ""));
				string = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
		catch(IOException e) {
			return null;
		}
		return streetSet;
	}


	public Set<String> getStreetNames() {
		return streetNames;
	}

	/************* FOR TESTING *************/
	public String getBestPathNodeIDs(String id1, String id2) throws IOException {

		LocationNode startNode = fileReader.getLocationNode(id1);
		LocationNode endNode = fileReader.getLocationNode(id2);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPathIds(startNode, endNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}

		return result;
	}

	public long[] forTestingGetBytes(String s, String s2) throws IOException{
		return fileReader.getByteBounds(s, s2);
	}
	
	public Map<String, LocationNode> nodesChunkTest(String top8, String bot8) throws IOException {
		return fileReader.getAllLocationNodesWithin(top8, bot8);
	}
}
