package cs32.maps;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Preconditions;
import KDTree.*;
import PathFinding.PathFinder;
import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class MapsEngine {

	public KDTree k;
	public MapsIO fileReader;

	private Set<String> streetNames; //autocomplete will get
	private LinkedList<Node> recentlyFoundKDNodes; //FOR GUI so dont have to keep kd-treeing

	
	private Map<String, Set<StreetNode>> streetNodesOnScreen; 
	// map "4-lat""4-long" to streetNodes
	
	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {
		recentlyFoundKDNodes = new LinkedList<>();
		streetNodesOnScreen = new HashMap<>();
		
		// build file reader
		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
		
		// build KD tree  (read nodes.tsv file)
		this.k = new KDTree(fileReader.getKDNodes());
		
		// build street name set for autocomplete
		streetNames = fileReader.getAllStreetNames();

	}

	// get nearest neighbor (x, y) --> lat, long, id
	/************** for use with CLI ******************/

	//CLI version
	public String getOutputFromIntersection(List<String> streetnames) throws IOException {

		// find nearest start node (kdtree)
		String intersection1 = fileReader.getIntersection(streetnames.get(0).toLowerCase(), streetnames.get(1).toLowerCase());
		if(intersection1.length()<1) {
			System.out.printf("No intersection found between %s and %s\n", streetnames.get(0), streetnames.get(1));
			return null;
		}
		LocationNode nearestStartNode = fileReader.getLocationNode(intersection1);

		// find nearest end node (kdtree)
		String intersection2 = fileReader.getIntersection(streetnames.get(2).toLowerCase(), streetnames.get(3).toLowerCase());
		if(intersection2.length()<1) {
			System.out.printf("No intersection found between %s and %s\n", streetnames.get(2), streetnames.get(3));
			return null;
		}
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

	/**
	 * For GUI: get the nearest Point2D.Double lat/long that is a real node
	 * @param pt
	 * @return
	 */
	public Point2D.Double getNearestPoint(Point2D.Double pt) {
		Node c = k.KDSearch(new Node("", pt.x, pt.y),1)[0];
		// can modify coordinates so it has ID? then can add to recentlyselectedpoints ETC.
		Point2D.Double nearestPt = new Point2D.Double(c.x, c.y);
		
		recentlyFoundKDNodes.addFirst(c);
		if(recentlyFoundKDNodes.size()>3){
			recentlyFoundKDNodes.removeLast();
		}
		return nearestPt;

	}

	public Point2D.Double getNodeFromIntersection(String name1, String name2) {
		// find intersection
		String intersection1;
		try {
			intersection1 = fileReader.getIntersection(name1, name2);
			if (intersection1.compareTo("") == 0) return null; 
			LocationNode ln = fileReader.getLocationNode(intersection1);
			return new Point2D.Double(ln.latlong.lat, ln.latlong.lon);
			
		} catch (IOException e) {
			return null;
		}
	}
		
	

	/**
	 * For GUI: get directions in the form of a set of StreetNodes
	 * @param start Point2D.Double
	 * @param end Point2D.Double
	 * @return Set<StreetNode>
	 */
	public Set<StreetNode> getPathStreetNodes(Point2D.Double start, Point2D.Double end) throws IOException {
		Set<StreetNode> pathSet = new HashSet<>();
		Node n1 = null, n2 = null;
		for(Node kdnode : recentlyFoundKDNodes) {
			if(kdnode.x == start.x && kdnode.y == start.y) {
				n1 = kdnode;
			}
			if(kdnode.x == end.x && kdnode.y == end.y) {
				n2 = kdnode;
			}
		}
		// if for some reasong we dont have them, find id with KD search
		if(n1==null)
			n1 = k.KDSearch(new Node("", start.x, start.y), 1)[0];
		if(n2==null)
			n2 = k.KDSearch(new Node("", end.x, end.y), 1)[0];


		LocationNode startNode = fileReader.getLocationNode(n1.ID);
		LocationNode endNode = fileReader.getLocationNode(n2.ID);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);

		List<LatLong[]> resultList = pF.getPathLatLongs(startNode,endNode);
		if (resultList == null) return null;

		for(LatLong[] leg : resultList) {
			Preconditions.checkState(leg.length == 2); //should be a pair of latlongs
			pathSet.add(new StreetNode(leg[0].lat, leg[0].lon, leg[1].lat, leg[1].lon, ""));
		}
		return pathSet;

	}
	
	//gui
	public Point2D.Double getIntersectionLatLong(String s1, String s2) throws IOException {


		String intersectionID = fileReader.getIntersection(s1.toLowerCase(), s2.toLowerCase());
		if(intersectionID.length()<1)
			return null;
		Preconditions.checkState(intersectionID.startsWith("/n/"));
		LocationNode intersectionNode = fileReader.getLocationNode(intersectionID);

		return intersectionNode.latlong.getPt();
	}
	

	public static Point2D.Double expandPoint(Point2D.Double latlon, double factor) {
		return new Point2D.Double(latlon.x + (latlon.x * factor), latlon.y + (latlon.y * factor));
	}
	// Should be called by the GUI, take in 2 bounding latlongs, return a set of nodes inside the bounding box 
	public Set<StreetNode> getStreetNodes (Point2D.Double topLeft, Point2D.Double botRight) throws IOException {
		int maxLat = this.fileReader.maxLat;
		int minLat = this.fileReader.minLat;
		int maxLon = this.fileReader.maxLon;
		int minLon = this.fileReader.minLon;

		topLeft = expandPoint(topLeft, .5);
		botRight = expandPoint(botRight, .5);
		
		Set<StreetNode> result = new HashSet<StreetNode>();
		//note: the direction  x/y - lat/long - ?? could be wrong ... not sure
		String xRight = Double.toString(botRight.x+0.01).substring(0,2) + Double.toString(botRight.x+0.01).substring(3,5);
		String xLeft = Double.toString(topLeft.x).substring(0,2) + Double.toString(topLeft.x).substring(3,5);
		String yTop = Double.toString(topLeft.y-0.01).substring(1,3) + Double.toString(topLeft.y-0.01).substring(4,6);
		String yBot = Double.toString(botRight.y).substring(1,3) + Double.toString(botRight.y).substring(4,6);



		//ensure that does not go out of the borders of map:
		xRight = Math.min(Integer.parseInt(xRight),maxLat) + "";
		xRight = Math.max(Integer.parseInt(xRight),minLat) + "";
		xLeft = Math.min(Integer.parseInt(xLeft),maxLat) + "";
		xLeft = Math.max(Integer.parseInt(xLeft),minLat) + "";

		yTop = Math.min(Integer.parseInt(yTop),maxLon) + "";
		yTop = Math.max(Integer.parseInt(yTop),minLon) + "";
		yBot = Math.min(Integer.parseInt(yBot),maxLon) + "";
		yBot = Math.max(Integer.parseInt(yBot),minLon) + "";

		//Test
/*
		System.out.println("xLeft: " + xLeft);

		System.out.println("xRight: " + xRight);

		System.out.println("yTop: " + yTop);

		System.out.println("yBot: " + yBot);*/

		/**
		 * loop through all mini chunks -- if they are already in hashtable, don't get them (get them from hashtable)
		 */
		for (int lat = Integer.parseInt(xLeft); lat < Integer.parseInt(xRight); lat++) {
			for (int lon = Integer.parseInt(yBot); lon < Integer.parseInt(yTop); lon ++) {
				String topChunk = lat + "." + lon;
				String bottomChunk = lat + "." + (lon+1);
				Set<StreetNode> toAdd;
				if (streetNodesOnScreen.containsKey(topChunk)) {
					toAdd = streetNodesOnScreen.get(topChunk);
				}
				else {
					toAdd = getStreetNodesWithin(topChunk, bottomChunk);
					streetNodesOnScreen.put(topChunk, toAdd);
				}
				
				result.addAll(toAdd);
			}
			
		}
		return result;
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
		//RandomAccessFile raf = fileReader.getRAF();
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
		//raf.close();
		return snSet;
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
