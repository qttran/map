package Autocomplete;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import static java.lang.Math.*;

public class Trie {
	private Node _root; 
	
	/*
		>Purpose: 
			constructor that creates a new trie with a root node.
		>Input: --
		>Output: --
		>Throws: -- 
	*/
	public Trie() {
		_root = new Node("root");	
	}
	
	/*
		>Purpose: 
			this method adds a word to the trie by searching through the trie and 
			adding new nodes where necessary.
		>Input: s - word that will be added to the trie.
		>Output: --
		>Throws: -- 
	*/
	private void addToTrie(String s){
		Node currentNode = _root;
		char[] letters = s.toCharArray();
		for (int i=0; i<letters.length; i++){
			char letter = letters[i];
			Boolean isFound = false;
			if (!currentNode.getChildren().isEmpty()) {
				for (Node n : currentNode.getChildren()) {
					if (n._letter == letter) {
						currentNode = n;
						isFound = true;
						break;
					}
				}	
			}
			if (!isFound) {
				if (i==letters.length-1) {
					currentNode = currentNode.addChild(letter,true);
				}
				else currentNode = currentNode.addChild(letter, false);
			}
			else if (i==letters.length-1) currentNode.setWord();
		}
	}

	/*
		>Purpose: 
			searches the trie for the given string. returns null if the string is not in
			the trie.
		>Input: s - string that will be searched for.
		>Output: Node - the node that represents the input string.
		>Throws: -- 
	*/
	public Node searchTrie(String s){
		Node currentNode = _root;
		LinkedList<Character> charQueue = new LinkedList<Character>();
		for (Character letter : s.toCharArray()){
			charQueue.add(letter);
		}
		while (!charQueue.isEmpty()){
			Boolean isFound = false;
			if (currentNode.getChildren().isEmpty()) return null;
			for (Node n : currentNode.getChildren()) {
				if (n._letter == charQueue.peek()) {
					charQueue.pop();
					currentNode = n;
					isFound = true;
					break;
				}
			}
			if (!isFound) return null;
		}
		return currentNode;
	}
	
	/*
		>Purpose: 
			lists all the strings that starts with the given prefix.
		>Input: s - the prefix that will be searched.
		>Output: ArrayList<String> a list of all the strings that start with the prefix.
		>Throws: -- 
	*/
	public ArrayList<String> getPrefixMatch(String s) {
		Node n = searchTrie(s);
		if (n==null) return new ArrayList<String>();
		ArrayList<String> stringsToReturn = new ArrayList<String>();
		Stack<Node> stack = new Stack<Node>();
		stack.push(n);
		
		while (!stack.isEmpty()){
			Node node = stack.pop();
			
			if (node.isWord()) {
				stringsToReturn.add(node._key);
			}
			
			for (Node child : node.getChildren()) {
				stack.push(child);
			}
		}
		return stringsToReturn;
	}
	
	/*
		>Purpose: 
			lists all the words that are at most the specified number of Edit Distances away
			from the input string.
		>Input: string1 - the input string.
				d - the maximum number of edit distances.
		>Output: - list of all the found words.
		>Throws: -- 
	*/
	public ArrayList<String> getLED(String string1, int d){
		ArrayList<String> stringsToReturn = new ArrayList<String>();
		int l1 = string1.length()+1; 
		Stack<Node> stack = new Stack<Node>();
		stack.push(_root);
		
		while (!stack.isEmpty()){
			Node node = stack.pop();
			if (node.isWord()) {
				String string2 = node._key;
				int[] currentRow = new int[l1];
				int[] previousRow = new int[l1];
				int l2 = string2.length()+1;

				for (int col = 0; col<l1; col++) previousRow[col] = col;

				for (int row = 1; row<l2; row++) {
					currentRow = new int[l1];
					currentRow[0] = previousRow[0] + 1;

					for (int col = 1; col<l1; col++){
						if (string1.charAt(col-1) == string2.charAt(row-1)) {
							currentRow[col] = previousRow[col - 1];
						}
						else {
							int replaceDistance = previousRow[col - 1] + 1;
							int insertDistance = currentRow[col - 1] + 1;
							int removeDistance = previousRow[col] + 1;
							currentRow[col] = min(min(insertDistance,removeDistance),replaceDistance);
						}
					}
					previousRow = currentRow;
				}
		
				if (currentRow[l1-1]<=d) stringsToReturn.add(node._key);

				for (Node child : node.getChildren()) {
					stack.push(child);
				}
			}
			else {
				for (Node child : node.getChildren()) {
					stack.push(child);
				}
			}
		}
		return stringsToReturn;
	}
	

	/*
		>Purpose: 
			returns all the possible ways to split the input word into two meaningful words
		>Input: s - input string
		>Output: ArrayList<String> - list of all the possible two word splits.
		>Throws: -- 
	*/
	public ArrayList<String> getWordSplit(String s) {
		ArrayList<String> stringsToReturn = new ArrayList<String>();
		Stack<Node> nodeStack = new Stack<Node>();
		LinkedList<Character> charQueue = new LinkedList<Character>();
		for (Character letter : s.toCharArray()){
			charQueue.add(letter);
		}
		nodeStack.push(_root);
		
		while (!nodeStack.isEmpty()){
			Node node1 = nodeStack.pop();
			
			if (node1.isWord()) {
				String k = s.substring(node1._key.length());
				Node node2 = searchTrie(k); 
				if (node2 != null && node2.isWord()) {
					stringsToReturn.add(node1._key + " " + k);
				}
			}
			if (!charQueue.isEmpty()){
				Character c = charQueue.pop();
				for (Node child : node1.getChildren()) {
					if(child._letter == c) nodeStack.push(child);
				}	
			}
		}
		return stringsToReturn;
	}
	
	/*
		>Purpose: 
			adds all the words in a given list to the trie.
		>Input: words - the strings that will be inserted into the trie.
		>Output: --
		>Throws: -- 
	*/
	public void insertToTrie(ArrayList<String> words){
		for (String s : words) {
			if(s.equals("")) { //no letters in this string
				continue;
			}
			addToTrie(s);
		}
	}
}
