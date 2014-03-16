package Autocomplete;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AutocompleteEngineTest {

	@Test
	public void sortedWordTest() { // testing word sorting using WordSortComparator
		Set<String> ws = new HashSet<>();
		ws.add("bca");
		ws.add("abc");
		ws.add("aa");
		ws.add("dcba");
		ws.add("mz");
		ws.add("ab");
		List<String> sorted = AutocompleteEngine.getSortedWords(ws);
		assertTrue(sorted.get(0) == "aa");
		assertTrue(sorted.get(1) == "ab");
		assertTrue(sorted.get(2) == "mz");
		assertTrue(sorted.get(3) == "abc");
		assertTrue(sorted.get(4) == "bca");
		assertTrue(sorted.get(5) == "dcba");
	}
	
	@Test
	public void aeTest(){
		Set<String> ways = new HashSet<>();
		ways.add("111 Highland Rd");
		ways.add("10th Street");
		ways.add("10th Street");
		ways.add("11th Street");
		
		AutocompleteEngine a = new AutocompleteEngine();
		a.addAllWords(ways);
		System.out.println(a.getCompletions("1"));
		System.out.println(a.getCompletions("11"));
		System.out.println(a.getCompletions("10"));
		System.out.println(a.getCompletions("zz"));	
	}
	

}
