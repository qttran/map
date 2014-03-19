package Autocomplete.old;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
/**
 * TrieNode class
 * 
 * 
 * Children are represented in an ArrayList of size 26 (_children)
 * if there is a child repping char 'a' it is stored at _children.get(0),
 * a child 'b' is stored at _children.get(1), etc... if there is
 * no child there the array slot is null.
 * 
 * @author mcashton
 *
 */
public class TrieNode {

	private List<TrieNode> _children;
	private String _word; //IF it is a real word, what is the word

	public TrieNode() {
		_word="";
		
		//initialize an arraylist of 64 nulls
		_children = new ArrayList<TrieNode>(64);
		
		//32 - 96 reg uppercase  (64)
		
		
		for(int i = 0; i<65; i++){
			_children.add(i, null);
		}
	}
	/**
	 * add a new child at char 'c'
	 * throws exception if node already has a child there
	 * @param c
	 * @return
	 */
	public TrieNode addChild(char c) {
		int i = getIndexFromChar(c);
		Preconditions.checkState(_children.get(i)==null); //child should not already exist
		TrieNode newChild = new TrieNode();
		_children.set(i, newChild);
		return newChild;
	}
	
	
	public boolean isLeaf(){
		for(int i = 0; i<65;i++){ 
			if(_children.get(i)!=null)
				return false;
		}
		return true;
	}

	
	
	/**** word status ****/
	
	
	public boolean isWord(){
		return _word!="";
	}
	
	public void setIsWord(String s) {
		_word = s;
	}
	
	public String getWord(){
		Preconditions.checkState(this.isWord()); //should only be retrieving word if this actually is a word
		return _word;
	}
	
	
	/*** child-getters ***/
	
	public TrieNode getChildAtChar(char c){
		int i = getIndexFromChar(c);
		return _children.get(i);
	}
	public TrieNode getChildAtIndex(int i){
		Preconditions.checkArgument(i>=0 && i<65);
		return _children.get(i);
	}
	

	/** helper method to get an array index from a char
	 * 'a' -> 0
	 * 'b' -> 1
	 * 'z' -> 25
	 * @param c
	 * @return int
	 */
	private static int getIndexFromChar(char c) {
		Preconditions.checkArgument((int) c>31 && (int) c<97 ); 
		return (int) c - 31;
	}
	

}




