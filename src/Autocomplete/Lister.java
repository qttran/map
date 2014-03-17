package Autocomplete;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lister {	
	
	/*
		>Purpose: 
			a static method that takes a file location and parses the words contained
			in that file into a ArrayList<String>.
		>Inputs: fileLocation - location of the file that is being parsed.			
		>Output: ArrayList<String> - A list of all the words in the input file.
		>Throws: -- 
	*/
	public  static ArrayList<String> listAllWords(String fileLocation){
		BufferedReader bufferedReader = null;
		String string;
		ArrayList<String> stringList;
		
		stringList = new ArrayList<String>();
		try {
			bufferedReader = new BufferedReader(new FileReader(fileLocation));
		} catch (FileNotFoundException e) {	
			return null;
		}
	    try {
	    	string = bufferedReader.readLine();
			while(string != null) {
				string = string.toLowerCase();
				//string = string.replaceAll("[^a-zA-Z ]", " ");
				string = string.replaceAll(" +", " ");
				String[] words = string.split("	")[1].split(" ");
				for (String s : words) {
					stringList.add(s);
				}
		        string = bufferedReader.readLine();
		    }
		    bufferedReader.close();
	    }
	    catch(IOException e) {
	    	return null;
	    }
	    return stringList;
	}
}
