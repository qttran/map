package Autocomplete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Autocorrect {

	private Trie _trie;
	private Ranker _ranker;
	private int _ED = 2;

	

	/*
		>Purpose:
			autocorrect constructor creates a new trie to hold the strings in corpus and 
			a new ranker to rank the set of suggestions returned by the trie.			
		>Input: --
		>Output: -- 
		>Throws: -- 
	*/
	public Autocorrect(){
		_trie = new Trie();
		_ranker = new Ranker();
	}
	
	/*
		>Purpose:
			addCorpus uses Lister to get all the words in a corpus and adds those words 
			to the trie and the dictionaries in ranker.		
		>Input: fileLocation - location of the corpus.
		>Output: Boolean - whether the operation was successful or not. 
		>Throws: -- 
	*/
	public Boolean addCorpus(Set<String> words){				
		_trie.insertToTrie(words);
		_ranker.insertToMaps(words);
		return true;
	}
	
	/*
		>Purpose: 
			getSuggestions uses the trie's methods to creat a set of possible suggestions
			and ranks those suggestions by using Ranker's methods. 
		>Input: word - the word that is currently being written
				previousWord - the word that precedes word (if one exists)
		>Output: ArrayList<String> a sorted list of possible suggestions.
		>Throws: -- 
	*/

	public ArrayList<String> getSuggestions(String input) {
		//modify the input to make all the letters lowercase, remove unwanted spaces and
		//punctuations.
		input = input.toLowerCase();
		//input = input.replaceAll("[^a-zA-Z ]", " ");
		input = input.replaceAll(" +", " ");
		input = input.trim();
		
		//store suggestions in a HashSet<String> to prevent duplicates;
		HashSet<String> suggestions = new HashSet<String>(32);
		suggestions.addAll(_trie.getPrefixMatch(input));
		suggestions.addAll(_trie.getLED(input, _ED));
		suggestions.addAll(_trie.getWordSplit(input)); 
		Node n = _trie.searchTrie(input);
		//check if the input word is among the ones that are stored in trie and put it
		//at the beginning of the sorted list if so.
		if (n != null && n.isWord()) suggestions.add(n._key);
		ArrayList<String> rankedArray = _ranker.standardRank(suggestions, input, null);
		ArrayList<String> toReturn = new ArrayList<String>();
		for (String s : rankedArray){
			toReturn.add(s);
		}
		return toReturn;
	}
}
