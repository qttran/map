package KDTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import KDTree.Node;

public class KDTree {

	private Hashtable<String, Node> _starsWithNames;
	private List<Node> _starsToAdd;
	private List<Node> _starsForNaive;
	public final Node _root;

	public KDTree(List<Node> starsToAdd) {
		_starsWithNames = new Hashtable<String, Node>();
		_starsToAdd = starsToAdd;
		_starsForNaive = new ArrayList<Node>();
		_root = RecursiveKDBuilder(_starsToAdd, 0);
	}

	// get an approximate median -> get comparison axis -> compare to median -> put into different children
	private Node RecursiveKDBuilder(List<Node> stars, Integer depth){
		if (stars.size() == 1) return stars.get(0);
		Axis axis = Axis.getAxis(depth);
		Node median = MedianHeuristic(stars, axis);
		stars.remove(median);

		List<Node> leftStars = new ArrayList<Node>();
		List<Node> rightStars = new ArrayList<Node>();

		switch (axis){
		case X:
			for (Node star : stars) {
				if (star.x > median.x)	rightStars.add(star);
				else leftStars.add(star);
			}
			break;
		default:
			for (Node star : stars) {
				if (star.y > median.y)	rightStars.add(star);
				else leftStars.add(star);
			}
		}

		if (rightStars.size() > 0) median.setRightChild(RecursiveKDBuilder(rightStars, depth + 1));
		if (leftStars.size() > 0) median.setLeftChild(RecursiveKDBuilder(leftStars, depth + 1));
		return median;
	}

	// get a random sample of nodes -> sort them according to the comparison axis -> return the middle element. 
	private Node MedianHeuristic(List<Node> stars, Axis axis){
		List<Integer> indices = new ArrayList<Integer>();

		for (Integer index = 0; index< stars.size(); index++) {
			indices.add(index);
		}

		Collections.shuffle(indices);
		Integer sampleSize;
		if (stars.size() > 1000) sampleSize = 1000;
		else sampleSize = stars.size();
		ComparableNode[] sample = new ComparableNode[sampleSize];

		switch (axis) {
		case X:
			for (int i = 0; i < sampleSize; i++) {
				Node n = stars.get(indices.remove(0));
				sample[i] = new ComparableNode(n.x, n);
			}
			break;
		default:
			for (int i = 0; i < sampleSize; i++) {
				Node n = stars.get(indices.remove(0));
				sample[i] = new ComparableNode(n.y, n);
			}
		}

		Arrays.sort(sample);
		if (sampleSize % 2 == 0) return sample[sampleSize / 2].n;
		else return sample[(sampleSize - 1) / 2].n;
	}

	
	public enum Axis {
		X, Y;	

		static Axis getAxis(Integer depth){//nodes with same depth share a comparison axis.
			Integer i = depth % 2;
			switch (i){
			case 0:
				return X;
			default:
				return Y;
			}
		}
	}

	private class ComparableNode implements Comparable<ComparableNode>{
		private final Double d;
		private final Node n;
		private ComparableNode(Double d, Node n) {
			this.d = d;
			this.n = n;
		}
		@Override
		public int compareTo(ComparableNode o) {
			if (o.d > this.d) return 1;
			if (o.d < this.d) return -1;
			return 0;
		}
	}

	public Node getRoot(){
		return _root;
	}

	public Node[] KDSearch(String name, Integer range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		return KDSearcher.KDSearch(target, range, _root, 0, new Stack<Node>());
	}

	public Node[] KDSearch(Node target, Integer range){
		return KDSearcher.KDSearch(target, range, _root, 0, new Stack<Node>());
	}

	public Node[] KDRangeSearch(String name, Double range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		return KDSearcher.KDRangeSearch(target, range, _root, 0, new Stack<Node>());
	}

	public Node[] KDRangeSearch(Node target, Double range){
		return KDSearcher.KDRangeSearch(target, range, _root, 0, new Stack<Node>());
	}

	public Node[] NaiveSearch(Node target, Integer range){
		List<Node> nodes = KDSearcher.NaiveSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveSearch(String name, Integer range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		List<Node> nodes = KDSearcher.NaiveSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveRangeSearch(Node target, Double range){
		List<Node> nodes = KDSearcher.NaiveRangeSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveRangeSearch(String name, Double range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		List<Node> nodes = KDSearcher.NaiveRangeSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}
}