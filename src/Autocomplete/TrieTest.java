package Autocomplete;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class TrieTest {

	@Test
	public void test() { //testing that it works with other characters
		Trie t = new Trie();
		t.addWord("32 John St");
		t.addWord("#$ > Road");
		t.addWord("2 MGH !! 90");
		t.addWord("32 Johnson");
		t.addWord("#$ Meeting");
		t.addWord("Hello");
		t.addWord("Hello");
		
		assertTrue(t.getPrefixNode("Hel") != null);
		assertTrue(t.getPrefixNode("#$") != null);
		assertTrue(t.getPrefixNode("32 ") != null);
		assertTrue(t.getPrefixNode("32 Johns") != null);
		assertTrue(t.getPrefixNode("2 MGH !") != null);
		
		Set<String> c = t.getCompletions("32 j");
		for(String s : c) {
			//System.out.println(s);
		}
	}

}
