package Autocomplete;

public class ComparableNode implements Comparable<ComparableNode> {
	public final String _word;
	private int _valueU;
	private int _valueB;
	

	/*
		>Purpose: 
			constructor creates a new node that stores a word, its unigram and 
			bigram frequencies.
		>Input: word - word to store.
				valueU - word's unigram frequency.
				valueB - word's bigram frequency.
		>Output: -- 
		>Throws: -- 
	*/
	public ComparableNode(String word, int valueU, int valueB){
		_word = word;
		_valueU = valueU;
		_valueB = valueB;
	}

	/*
		>Purpose: 
			compares this to another ComparableNode and return the results
			in the form of positive and negative integer.
		>Input: c - node that will be compared to this one.
		>Output: Integer - results of the comparison.
		>Throws: -- 
	*/
	public int compareTo(ComparableNode c) {
		
		if (c.getValueB() < _valueB) return -1;
		if (c.getValueB() > _valueB) return 1;
		if (c.getValueU() < _valueU) return -1;
		if (c.getValueU() > _valueU) return 1;
		return _word.compareTo(c._word);
	}
	
	//setter and getters.
	public int getValueU(){
		return _valueU;
	}

	public void setValueU(int i){
		_valueU = i;
	}
	public int getValueB(){
		return _valueB;
	}

	public void setValueB(int i){
		_valueB = i;
	}
}
