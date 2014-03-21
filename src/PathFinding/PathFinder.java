package PathFinding;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import cs32.maps.LocationNode;
import cs32.maps.Way;
import cs32.maps.FileReader.MapsIO;

/**
 * PathFinder
 * @author mcashton
 */
public class PathFinder {

	/**
	 * INNER CLASS:  PathFinderNode
	 */
	private static class PathFinderNode {
		public final LocationNode locNode;
		public double g_score = Double.MAX_VALUE;
		public double f_score = Double.MAX_VALUE;
		public PathFinderNode predecessor = null;
		public String predConnection = ""; //unweighted connection to predecessor
		
		public PathFinderNode(LocationNode a) {
			locNode = a;
		}

		public void setPredecessor(PathFinderNode pred, String conn) {
			predecessor = pred;
			predConnection = conn;
		}

		@Override
		public boolean equals(Object o){  //nodes are equal if they hold the same LocationNode
			final PathFinderNode other = (PathFinderNode) o;
			return locNode.equals(other.locNode); 
		}
		
		@Override
		public int hashCode(){
			return Objects.hashCode(locNode.id); //guava -- hash based on locNode id
		}
	}

	/**
	 * Comparator -- for Priority Queue to compare nodes based on 'f_score' attribute
	 */
	private static Comparator<PathFinderNode> NodeComparator = new Comparator<PathFinderNode>() {
		@Override
		public int compare(PathFinderNode arg0, PathFinderNode arg1) {
			if(arg0.f_score == arg1.f_score)
				return 0;
			if(arg0.f_score < arg1.f_score)
				return -1;
			return 1;
		}

	};



	private final MapsIO _fileReader;
	private Map<String, PathFinderNode> _nodeMap;
	private Map<String, LocationNode> _pagedNodes;
	
	
	public PathFinder(MapsIO f){
		_fileReader = f;
		
		
		// nodeMap holds exactly what is in the a-star priority queue, but 
		// maps LocationNode IDs to their corresponding PathFinderNode
		_nodeMap = new HashMap<>(); 
		
		// pagedNodes -- nodes that are nearby nodes we have searched so far in a current A-Star
		_pagedNodes = new HashMap<>(); 
		
	}


	/**
	 * GET PATH FOR CLI: getPathIds 
	 */
	public List<String> getPathIds(LocationNode start, LocationNode end) throws IOException {
		
		PathFinderNode ret  = aStarSearch(new PathFinderNode(start), new PathFinderNode(end));
		// trace path back
		if(ret!=null){
			List<String> path = new ArrayList<>();
			PathFinderNode curr = ret;
			PathFinderNode pred = ret.predecessor;
			
			while(pred != null) {
				String connection = pred.locNode.id+" -> "+curr.locNode.id+" : "+curr.predConnection;
				path.add(0, connection);
				curr = pred;
				pred = curr.predecessor;
			}
			return path;
		}
		return null; //no path
	}
	
	
	
