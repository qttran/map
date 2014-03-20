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
		
	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {
		
		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
		this.k = buildKDTreeLatLong(fpNodes);
		
		
		//fileReader.getAllWays(); // send to autocomplete??? TODO
		//System.out.println("-- done reading all ways --");
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

	//GUI version
	// node1 - node2    ...   node2 - node3
	// set of StreetNodes that have starting point (2d double), ending point (name not  necessary)
	
	
	//User-Click backend:
	// input: latitude and longitude
	// output: latitude and longitude that is nearest real point
	
	
	//Get Directions backend:
	// input: two pairs of latitude and longitude (that are REAL POINTS)
	// output: set of StreetNodes...


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


	public Set<StreetNode> getAllStreetNodes() throws IOException{
		List<Way> ws = fileReader.getAllWays();
		Set<StreetNode> hs = new HashSet<StreetNode>();
		for(Way w : ws) {
			//System.out.println("yay!!");
			LocationNode start = fileReader.getLocationNode(w.startNodeID);
			LocationNode end = fileReader.getLocationNode(w.endNodeID);
			hs.add(new StreetNode(start.latlong.lat, start.latlong.lon, end.latlong.lat, end.latlong.lon, w.name));
		}
		return hs;
	}


	public Set<StreetNode> getStreetNodesWithin(Point2D.Double topLeft, Point2D.Double botRight) throws IOException{
		// read within specific bytes of nodes file
		// have a list of all node objects within
		//   from that list, create a list of all way IDs
		// fileReader.getWay for every single way ID
		
		// ---------
		
		// read within specific bytes of WAYS file based on bounds
		// have a list of way objects
		//   for each node connected to each way, search (WITHIN BOUNDS) and create node object
		           //create streetnode object
		String top = Double.toString(topLeft.x).substring(0, 2) +  Double.toString(topLeft.x).substring(3,5);
		String bottom =  Double.toString(botRight.x).substring(0, 2) +  Double.toString(botRight.x).substring(3,5);

		List<Way> ws = fileReader.getWaysWithin(top, bottom);
		
		Set<StreetNode> hs = new HashSet<StreetNode>();
		for(Way w : ws) {
			
//			String startTopLat = w.startNodeID.substring(3, 7);
//			String startBotLat = Integer.toString(Integer.parseInt(startTopLat)+1);
//			long t = nodeLatLongPointers.get(startTopLat);
//			long b = nodeLatLongPointers.get(startBotLat);
			LocationNode start = fileReader.getLocationNodeWithin(w.startNodeID);//, t, b);
			
			
//			String endTopLat = w.endNodeID.substring(3, 7);
//			String endBotLat = Integer.toString(Integer.parseInt(endTopLat)+1);
//			t = nodeLatLongPointers.get(endTopLat);
//			b = nodeLatLongPointers.get(endBotLat);
			LocationNode end = fileReader.getLocationNodeWithin(w.endNodeID);//, t, b);
			
			
			hs.add(new StreetNode(start.latlong.lat, start.latlong.lon, end.latlong.lat, end.latlong.lon, w.name));
		}
		System.out.printf("Done. %s StreetNodes between lats %s and %s\n", hs.size(), top, bottom);
		return hs;
	}



	private KDTree buildKDTree(String nodeFile) throws IOException {
		HashMap<String, Long> nodeLatLongPointers = new HashMap<>();
		
		
		//Create a KDTree from the file
		KDTree k = new KDTree();
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		long bytes = 0;
		

		//String tosplit = "a,b,c,";
		//System.out.println(tosplit.split(",",-1).length);

		String line = br.readLine();
		bytes += line.getBytes().length + 1;
				
		String lat = "9999";

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

			String currLat = list[0].substring(3,7);
			//keep track of lat pointers:
			if (!lat.equals(currLat)) {
				if(!nodeLatLongPointers.containsKey(currLat)) {
					nodeLatLongPointers.put(currLat, bytes);
				}
				lat = currLat;
			} 

			bytes += line.length() +1;
		}

		if(!nodeLatLongPointers.containsKey(lat)) {
			nodeLatLongPointers.put(lat,bytes);
		}
		br.close();
		
		fileReader.setNodeLatLongPtrs(nodeLatLongPointers); //send hashmap to file reader
		return k;
	}
	
	
	private KDTree buildKDTreeLatLong(String nodeFile) throws IOException {
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

		if(!nodeLatLongPointers.containsKey(latLong)) {
			nodeLatLongPointers.put(latLong,bytes);
		}
		br.close();
		
		fileReader.setNodeLatLongPtrs(nodeLatLongPointers); //send hashmap to file reader
		return k;
	}
	
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
}
