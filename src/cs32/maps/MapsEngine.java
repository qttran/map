package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	private MapsIO fileReader;
	HashMap<String, Integer> latPointers = new HashMap<>();

	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {
		this.k = buildKDTree(fpNodes);
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
			pathSet.add(new StreetNode(leg.s.getPt(), leg.e.getPt(), ""));
		}
		
		return pathSet;
		
	}

	private KDTree buildKDTree(String nodeFile) throws IOException {
		//Create a KDTree from the file
		KDTree k = new KDTree();
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		String line = br.readLine();
		line = br.readLine();
		String lat = line.split("\t")[0].substring(3,7);
		int bytes = 0;

		String tosplit = "a,b,c,";
		System.out.println(tosplit.split(",",-1).length);

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
		for(Way w : ws) {
			//System.out.println("yay!!");
			LocationNode start = fileReader.getLocationNode(w.startNodeID);
			LocationNode end = fileReader.getLocationNode(w.endNodeID);
			hs.add(new StreetNode(new Point2D.Double(start.latlong.lat, start.latlong.lon), 
				new Point2D.Double(end.latlong.lat, end.latlong.lon), w.name));
		}
		return hs;
	}
}
