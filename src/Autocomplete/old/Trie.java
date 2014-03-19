package Autocomplete.old;
import java.util.HashSet;
import java.util.Set;
import com.google.common.base.Preconditions;

public class Trie {

	private TrieNode _root;

	public Trie() {
		_root = new TrieNode();
	}
	
	

	/**
	 * addWord
	 *  -> calls recursive func. insert(root, word)
	 * @param word to add to the Trie
	 * @return added TrieNode. If word already existed in Trie,
	 * return the existing TrieNodes.
	 */
	public TrieNode addWord(String word) {
		Preconditions.checkArgument(isValidWord(word.toUpperCase()));
		
		TrieNode newNode = insert(_root, word.toUpperCase()); //uppercase in tree
		newNode.setIsWord(word); //word var holds original case

		return newNode;
	}


	/**
	 * insert(node, word)
	 * 
	 * insert a string into trie. If TrieNode for this
	 * string already exists, just return it. (The caller,
	 * addWord(), takes are of incrementing occurrences
	 * if this is the case).
	 * 
	 * @param node
	 * @param word
	 * @return the new or existing TrieNode that is the end of 'word'
	 */
	private TrieNode insert(TrieNode node, String word) {
		if(word.length()==0) {
			return node;
		}
		char first = word.charAt(0);
		TrieNode child = node.getChildAtChar(first);

		//if char is not already a child, create it
		if(child == null) {
			child = node.addChild(first); //add a child with that first char
		}
		return insert(child, word.substring(1, word.length()));
	}

	
	
	
	//32 - 96
	//lowercase 97 - 122
	//123 - 126 
	/* if word is a valid word to add to trie */
	public static boolean isValidWord(String word) {
		for(char c : word.toCharArray()) {
			if ((int) c<32 || (int) c>96 ) { 
				System.out.println("ALERT char _"+c+"_ is invalid");
				return false;
			}
		}
		return true;
	}
	
	/* is empty boolean */
	public boolean isEmpty() {
		return _root.isLeaf();
	}

	
	
	
	/******** finding prefixes **********/
	
	
	/**
	 * simple function to call find() without having
	 * to give root.
	 * @param s
	 * @return found TrieNode or null
	 */
	public TrieNode getPrefixNode(String s){
		return this.find(_root, s);
	}

	/**
	 * finds end node of string "str"
	 * end node may be a real word or a prefix
	 * if str prefix does not exist, returns null
	 * 
	 * @param node
	 * @param str
	 * @return found node or null
	 */
	private TrieNode find(TrieNode node, String str) {
		if(str.length()==0) {
			return node;
		}
		
		str = str.toUpperCase();
		
		char first_char = str.charAt(0); 
		
		TrieNode child = node.getChildAtChar(first_char);
				
		if(child==null) // at the end of the string 
			return null;
		return find(child, str.substring(1, str.length()));
	}
	
	
	
	
	/**
	 * getCompletions
	 * 
	 * @param prefix
	 * @return Set of Strings that are completions of the prefix
	 */
	public Set<String> getCompletions(String prefix){
		Set<String> ret = new HashSet<>();
		
		TrieNode prefixNode = this.getPrefixNode(prefix);
		
		if(prefixNode == null) //if trie does not contain this prefix
			return ret;
		
		Set<TrieNode> nodes = getEndWords(prefixNode, new HashSet<TrieNode>());
		
		//extract strings and return them
		for(TrieNode n : nodes){
			Preconditions.checkState(n.isWord());
			ret.add(n.getWord());
		}
		
		return ret;
	}
	
	/**
	 * getEndWords: private helper method for getCompletions
	 * 
	 * 
	 * @param pre
	 * @param nodes_so_far
	 * @return set of TrieNodes that are completions of prefix pre
	 */
	private Set<TrieNode> getEndWords(TrieNode pre, Set<TrieNode> nodes_so_far){
		if(pre.isWord())
			nodes_so_far.add(pre);

		if(pre.isLeaf())
			return nodes_so_far;
		
		//recursive call on each child
		for(int i=0;i<65;i++){
			TrieNode child = pre.getChildAtIndex(i);
			if(child!=null)
				nodes_so_far = getEndWords(child, nodes_so_far);
		}

		return nodes_so_far;
	}

}






