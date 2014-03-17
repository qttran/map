package Autocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AutocompleteEngine {
	
	public static final int SUGGS = 5; // how many suggestions to give
	
	private Trie _trie;
	
	public AutocompleteEngine() {
		_trie = new Trie();
	}
	
	
	/*
	 * word list generation:
	 * 
	 * Read "Index" file line by line to add each "Way name" to a SET
	 *  -> no need to add duplicates since we don't care about occurrences
	 *  
	 *  note: searching for completions should not be case sensitive, i.e.
	 *  "joh"  ==>  "John Street"
	 *  
	 *  
	 */

	public void addAllWords(Set<String> wordSet) {

		for(String s : wordSet) {

			// no modification -- keep spaces, characters, capitalization

			if(s.equals("")) { //no letters in this string
				continue;
			}

			_trie.addWord(s);
		}
	}


	public List<String> getCompletions(String prefix) {
		//Preconditions.checkArgument(prefix.matches("[a-z]{1,}")); //check valid
		
		List<String> completions = getSortedWords(_trie.getCompletions(prefix));
		if(completions.size() > SUGGS) { //trim list size
			completions = completions.subList(0, SUGGS);
		}
		return completions; 
	}
	
	
	public static List<String> getSortedWords(Set<String> ws) {
		List<String> sorted = new ArrayList<>();
		for(String s : ws) {
			sorted.add(s);
		}
		Collections.sort(sorted, new WordSortComparator());
		return sorted;
	}

	
	
	public Trie getTrie(){
		return _trie;
	}

}
