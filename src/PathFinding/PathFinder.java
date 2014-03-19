package PathFinding;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import cs32.maps.LatLong;
import cs32.maps.LocationNode;
import cs32.maps.MapsMathUtility;
import cs32.maps.Way;
import cs32.maps.FileReader.MapsIO;

/**
 * PathFinder
 * @author mcashton
 */
public class PathFinder {

	/**
	 * INNER CLASS:  Node
	 */
	private static class Node {
		public final LocationNode locNode;
		public double g_score = Double.MAX_VALUE;
		public double f_score = Double.MAX_VALUE;
		public Node predecessor = null;
		public String predConnection = ""; //unweighted connection to predecessor
		public Node(LocationNode a) {
			locNode = a;
		}

		public void setPredecessor(Node pred, String conn) {
			predecessor = pred;
			predConnection = conn;
		}

		@Override
		public boolean equals(Object o){  //nodes are equal if they hold the same LocationNode
			final Node other = (Node) o;
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
	public static Comparator<Node> NodeComparator = new Comparator<Node>() {
		@Override
		public int compare(Node arg0, Node arg1) {
			if(arg0.f_score == arg1.f_score)
				return 0;
			if(arg0.f_score < arg1.f_score)
				return -1;
			return 1;
		}

	};



	private final MapsIO fileUtility;
	private Map<String, Node> _nodeMap;
	private Map<String, LocationNode> _pagedNodes;
	
	
	public PathFinder(MapsIO f){
		_nodeMap = new HashMap<>(); // the priority queue of A-STAR
		_pagedNodes = new HashMap<>(); 
		fileUtility = f;
	}


	/**
	 * getPath(...)
	 * 
	 * - get path in the form of a list of Strings in the format "name -> name : filme"
	 * - if no path, return null
	 * 
	 * @param name1
	 * @param name2
	 * @return List<String> 
	 * @throws IOException
	 */
	public List<String> getPath(LocationNode start, LocationNode end) throws IOException {
		_nodeMap = new HashMap<>();
		Node ret  = aStarSearch(new Node(start), new Node(end));


		// trace path back
		if(ret!=null){
			List<String> path = new ArrayList<>();
			Node curr = ret;
			Node pred = ret.predecessor;
			
			while(pred != null) {
				String connection = getConnectionString(pred.locNode.id, curr.locNode.id, curr.predConnection);
				path.add(0, connection);
				curr = pred;
				pred = curr.predecessor;
			}
			return path;
		}

		return null; //no path
	}

	private static String getConnectionString(String nodeid1, String nodeid2, String wayid){
		return nodeid1 + " -> " + nodeid2 + " : " + wayid;
	}

	public class Connection {
		public final LatLong s;
		public final LatLong e;
		public Connection(LatLong loc1, LatLong loc2) {
			s = loc1;
			e = loc2;
		}
	}
	public List<Connection> getPathSet(LocationNode start, LocationNode end) throws IOException {
		_nodeMap = new HashMap<>();
		Node ret  = aStarSearch(new Node(start), new Node(end));

		
		
		// trace path back
		if(ret!=null){
			List<Connection> path = new ArrayList<>();
			Connection connection;
			
			
			Node curr = ret;
			Node pred = ret.predecessor;
			
			while(pred != null) {
				connection = new Connection(pred.locNode.latlong, curr.locNode.latlong);

				path.add(connection);
				curr = pred;
				pred = curr.predecessor;
			}
			return path;
		}

		return null; //no path
	}

	private Node aStarSearch(Node start, Node goal) throws IOException {

		start.g_score = 0;
		start.f_score = start.g_score + heuristic(start, goal);

		PriorityQueue<Node> pq = new PriorityQueue<>(50, NodeComparator);
		pq.add(start);		
		_nodeMap.put(start.locNode.id, start);
		
		Node curr;
		
		Set<Node> closedSet = new HashSet<>();
		
		
		while(!pq.isEmpty()) {

			//remove 'curr' from PQ
			curr = pq.poll(); 
			Preconditions.checkNotNull(_nodeMap.remove(curr.locNode.id));
			
			//System.out.println("\n\nCURR "+curr.locNode.toString());
			
			if(curr.locNode.id.equals(goal.locNode.id)) { // found the best path!
				//System.out.println("----------------");
				return curr;
			}

			closedSet.add(curr);

			Map<Node, String> neighbors = getConnectedNodes(curr); //  'neighbor Node' , 'wayID that connects it to curr'
//			for(Node n : neighbors.keySet()) {
//				System.out.println("    connected to "+n.locNode.id);
//			}

			/* for each neighbor 'b' of 'curr' */
			for(Node b : neighbors.keySet() ) {
				if(closedSet.contains(b))
					continue;

//				System.out.println();
//				System.out.println(b.locNode.toString());
//				System.out.println("PQ: ");
//				for(Iterator<Node> it = pq.iterator(); it.hasNext(); ) {
//					Node p = it.next();
//					System.out.print(p.locNode.id + ", ");
//				}
//				System.out.println();
//				System.out.println("Node Map: ");
//				for(String s : _nodeMap.keySet()) {
//					System.out.print(s+", ");
//				}
//				System.out.println("<><><<>");
				
				
				
				boolean isInPQ = pq.contains(b);

				if(!isInPQ) {
					Preconditions.checkState(b.predecessor.equals(curr));
				}
				else {
					Preconditions.checkState(!b.predecessor.equals(curr));
				}


				double tentative_g_score = curr.g_score; // + dist_between(curr, b)

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

	private static double heuristic(Node curr, Node goal) {
		return MapsMathUtility.distance(curr.locNode.latlong, goal.locNode.latlong);
	}







	// map (NODE => wayID) that connects it to 'start'
	private Map<Node, String> getConnectedNodes(Node start) throws IOException {
		
		Map<Node, String> neighbors = new HashMap<>();

		for(String wayID : start.locNode.ways) { // for every WAY connected to 'start'

			Way way = fileUtility.getWay(wayID); // "id"  "start node ID"    "end node ID"
			
			Preconditions.checkState(start.locNode.id.equals(way.startNodeID));
			
			String oppositeNodeID = way.endNodeID;
			Node neighbor = null;
			// if node is already in my nodeMap, get and add it to neighbors
			if(_nodeMap.containsKey(oppositeNodeID)) { 
				neighbor = _nodeMap.get(oppositeNodeID);
			}
			// else, if node is in my pagedMap
			else if(_pagedNodes.containsKey(oppositeNodeID)) {
				System.out.println("NOT calling file utility because I already have node!!!");
				neighbor = new Node(_pagedNodes.get(oppositeNodeID)); // already have the LocationNode, just create new node
				neighbor.setPredecessor(start, wayID);
				
			}
			// else, read the node from file
			else {
				LocationNode opp = this.getLocationNode(oppositeNodeID); // will also add nodes to _pagedNodes
				neighbor = new Node(opp);
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
	private LocationNode getLocationNode(String nodeID) throws IOException {

		List<LocationNode> pageOfNodes = fileUtility.getNodePage(nodeID);
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
		System.out.printf("Paged in %s new location nodes. Size of _pagedNodes is %s\n\n", pageOfNodes.size(), _pagedNodes.size());
		return toReturn;
	}
	
	
	/*** for testing only ***/
	public Set<LocationNode> dummyGetReceivers(String nodeID) throws IOException{
		LocationNode a = fileUtility.getLocationNode(nodeID);
		Map<Node, String> map = getConnectedNodes(new Node(a));
		Set<LocationNode> as = new HashSet<>();
		for(Node nn : map.keySet()){
			as.add(nn.locNode);
		}
		return as;
	}


	
	
	
	
	
	
	
	
	
	
	/**
	 * given a priority queue and an LocationNode,
	 * return the node in the priority queue that contains the
	 * same LocationNode.
	 * 
	 * if no such node, return null.
	 * 
	 * @param pq
	 * @param LocationNode
	 * @return node containing LocationNode, or null
	 */
	private Node getNodeWithLocationNode(PriorityQueue<Node> pq, LocationNode locNodeToGet){
		for(Node n : pq) {
			if(n.locNode.equals(locNodeToGet)){
				return n;
			}
		}
		return null;
	}
}
