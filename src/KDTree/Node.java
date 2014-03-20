package KDTree;

import KDTree.KDTree.Axis;

public class Node {
	public final String ID;
	public final Double x;
	public final Double y;
	private Node _leftChild;
	private Node _rightChild;

	/*
		>Purpose:
		>Input:
		>Output:
		>Throws:
	*/
	public Node(String ID, Double x, Double y){
		this.ID = ID;
		this.x = x;
		this.y = y;
	}
	
	public Double getSplitAxis(Axis axis) {
		switch (axis){
			case X:
				return x;
			default:
				return y;
		}
	}
	
	public void setRightChild(Node n){
		_rightChild = n;
	}
	
	public void setLeftChild(Node n){
		_leftChild = n;
	}
	
	public Node getRightChild(){
		return _rightChild;
	}
	
	public Node getLeftChild(){
		return _leftChild;
	}
	
	public Boolean hasLeftChild(){
		return (_leftChild != null);
	}
	
	public Boolean hasRightChild(){
		return (_rightChild != null);
	}
	
}
