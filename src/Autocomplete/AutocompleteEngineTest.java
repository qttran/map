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
		//a.addAllWords("/home/bsenturk/course/cs032/map/src/Autocomplete/Test1");
		System.out.println(a.getCompletions("Che"));
		System.out.println(a.getCompletions("H"));
		System.out.println(a.getCompletions("C"));
		System.out.println(a.getCompletions("Ced"));	
	}
	

}
