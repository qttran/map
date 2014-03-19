package KDTree;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class KDTree {	
	public KDNode root;

	/** SPLAY NODE CLASS
	 */
	private static class KDNode{
		Coordinates item;
		String id;
		KDNode left = null;
		KDNode right = null;
		KDNode parent = null;

		public KDNode(String id, Coordinates item) {
			this.id = id;
			this.item = item;
		}

		public String toString() {
			String result = item.toString() + ", ";
			if (left != null) result += left.toString();
			if (right != null) result +=  right.toString();
			return result;
		}
	}


	
	/** LOOK UP METHOD
	 */
	public boolean lookup(Coordinates item) {
		Tuple<KDNode, Integer> tuple = this.search(item);
		if (tuple.y == null) return false;
		else return (tuple.y == 0);
	}


	/** INSERT METHOD
	 */
	public void insert(String id, Coordinates item) {
		Tuple<KDNode, Integer> tuple = this.search(item);
		KDNode node = new KDNode(id, item);
		if (tuple.y == null){
			this.root = node;
		}
		else if (tuple.y == 0) {
			return;
		}
		else if (tuple.y == 1) {
			tuple.x.right = node;
			node.parent = tuple.x;
		}
		else if (tuple.y == -1){
			tuple.x.left = node;
			node.parent = tuple.x;
		}

		// splay the node
		while (!node.item.equals(root.item)) {
			this.splay(node);
		}
	}

	
	
	/**SEARCH DISTANCE */
	public List<String> searchDistance(int r, Coordinates item, KDNode node) {
		List<String> result = new ArrayList<String>();
		PriorityQueue<Tuple<KDNode,Double>> pQueue = searchDistanceHelper(r,item,node);

		while (!pQueue.isEmpty()) {
			result.add(pQueue.poll().x.id);
		}
		result.add("");
		// SORT RESULT
		
		return result;
	}


	/** SEARCH NUMBER */
	public List<String> searchNumber(int n, Coordinates item) {
		
		KDNode node = search(item).x;
		PriorityQueue<Tuple<KDNode,Double>> pQueue = new PriorityQueue<Tuple<KDNode, Double>>();
		List<String> result = new ArrayList<String>();
		if (n == 0) {
			result.add("");
			return result;
		}

		Tuple<KDNode, Double> closest = new Tuple<KDNode, Double>(node,0.0);
		pQueue.add(closest);

		do {
			//add the small
			KDNode newClosestBig = searchClosestBig(closest.x);
			//System.out.println(newClosestBig.id);
			if ((newClosestBig != null) && (!result.contains(newClosestBig.id))){
				double newClosestBigScore = Math.abs(newClosestBig.item.compareTo(node.item));
				Tuple<KDNode,Double> newBigTuple = new Tuple<KDNode,Double> (newClosestBig,newClosestBigScore);
				pQueue.add(newBigTuple);
			}
			//add the big
			KDNode newClosestSmall = searchClosestSmall(closest.x);
			if ((newClosestSmall != null)  && (!result.contains(newClosestSmall.id))) {
				double newClosestSmallScore = Math.abs(newClosestSmall.item.compareTo(node.item));
				Tuple<KDNode,Double> newSmallTuple = new Tuple<KDNode,Double> (newClosestSmall,newClosestSmallScore);
				pQueue.add(newSmallTuple);
			}
			//poll
			while (result.contains(closest.x.id)) {
				if (pQueue.size() == 0) {
					result.add("");
					return result;
				}
				closest = pQueue.poll();
			}
			result.add(closest.x.id);
		} 
		while (result.size() < n);
		result.add("");
		return result;
	}

	
	/** SEARCH NUMBER COORDINATES */
	public List<Coordinates> searchNumberCoordinates(int n, Coordinates item) {
		
		KDNode node = search(item).x;
		PriorityQueue<Tuple<KDNode,Double>> pQueue = new PriorityQueue<Tuple<KDNode, Double>>();
		List<Coordinates> result = new ArrayList<>();
		if (n == 0) {
			return result;
		}

		Tuple<KDNode, Double> closest = new Tuple<KDNode, Double>(node,0.0);
		pQueue.add(closest);

		do {
			//add the small
			KDNode newClosestBig = searchClosestBig(closest.x);
			//System.out.println(newClosestBig.id);
			if ((newClosestBig != null) && (!result.contains(newClosestBig.id))){
				double newClosestBigScore = Math.abs(newClosestBig.item.compareTo(node.item));
				Tuple<KDNode,Double> newBigTuple = new Tuple<KDNode,Double> (newClosestBig,newClosestBigScore);
				pQueue.add(newBigTuple);
			}
			//add the big
			KDNode newClosestSmall = searchClosestSmall(closest.x);
			if ((newClosestSmall != null)  && (!result.contains(newClosestSmall.id))) {
				double newClosestSmallScore = Math.abs(newClosestSmall.item.compareTo(node.item));
				Tuple<KDNode,Double> newSmallTuple = new Tuple<KDNode,Double> (newClosestSmall,newClosestSmallScore);
				pQueue.add(newSmallTuple);
			}
			//poll
			while (result.contains(closest.x.id)) {
				if (pQueue.size() == 0) {
					return result;
				}
				closest = pQueue.poll();
			}
			result.add(closest.x.item);
		} 
		while (result.size() < n);
		return result;
	}

	/**SEARCH THE CLOSEST NODE THAT 
	 * IS SMALLER THAN A NODE */
	private KDNode searchClosestSmall(KDNode node) {
		KDNode left = node.left;
		KDNode toFind = left;
		while ((left!= null) && (left.right != null)) {
			toFind = left.right;
			left = toFind;
		}
		if (toFind == null) {
			KDNode parent = node.parent;
			KDNode current = node;
			if (parent == null) return toFind;
			while ((parent.right == null)||
					(parent.right.item.compareTo(current.item) != 0)) {
				parent = parent.parent;
				current = current.parent;
				if (parent  == null) return toFind;
			}
			toFind = parent;
		}
		return toFind;
	}

	/**SEARCH THE CLOSEST NODE THAT 
	 * IS BIGGER THAN A NODE */
	private KDNode searchClosestBig(KDNode node) {
		KDNode right = node.right;
		KDNode toFind = right;
		while ((right!= null) && (right.left != null)) {
			toFind = right.left;
			right = toFind;
		}
		if (toFind == null) {
			KDNode parent = node.parent;
			KDNode current = node;
			if (parent == null) return toFind;
			while ((parent.left == null)||
					(parent.left.item.compareTo(current.item) != 0)) {
				parent = parent.parent;
				current = current.parent;
				if (parent  == null) return toFind;
			}
			toFind = parent;
		}
		return toFind;
	}


	/**SEARCH DISTANCE HELPER */
	private PriorityQueue<Tuple<KDNode,Double>> searchDistanceHelper(int r, Coordinates item, KDNode node) {
		PriorityQueue<Tuple<KDNode,Double>> pQueue = new PriorityQueue<Tuple<KDNode, Double>>();

		if (r == 0) {
			return pQueue;
		}
		if (node != null) {
			double difference = item.distance(node.item);
			double compare = node.item.compareTo(item);
			if (Math.abs(difference) <= r*r) {
				pQueue.add(new Tuple<KDNode, Double>(node,difference));
				PriorityQueue<Tuple<KDNode,Double>> toAdd = searchDistanceHelper(r,item,node.right);
				PriorityQueue<Tuple<KDNode,Double>> toAdd2 = searchDistanceHelper(r,item,node.left);
				pQueue.addAll(toAdd);
				pQueue.addAll(toAdd2);
			}
			else if (difference > r*r) {
				if (compare > 0) {
					PriorityQueue<Tuple<KDNode,Double>> toAdd = searchDistanceHelper(r,item,node.right);
					pQueue.addAll(toAdd);
				} else {
					PriorityQueue<Tuple<KDNode,Double>> toAdd = searchDistanceHelper(r,item,node.left);
					pQueue.addAll(toAdd);
				}
			} 
		}
		return pQueue;
		
	}
	
	/** ROTATE METHOD
	 */
	private void rotate(KDNode x) {
		if ((x != null) && (x.parent != null)) {
			KDNode y = x.parent;
			//connect x with y's parent if there is one
			x.parent = y.parent;
			if (y.parent != null) {
				if (y.parent.item.compareTo(y.item) < 0) 
					y.parent.right = x;
				else y.parent.left = x;
			}
			// CW
			if (x.item.compareTo(y.item) < 0) {
				KDNode b = x.right;
				x.right = y;
				y.parent = x;
				y.left = b;
				if (b != null) {
					b.parent = y;
				}
			}
			// CCW
			else {
				KDNode b = x.left;
				x.left = y;
				y.parent = x;
				y.right = b;
				if (b != null) {
					b.parent = y;
				}
			}
		}
	}

	/** SPLAY METHOD 
	 */
	private void splay(KDNode x) {
		if (x.parent != null) {
			KDNode p = x.parent;
			//Zig step: p is the root
			if (p.parent == null) {
				this.rotate(x);
				this.root = x;
			}
			else {
				KDNode g = p.parent;
				//Zig-zig step: p is not the root and 
				// x and p are either both right or left children
				if ((x.item.compareTo(p.item) * p.item.compareTo(g.item)) > 0) {
					this.rotate(p);
					this.rotate(x);
					if (x.parent == null) 
						this.root = x;
				}
				//Zig-zag step: p is not the root and 
				//x is a right child and p is a left child or vice versa
				else {
					this.rotate(x);
					this.rotate(x);
					if (x.parent == null) 
						this.root = x;
				}
			}

		}
	}
	
	/** SEARCH METHOD
	 * Input: ITEM type T
	 * Output: returns a Tuple(Binary Node, Integer)
	 * If the tree is empty, return Tuple(null,null)
	 * Otherwise, if ITEM is already in the tree, return Tuple(that node, 0)
	 * if the node is not in the tree, return the parent of the node 
	 * where ITEM is supposed to be put in and 1 if ITEM on the right of that parent,
	 * and -1 if ITEM should be put on the left of that parent.
	 */
	private Tuple<KDNode, Integer> search(Coordinates item) {
		KDNode current = root;
		Tuple<KDNode, Integer> tuple = new Tuple<KDNode, Integer>(null, null);
		while (current != null) {
			if (current.item.compareTo(item) == 0) {
				tuple.x = current;
				tuple.y = 0;
				current = null;
			}
			else if (current.item.compareTo(item) < 0) {
				tuple.x = current;
				tuple.y = 1;
				current = current.right;
			}
			else {
				tuple.x = current;
				tuple.y = -1;
				current = current.left;
			}
		}
		return tuple;
	}
	

	
	/** DELETE METHOD
	 * METHOD NEVER USED
	 */
	public void delete(Coordinates item) {
		Tuple<KDNode, Integer> tuple = this.search(item);
		if ((tuple.y == null) || (tuple.y != 0)) 
			System.out.println("The item is not found in the tree therefore cannot be deleted");
		else {
			KDNode nodeToDelete = tuple.x;
			// Delete a node with no child
			if ((nodeToDelete.left == null) && (nodeToDelete.right == null)) {
				if (nodeToDelete.parent == null) {
					root = null;
				}
				else if (item.compareTo(nodeToDelete.parent.item) > 0) {
					nodeToDelete.parent.right = null;
				}
				else {
					nodeToDelete.parent.left = null;
				}
			}
			// Delete a node with 1 child.
			else if ((nodeToDelete.left == null) || (nodeToDelete.right == null)) {
				KDNode childNode = new KDNode("",null);
				if (nodeToDelete.left != null) childNode = nodeToDelete.left;
				else childNode = nodeToDelete.right;

				if (nodeToDelete.parent == null) {
					root = childNode;
					childNode.parent = null;
				}
				else if (item.compareTo(nodeToDelete.parent.item) > 0) {
					childNode.parent = tuple.x.parent;
					if (nodeToDelete.parent != null)
						nodeToDelete.parent.right = childNode;
				}
				else {
					childNode.parent = tuple.x.parent;
					if (nodeToDelete.parent != null)
						nodeToDelete.parent.left = childNode;
				}
			}
			//Delete a node with 2 children
			else {

				//find right-most child of the left nodeToDelete
				KDNode rightMostOfLeft = new KDNode("",null);
				rightMostOfLeft = nodeToDelete.left;
				while (rightMostOfLeft.right != null) {
					rightMostOfLeft = rightMostOfLeft.right;
				}

				KDNode leftNodeofRightMostOfLeft = rightMostOfLeft.left;
				KDNode parentofRightMostOfLeft = rightMostOfLeft.parent;

				//replace nodeToDelete with rightMostOfLeft
				nodeToDelete.item = rightMostOfLeft.item;

				// delete rightMostOfLeft 
				if (rightMostOfLeft.item.compareTo(parentofRightMostOfLeft.item) > 0) {
					parentofRightMostOfLeft.right = leftNodeofRightMostOfLeft;
					if (leftNodeofRightMostOfLeft != null)
						leftNodeofRightMostOfLeft.parent = parentofRightMostOfLeft ;
				}
				else {
					parentofRightMostOfLeft.left = leftNodeofRightMostOfLeft;
					if (leftNodeofRightMostOfLeft != null)
						leftNodeofRightMostOfLeft.parent = parentofRightMostOfLeft;
				}
			}

			// Splay x's parent to the root
			KDNode p = tuple.x.parent;
			if ((root != null) && (p != null)) {
				while (p.item.compareTo(root.item) != 0) {
					this.splay(p);
				}
			}
		}
	}
}