package PathFinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.base.Preconditions;

import cs32.maps.LocationNode;
import cs32.maps.MapsMathUtility;
import cs32.maps.Way;
import cs32.maps.FileReader.*;

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
		public double f_score = 0;
		public Node predecessor = null;
		public String predConnection = ""; //unweighted connection to predecessor
		public Node(LocationNode a) {
			locNode = a;
		}
		@Override
		public boolean equals(Object o){  //nodes are equal if they hold the same LocationNode
			final Node other = (Node) o;
			return locNode.equals(other.locNode); 
		}
		public void setPredecessor(Node pred, String conn) {
			predecessor = pred;
			predConnection = conn;
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

	public PathFinder(MapsIO f){
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
			}
			return path;
		}

		return null; //no path
	}

	private static String getConnectionString(String nodeid1, String nodeid2, String wayid){
		return nodeid1 + " -> " + nodeid2 + " : " + wayid;
	}



	/**
	 */
	private Node aStarSearch(Node start, Node goal) throws IOException {

		start.g_score = 0;
		start.f_score = start.g_score + heuristic(start, goal);

		PriorityQueue<Node> pq = new PriorityQueue<>(50, NodeComparator);
		pq.add(start);		

		Node curr;

		while(!pq.isEmpty()) {

			curr = pq.poll(); //remove 'curr' from PQ

			if(curr.locNode.id.equals(goal.locNode.id)) { // found the best path!
				return curr;
			}

			// add curr to closed set?

			List<Node> connectedNodes = getConnectedNodes(curr); 


			/* FOR EACH NODE 'b' THAT IS CONNECTED TO  'curr' */
			for(Node b : connectedNodes ) {

				// if the LocationNode in Node b is already in the pq, change
				// its dist to be whats in the openSet
				boolean isInPQ = false;
				Node nodeWithThisLocationNode = getNodeWithLocationNode(pq, b.locNode);
				if(nodeWithThisLocationNode!=null){
					b.g_score = nodeWithThisLocationNode.g_score;
					b.f_score = nodeWithThisLocationNode.f_score;
					isInPQ = true;
				}

				// 'pre' is the connection between 'b' and 'curr'
				Node pred = b.predecessor;
				Preconditions.checkState(pred.equals(curr));


				double b_new_g_score = curr.g_score; // + dist_between(curr, b)

				boolean thisPathIsBetter = b_new_g_score < b.g_score;


				if(isInPQ && thisPathIsBetter) {
					b.g_score = b_new_g_score;
					b.f_score = b.g_score + heuristic(b, goal);
					// remove the old reference to this LocationNode and add the new one
					// ****this will update the priority and also the 'predecessor' reference
					pq.remove(b);
					pq.offer(b);

				}
				else if(!isInPQ){
					//predecessor info is already set
					b.g_score = b_new_g_score;
					pq.offer(b);
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





	/**
	 * - get list of possible bacon receivers given a start Node
	 * - creates all NEW NODES, so their 'predecessor' is set to 'start'
	 * and their 'dist' is set to infinity.
	 * 
	 * NOTE: these LocationNodes returned MAY already be in the PQ. Taken care
	 * of in dijkstra's.
	 */
	private List<Node> getConnectedNodes(Node start) throws IOException {
		List<Node> receivers = new ArrayList<>();

		for(String wayID : start.locNode.ways) { // for every WAY connected to 'start'

			Way w = fileUtility.getWay(wayID);
			String oppositeNodeID = "";
			if(w.startNodeID == start.locNode.id) {
				oppositeNodeID = w.endNodeID;
			}
			else if (w.endNodeID == start.locNode.id) {
				oppositeNodeID = w.startNodeID;
			}
			else {
				System.out.println("ERROR");
			}

			LocationNode opp = fileUtility.getLocationNode(oppositeNodeID);
			
			Node nodeToAdd = new Node(opp);
			nodeToAdd.setPredecessor(start, wayID);
			receivers.add(nodeToAdd);			
		}
		return receivers;
	}



	/*** for testing only ***/
	public Set<LocationNode> dummyGetReceivers(String nodeID) throws IOException{
		LocationNode a = fileUtility.getLocationNode(nodeID);
		List<Node> map = getConnectedNodes(new Node(a));
		Set<LocationNode> as = new HashSet<>();
		for(Node nn : map){
			as.add(nn.locNode);
		}
		return as;
	}


}
