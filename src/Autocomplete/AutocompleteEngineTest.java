package Autocomplete;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AutocompleteEngineTest {
	
	@Test
	public void aeTest(){
		AutocompleteEngine a = new AutocompleteEngine();
		System.out.println(a.getCompletions("Che"));
		System.out.println(a.getCompletions("H"));
		System.out.println(a.getCompletions("C"));
		System.out.println(a.getCompletions("Ced"));	
	}
	

}
