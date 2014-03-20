package KDTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;
import KDTree.Node;

public class KDTree {

	private Hashtable<String, Node> _starsWithNames;
	private Hashtable<String, Node> _stars;
	private ArrayList<Node> _starsToAdd;
	private ArrayList<Node> _starsForNaive;
	public final Node _root;

	public KDTree(Hashtable<String, Node> stars, ArrayList<Node> starsToAdd) {
		_starsWithNames = new Hashtable<String, Node>();
		_stars = stars;
		_starsToAdd = starsToAdd;
		_starsForNaive = new ArrayList<Node>();
		//this.ParseStarData(fileLocation);
		_root = RecursiveKDBuilder(_starsToAdd, 0);
	}

	private Node RecursiveKDBuilder(ArrayList<Node> stars, Integer depth){
		if (stars.size() == 1) return stars.get(0);
		Axis axis = Axis.getAxis(depth);
		Node median = MedianHeuristic(stars, axis);
		stars.remove(median);

		ArrayList<Node> leftStars = new ArrayList<Node>();
		ArrayList<Node> rightStars = new ArrayList<Node>();

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

	private Node naiveHeuristic(ArrayList<Node> stars, Axis axis){
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (Integer index = 0; index< stars.size(); index++) {
			indices.add(index);
		}

		Collections.shuffle(indices);
		Integer sampleSize;
		sampleSize = stars.size();
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

	private Node MedianHeuristic(ArrayList<Node> stars, Axis axis){
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (Integer index = 0; index< stars.size(); index++) {
			indices.add(index);
		}

		Collections.shuffle(indices);
		Integer sampleSize;
		if (stars.size() > 1000) sampleSize = 1000;
		else sampleSize = stars.size();
		//System.out.println("sampleSize: " + sampleSize);
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

		static Axis getAxis(Integer depth){
			Integer i = depth % 3;
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
		ArrayList<Node> nodes = KDSearcher.NaiveSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveSearch(String name, Integer range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		ArrayList<Node> nodes = KDSearcher.NaiveSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveRangeSearch(Node target, Double range){
		ArrayList<Node> nodes = KDSearcher.NaiveRangeSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] NaiveRangeSearch(String name, Double range){
		Node target = _starsWithNames.get(name);
		if (target == null) {
			System.out.println("ERROR: No such star.");
			Node[] temp = new Node[0];
			return temp;
		}
		ArrayList<Node> nodes = KDSearcher.NaiveRangeSearch(_starsForNaive, target, range);
		return nodes.toArray(new Node[nodes.size()]);
	}
}