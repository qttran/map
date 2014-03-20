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
	 * create a set of StreetNodes in a bounding box
	 * @param topLeft
	 * @param botRight
	 * @return
	 * @throws IOException
	 */
	public Set<StreetNode> getStreetNodesWithin(Point2D.Double topLeft, Point2D.Double botRight) throws IOException{

		// get all LocationNodes within latlong
		// locationNodes = new Set
		
		// for N in locationNodes:
			// for /w/id of N:
				// fileReader.getOppositeNodeID(/w/id)
				// if already in locationNodes:
					// new StreetNode
				// else:
					// fileReader.getLocationNode(id)  ==>   new StreetNode
		
		//--DONE

		
		
		String top = Double.toString(topLeft.x).substring(0, 2) +  Double.toString(topLeft.x).substring(3,5);
		String bottom =  Double.toString(botRight.x).substring(0, 2) +  Double.toString(botRight.x).substring(3,5);

		List<Way> ws = fileReader.getAllWaysWithin(top, bottom);
		
		Set<StreetNode> hs = new HashSet<StreetNode>();
		for(Way w : ws) {

			// both looking in same general area --- optimize

			LocationNode start = fileReader.getLocationNodeWithin(w.startNodeID);//, t, b);
			
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
