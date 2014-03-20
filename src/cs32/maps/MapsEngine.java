package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import KDTree.Coordinates;
import KDTree.KDTree;
import PathFinding.PathFinder;
import PathFinding.PathFinder.Connection;
import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class MapsEngine {

	public KDTree k;
	protected MapsIO fileReader;

	private Set<String> streetNames; //autocomplete will get


	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {

		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
		this.k = buildKDTree(fpNodes);

		streetNames = fileReader.getAllStreetNames();
	}


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
		List<String> resultList = pF.getPath(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}

	public String getOutputFromLatLongs(LatLong s, LatLong e) throws IOException {
		// find nearest start node (kdtree)
		String nearestStartNodeID = k.searchNumber(1, new Coordinates(s.lat, s.lon)).get(0);
		LocationNode nearestStartNode = fileReader.getLocationNode(nearestStartNodeID);

		// find nearest end node (kdtree)
		String nearestEndNodeID = k.searchNumber(1, new Coordinates(e.lat, e.lon)).get(0);
		LocationNode nearestEndNode = fileReader.getLocationNode(nearestEndNodeID);


		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPath(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}


	/*************** for use with GUI ***************/

	/**
	 * For GUI: get the nearest Point2D.Double lat/long that is a real node
	 * @param pt
	 * @return
	 */
	public Point2D.Double getNearestPoint(Point2D.Double pt) {
		Coordinates c = k.searchNumberCoordinates(1, new Coordinates(pt.x, pt.y)).get(0);
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

		// find nearest start node (kdtree)
		String nearestStartNodeID = k.searchNumber(1, new Coordinates(start.x, start.y)).get(0);
		LocationNode nearestStartNode = fileReader.getLocationNode(nearestStartNodeID);

		// find nearest end node (kdtree)
		String nearestEndNodeID = k.searchNumber(1, new Coordinates(end.x, start.y)).get(0);
		LocationNode nearestEndNode = fileReader.getLocationNode(nearestEndNodeID);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<Connection> resultList = pF.getPathSet(nearestStartNode,nearestEndNode);
		for(Connection leg : resultList) {
			pathSet.add(new StreetNode(leg.s.getPt().x, leg.s.getPt().y,  leg.e.getPt().x, leg.e.getPt().y, ""));
		}

		return pathSet;

	}




	/**
	 * takes in "chunks"  ( ex "1111.2222" )
	 */
	public Set<StreetNode> getStreetNodes(Point2D.Double topLeft, Point2D.Double botRight) throws IOException{
		int xLeft = Integer.parseInt(Double.toString(topLeft.x).substring(0, 2) +  Double.toString(topLeft.x).substring(3,5));
		int xRight =  Integer.parseInt(Double.toString(botRight.x).substring(0, 2) +  Double.toString(botRight.x+0.01).substring(3,5));

		int yTop = Integer.parseInt(Double.toString(topLeft.y).substring(1, 3) +  Double.toString(topLeft.y+0.01).substring(4,6));
		int yBot =  Integer.parseInt(Double.toString(botRight.y).substring(1, 3) +  Double.toString(botRight.y).substring(4,6));

		
		Set<StreetNode> result = new HashSet<StreetNode>();
		for (int i = yBot; i<yTop; i++) {
			String bottomRight = Integer.toString(i) + "." + Integer.toString(xRight);
			if (!fileReader.nodeLatLongPointers.containsKey(bottomRight)) {
				
			}
		}
		
		return null;
	}
	
	
	public Set<StreetNode> getStreetNodesWithin(String topChunk, String bottomChunk) throws IOException{
		Set<StreetNode> snSet = new HashSet<>();

		Map<String, LocationNode> nodeMap = fileReader.getAllLocationNodesWithin(topChunk, bottomChunk);
		Set<String> ids = nodeMap.keySet();

		// for each node
		for(String n : ids) {
			LocationNode node = nodeMap.get(n);

			// for each way ID, get opposide node and to snSet
			for(String wID : node.ways) {
				Way w = fileReader.getWay(wID);
				String oppositeNodeID = w.endNodeID;
				LatLong opposite;
				if(ids.contains(oppositeNodeID)) {
					opposite = nodeMap.get(oppositeNodeID).latlong;
				}
				else {
					System.out.println(":((( node was NOT already in set (getStreetNodesWithin)");
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
	HashMap<String, Long> nodeLatLongPointers = new HashMap<>();


	//Create a KDTree from the file
	KDTree k = new KDTree();
	BufferedReader br = new BufferedReader(new FileReader(nodeFile));
	long bytes = 0;

	// read the first - unnecessary line
	String line = br.readLine();

	bytes += line.getBytes().length + 1;

	String latLong = "9999.9999";

	while (line != null) {
		line = br.readLine();
		if(line==null)
			break;


		String[] list = line.split("\t");
		double x = Double.parseDouble(list[1]);
		double y  = Double.parseDouble(list[2]);
		//put in KDTree
		Coordinates coordinate = new Coordinates(x,y);
		String id = list[0];
		k.insert(id, coordinate);

		String currLatLong = list[0].substring(3,12) ;
		//keep track of latLong pointers:
		if (!latLong.equals(currLatLong)) {
			if(!nodeLatLongPointers.containsKey(currLatLong)) {
				nodeLatLongPointers.put(currLatLong, bytes);
			}
			latLong = currLatLong;
		} 

		bytes += line.getBytes().length +1;
	}

	// put the final coordinate
	Integer finalLong = Integer.parseInt(latLong.substring(5,9)) + 1;
	latLong = latLong.substring(0,5) + finalLong.toString();
	if(!nodeLatLongPointers.containsKey(latLong)) {
		nodeLatLongPointers.put(latLong,bytes);
	}
	br.close();


	fileReader.setNodeLatLongPtrs(nodeLatLongPointers); //send hashmap to file reader
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
}
