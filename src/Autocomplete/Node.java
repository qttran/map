package Autocomplete;

import java.util.ArrayList;

public class Node {
	public final Character _letter;
	public final String _key;
	private Boolean _isWord;
	private ArrayList<Node> _children;
	
	
	/*
		>Purpose:
			constructer creates a node to be store in the trie.
		>Input: c - the letter that is represented by this node.
				k - the word that is stored in this node.
				b - whether this word is one that makes sense (is it in a corpus?).
		>Output: --
		>Throws: -- 
	*/
	public Node(char c, String k, Boolean b){
		_key = k;
		_letter = c;
		_isWord = b;
		_children = new ArrayList<Node>(26);
	}
	
	/*
		>Purpose: alternative constructor specifically for the root of the trie.
		>Input: --
		>Output: --
		>Throws: -- 
	*/
	public Node(String s){
		if (s == "root"){
			_children = new ArrayList<Node>(26);
		}
		else System.out.println("ERROR: invalid input");
		_letter = (Character) null;
		_key = "";
		_isWord = false;
	}
	
	/*
		>Purpose: creates a new node and adds it as a child. 
		>Input: c - the character that will be represented by the new node.
				b - is the new node the last letter of an English word.
		>Output: Node - new child node.
		>Throws: -- 
	*/
	public Node addChild(char c, Boolean b){
		Node n = new Node(c, _key+(Character.toString(c)), b);
		_children.add(n);
		return n;
	}
	
	//getters and setters
	public ArrayList<Node> getChildren(){
		return _children;
	}
	
	public void setWord(){
		_isWord = true;
	}
	
	public Boolean isWord(){
		return _isWord;
	}
	
}
