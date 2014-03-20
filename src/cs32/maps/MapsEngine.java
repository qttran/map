package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

//import KDTree.Coordinates;
import KDTree.KDTree;
import KDTree.Node;
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
		Node n1 = k.KDSearch(new Node("", s.lat, s.lon), 1)[0];
		LocationNode nearestStartNode = fileReader.getLocationNode(n1.ID);

		// find nearest end node (kdtree)
		Node n2 = k.KDSearch(new Node("", e.lat, e.lon), 1)[0];
		LocationNode nearestEndNode = fileReader.getLocationNode(n2.ID);


		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPath(nearestStartNode, nearestEndNode);
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
		System.out.println("HELLO1");
		//TODO populate recentlyselectedpointsids
		
		Node n1 = k.KDSearch(new Node("", start.x, start.y), 1)[0];
		Node n2 = k.KDSearch(new Node("", end.x, end.y), 1)[0];
		
		System.out.println("HELLO2");
		System.out.println(n1.ID + "    " + n2.ID);
		
		LocationNode startNode = fileReader.getLocationNode(n1.ID);
		LocationNode endNode = fileReader.getLocationNode(n2.ID);
		System.out.println("HELLO3");
		
		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<Connection> resultList = pF.getPathSet(startNode, endNode);
		System.out.println("HELLO4");
		for(Connection leg : resultList) {
			System.out.println("HELLO5");
			pathSet.add(new StreetNode(leg.s.getPt().x, leg.s.getPt().y,  leg.e.getPt().x, leg.e.getPt().y, ""));
		}

		return pathSet;

	}


	/**
	 * takes in "chunks"  ( ex "1111.2222" )
	 */
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
		
//		
//		String top = Double.toString(topLeft.x).substring(0, 2) +  Double.toString(topLeft.x).substring(3,5);
//		String bottom =  Double.toString(botRight.x).substring(0, 2) +  Double.toString(botRight.x).substring(3,5);
//
//		List<Way> ws = fileReader.getAllWaysWithin(top, bottom);
//		
//		Set<StreetNode> hs = new HashSet<StreetNode>();
//		for(Way w : ws) {
//
//			// both looking in same general area --- optimize
//			
//			// if (nodeID last 4 digits are in scope)
//			LocationNode start = fileReader.getLocationNode(w.startNodeID);
//			LocationNode end = fileReader.getLocationNode(w.endNodeID);
//			
//			
//			hs.add(new StreetNode(start.latlong.lat, start.latlong.lon, end.latlong.lat, end.latlong.lon, w.name));
//		}
//		System.out.printf("Done. %s StreetNodes between lats %s and %s\n", hs.size(), top, bottom);
//		return hs;
	}

	
	
	private KDTree buildKDTree(String nodeFile) throws IOException {
		Hashtable<String, Node> _stars = new Hashtable<String, Node>();
		ArrayList<Node> _starsToAdd = new ArrayList<Node>();
		
		HashMap<String, Long> nodeLatLongPointers = new HashMap<>();
		
		
		//Create a KDTree from the file
		
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
			String id = list[0];
			Node coordinate = new Node(id, x, y);
			_stars.put(id, coordinate);
			_starsToAdd.add(coordinate);

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

		if(!nodeLatLongPointers.containsKey(latLong)) {
			nodeLatLongPointers.put(latLong,bytes);
		}
		br.close();
		
		fileReader.setNodeLatLongPtrs(nodeLatLongPointers); //send hashmap to file reader
		
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
		List<String> resultList = pF.getPath(startNode, endNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		
		return result;
	}
	
	public long[] forTestingGetBytes(String s, String s2) throws IOException{
		return fileReader.getByteBounds(s, s2);
	}
}