	/**
	 * GET PATH FOR GUI: getPathLatLongs
	 * get path in the form of a list of pairs of lat longs
	 * each pair represents a 'leg' of the path
	 */
	public List<Point2D.Double[]> getPathLatLongs(LocationNode start, LocationNode end) throws IOException {
		
		PathFinderNode ret  = aStarSearch(new PathFinderNode(start), new PathFinderNode(end));

		// trace path back
		if(ret!=null){
			List<Point2D.Double[]> path = new ArrayList<>();
			
			
			PathFinderNode curr = ret;
			PathFinderNode pred = ret.predecessor;
			Point2D.Double[] connection;
			while(pred != null) {

				connection = new Point2D.Double[]{pred.locNode.latlong, curr.locNode.latlong};
				path.add(connection);
				curr = pred;
				pred = curr.predecessor;
			}
			return path;
		}

		return null; //no path
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A STAR
	 * reset _nodeMap and _pagedNodes
	 */
	private PathFinderNode aStarSearch(PathFinderNode start, PathFinderNode goal) throws IOException {
		_nodeMap = new HashMap<>();
		_pagedNodes = new HashMap<>();
		
		start.g_score = 0;
		start.f_score = start.g_score + heuristic(start, goal);

		PriorityQueue<PathFinderNode> pq = new PriorityQueue<>(50, NodeComparator);
		pq.add(start);		
		_nodeMap.put(start.locNode.id, start);
		
		PathFinderNode curr;
		
		Set<PathFinderNode> closedSet = new HashSet<>();
		
		
		while(!pq.isEmpty()) {

			//remove 'curr' from PQ
			curr = pq.poll(); 
			try{
				Preconditions.checkNotNull(_nodeMap.remove(curr.locNode.id));
			} catch (NullPointerException e) {}
			
			
			
			if(curr.locNode.id.equals(goal.locNode.id)) { // found the best path!
				return curr;
			}

			closedSet.add(curr);

			Map<PathFinderNode, String> neighbors = getConnectedNodes(curr); //  'neighbor PathFinderNode' , 'wayID that connects it to curr'

			/* for each neighbor 'b' of 'curr' */
			for(PathFinderNode b : neighbors.keySet() ) {
				if(closedSet.contains(b))
					continue;
				
				boolean isInPQ = pq.contains(b);
				double tentative_g_score = curr.g_score; // plus weight, but all edges are unweighted
				boolean thisPathIsBetter = tentative_g_score < b.g_score;


				if(isInPQ && thisPathIsBetter) {
					//update gscore, fscore, predecessor
					b.g_score = tentative_g_score;
					b.f_score = b.g_score + heuristic(b, goal);
					b.setPredecessor(curr, neighbors.get(b));

					//remove and re-add
					pq.remove(b);
					pq.offer(b);
					Preconditions.checkState(_nodeMap.containsKey(b.locNode.id));
					_nodeMap.remove(b.locNode.id);
					_nodeMap.put(b.locNode.id, b); //update nodeMap reference

				}
				else if(!isInPQ){
					//predecessor info is already set
					Preconditions.checkNotNull(b.predecessor);
					Preconditions.checkState(!_nodeMap.containsKey(b.locNode.id));
					
					b.g_score = tentative_g_score;
					b.f_score = b.g_score + heuristic(b, goal);
					
					pq.offer(b);
					_nodeMap.remove(b.locNode.id);
					_nodeMap.put(b.locNode.id, b); //add to nodeMap
				}

				// else: do nothing (it is already in the pq but this path is worse)
			}
		}
		//no path
		return null;
	}





	/**
	 * GET CONNECTIONS
	 * returns a map of (PathFinderNode -> wayID), where the wayID is the way that connects each node
	 * to 'start', the input node.
	 * 
	 * Only goes to file if don't already have opposite node in _pagedNodes
	 * 
	 * @param start
	 * @return 
	 * @throws IOException
	 */
	private Map<PathFinderNode, String> getConnectedNodes(PathFinderNode start) throws IOException {
		
		Map<PathFinderNode, String> neighbors = new HashMap<>();

		for(String wayID : start.locNode.ways) { // for every WAY connected to 'start'

			Way way = _fileReader.getWay(wayID); // "id"  "start node ID"    "end node ID"
			
			Preconditions.checkState(start.locNode.id.equals(way.startNodeID));
			
			String oppositeNodeID = way.endNodeID;
			PathFinderNode neighbor = null;
			// if node is already in my nodeMap, get and add it to neighbors
			if(_nodeMap.containsKey(oppositeNodeID)) { 
				neighbor = _nodeMap.get(oppositeNodeID);
			}
			// else, if node is in my pagedMap
			else if(_pagedNodes.containsKey(oppositeNodeID)) {
				neighbor = new PathFinderNode(_pagedNodes.get(oppositeNodeID)); // already have the LocationNode, just create new node
				neighbor.setPredecessor(start, wayID);
				
			}
			// else, read the node from file
			else {
				LocationNode opp = this.getLocationNodeAndPage(oppositeNodeID); // READS A PAGE FROM FILE
				neighbor = new PathFinderNode(opp);
				neighbor.setPredecessor(start, wayID);	
			}
			
			neighbors.put(neighbor, wayID);
			
		}
		return neighbors;
	}

	
	/**
	 * given a node ID, read in a page (i.e. a list of LocationNodes near 
	 * and including that node id)
	 * 
	 * add all paged nodes to _pagedNodes map
	 * 
	 * return the found LocationNode of original nodeID
	 */
	private LocationNode getLocationNodeAndPage(String nodeID) throws IOException {
		List<LocationNode> pageOfNodes = _fileReader.getNodePage(nodeID);
		LocationNode toReturn = null;
		for(LocationNode ln : pageOfNodes) {
			if(ln.id.equals(nodeID)) {
				toReturn = ln;
			}
			else {
				_pagedNodes.put(ln.id, ln);	
			}
		}
		Preconditions.checkNotNull(toReturn);
		return toReturn;
	}
	
	
	/**
	 * A-Star Heuristic  (distance between two lat/long points)
	 */
	private static double heuristic(PathFinderNode curr, PathFinderNode goal) {
		return distance(curr.locNode.latlong, goal.locNode.latlong);
	}

	private static double distance(Point2D.Double start, Point2D.Double end) {
		double R = 6371;
		double dLon = degreeToRad(start.y - end.y);
		double dLat = degreeToRad(start.x - end.x);
		double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
				Math.cos(degreeToRad(start.x)) *
				Math.cos(degreeToRad(end.x)) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		a = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R*a;
	}
	
	private static double degreeToRad(double degree) {
		return (degree * Math.PI / 180.0);
	}
	
	
	
	/*** for testing only ***/
	public Set<LocationNode> dummyGetReceivers(String nodeID) throws IOException{
		LocationNode a = _fileReader.getLocationNode(nodeID);
		Map<PathFinderNode, String> map = getConnectedNodes(new PathFinderNode(a));
		Set<LocationNode> as = new HashSet<>();
		for(PathFinderNode nn : map.keySet()){
			as.add(nn.locNode);
		}
		return as;
	}


	
	
	
	
	
	

}
