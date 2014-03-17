package Autocomplete;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteEngine {
	
	public static final int SUGGS = 5; // how many suggestions to give
	
	private Autocorrect _ac;
	
	public AutocompleteEngine() {
		_ac = new Autocorrect();
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

	public void addAllWords(String fileLocation) {
		_ac.addCorpus(fileLocation);
	}

	public List<String> getCompletions(String input) {
		//modify the input to make all the letters lowercase, remove unwanted spaces and
		//punctuations.
		input = input.toLowerCase();
		//input = input.replaceAll("[^a-zA-Z ]", " ");
		input = input.replaceAll(" +", " ");
		input = input.trim();
		//split input string into seperate words.
		String[] words = input.split(" ");
		String word = words[words.length-1];
		input = input.substring(0, input.lastIndexOf(" ")+1);
		String prevWord = null;
		if (words.length > 1) prevWord = words[words.length-2];
		
		List<String> completions = _ac.getSuggestions(word, prevWord);
		return completions; 
	}
}
