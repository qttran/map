package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import KDTree.Coordinates;
import KDTree.KDTree;
import PathFinding.PathFinder;
import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class MapsEngine {
	String fpWays;
	String fpNodes;
	String fpIndex;
	public KDTree k;
	MapsIO fileReader;
	HashMap<String, Integer> latPointers = new HashMap<>();

	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {
		this.k = buildKDTree(fpNodes);
		this.fpNodes = fpNodes;
		this.fpIndex = fpIndex;
		this.fpWays = fpWays;
		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
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
		MapsIO fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
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

	private KDTree buildKDTree(String nodeFile) throws IOException {
		//Create a KDTree from the file
		KDTree k = new KDTree();
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		String line = br.readLine();
		line = br.readLine();
		String lat = line.split("\t")[0].substring(3,7);
		int bytes = 0;
		
		//String tosplit = "a,b,c,";
		//System.out.println(tosplit.split(",",-1).length);

		while (line != null) {
			String[] list = line.split("\t");
			double x = Double.parseDouble(list[1]);
			double y  = Double.parseDouble(list[2]);

			//put in KDTree
			Coordinates coordinate = new Coordinates(x,y);
			String id = list[0];
			k.insert(id, coordinate);

			
			//keep track of lat pointers:
			if (!lat.equals(list[0].substring(3,7))) {
				latPointers.put(lat, bytes);
				lat = list[0].substring(3,7);
				bytes = line.length();
			} else {
				bytes += line.length();
			}
			line = br.readLine();
		}
		latPointers.put(lat,bytes);
		
		br.close();
		return k;
	}
	
	public Set<StreetNode> getAllStreetNodes() throws IOException{
		List<Way> ws = fileReader.getAllWays();
		Set<StreetNode> hs = new HashSet<StreetNode>();
		PrintWriter writer = new PrintWriter("all_ways.txt", "UTF-8");
		for(Way w : ws) {
			
			//System.out.println("yay!!");
			LocationNode start = fileReader.getLocationNode(w.startNodeID);
			LocationNode end = fileReader.getLocationNode(w.endNodeID);
			hs.add(new StreetNode(new Point2D.Double(start.latlong.lat, start.latlong.lon), 
				new Point2D.Double(end.latlong.lat, end.latlong.lon), w.name));
				writer.println(start.latlong.lat + "," + start.latlong.lon + "," + end.latlong.lat + "," + end.latlong.lon);
		}
		writer.close();
		return hs;
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
				streetSet.add(new StreetNode(new Point2D.Double(a,b), new Point2D.Double(c,d), ""));
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
