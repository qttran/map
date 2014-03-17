package Autocomplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class Ranker {

	private Map<String, Integer> _unigramMap;
	private Map<String, Integer> _bigramMap;
	

	/*
		>Purpose:
			constructor creates a new ranker with two empty HashMaps to hold unigram and bigram 
			frequencies associated with the words in corpus.
		>Input: --
		>Output: --
		>Throws: -- 
	*/
	public Ranker() {
		_bigramMap = new HashMap<String, Integer>();
		_unigramMap = new HashMap<String, Integer>();
	}
	
	/*
		>Purpose: 
			takes a List<String> and adds all the words to the HashMaps.
		>Input: words - words that will be inserted to the maps.
		>Output: --
		>Throws: -- 
	*/
	public void insertToMaps(ArrayList<String> words) {
		for (int i = 0 ; i < words.size() - 1 ; i++) {
			String word = words.get(i)+words.get(i+1);
			Integer c = _bigramMap.get(word); 
			if (c != null) _bigramMap.put(word, c+1);
			else _bigramMap.put(word, 1);
		}
		for (String word : words) {
			Integer c = _unigramMap.get(word); 
			if (c != null) _unigramMap.put(word, c+1);
			else _unigramMap.put(word, 1);
		}
	}
	
	/*
		>Purpose: 
			this method uses the standard way to rank all the words in a set of possible suggestions.
		>Input: set - set of all the possible suggestions.
		>Output: ArrayList<String> - sorted list of suggestions.
		>Throws: -- 
	*/
	public ArrayList<String> standardRank(HashSet<String> set, String word, String previousWord){
		ArrayList<String> stringsToReturn = new ArrayList<String>();
		ArrayList<ComparableNode> suggestions = new ArrayList<ComparableNode>();
		if (previousWord != null){
			for (String s : set){
				if (s.contains(" ")) {
					int i;
					if (_bigramMap.containsKey(previousWord+s.split(" ")[0])) i = _bigramMap.get(previousWord+s.split(" ")[0]);
					else i = 0;
					int j;
					if (_unigramMap.containsKey(s.split(" ")[0])) j = _unigramMap.get(s.split(" ")[0]);
					else j = 0;
					suggestions.add(new ComparableNode(s, j, i));
				}
				else{
					int i;
					if (_bigramMap.containsKey(previousWord+s)) i = _bigramMap.get(previousWord+s);
					else i = 0;
					suggestions.add(new ComparableNode(s, _unigramMap.get(s), i));
				}
				
			}
		}
		else {
			for (String s : set){
				if (s.contains(" ")) {
					int i;
					if (_unigramMap.containsKey(s.split(" ")[0])) i = _unigramMap.get(s.split(" ")[0]);
					else i = 0;
					suggestions.add(new ComparableNode(s, i, 0));
				}
				else suggestions.add(new ComparableNode(s, _unigramMap.get(s), 0));
			}
		}
		
		Collections.sort(suggestions);

		List<String> list = new ArrayList<String>();
		for (ComparableNode c : suggestions){
			list.add(c._word);
		}
		
		if (list.contains(word)){
			stringsToReturn.add(word);
			list.remove(word);
			if (list.size() >= AutocompleteEngine.SUGGS-1) list = list.subList(0, AutocompleteEngine.SUGGS-1);
			else list = list.subList(0,list.size());
		}
		
		else{
			if (list.size() >= AutocompleteEngine.SUGGS) list = list.subList(0, AutocompleteEngine.SUGGS);
			else list = list.subList(0,list.size());
		}
		
		stringsToReturn.addAll(list);
		
		return stringsToReturn;
	}
}
