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
		List<String> completions = _ac.getSuggestions(input);
		return completions; 
	}
}
