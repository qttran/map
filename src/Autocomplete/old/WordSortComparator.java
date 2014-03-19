package Autocomplete.old;

import java.util.Comparator;
/**
 * 
 * @author mcashton
 *
 */
public class WordSortComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		int diff = s1.length() - s2.length();
		if(diff==0) { //if same length, sort alphabetically
			diff = s1.compareToIgnoreCase(s2);
		}
		return diff;
	}

}
