package cs32.maps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import KDTree.KDTree;
import KDTree.Coordinates;

public class MapsCLI {

	private MapsEngine _engine;
	
	public MapsCLI(MapsEngine en) {
		_engine = en;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		
		System.out.println("Ready");
		while(true) {
			try {
				input = in.readLine();
				if(input == null){ //exit cleanly on EOF
					System.exit(1);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			/* process raw input */
			String out = this.getOutput(input);
			if(out!=null)
				System.out.println(out);
			
		}
		
	}

	private String getOutput(String input) {
		//find out which format it's in
		String[] spaces = input.split(" ");
		boolean latlongFormat = true;
		for(String s : spaces){
			if(!isDouble(s))
				latlongFormat=false;
		}
		
		//latitude/longitude format
		if(latlongFormat) {
			double lat1 = Double.parseDouble(spaces[0]);
			double lon1 = Double.parseDouble(spaces[1]);
			double lat2 = Double.parseDouble(spaces[2]);
			double lon2 = Double.parseDouble(spaces[3]);
			
			if(spaces.length!=4){
				System.out.println("ERROR: give two latitude/longitude pairs");
				return null;
			}
			
			LatLong start = new LatLong(lat1, lon1);
			LatLong end = new LatLong(lat2, lon2);
			
			return _engine.getOutputFromLatLongs(start, end); // engine
		}
		//street name format
		else {
			String[] quotes = input.split("\"");
			List<String> streets = new ArrayList<>();
			for(String s : quotes){
				if(!s.trim().isEmpty())
					streets.add(s);
			}
			if(streets.size()!=4){
				System.out.println("ERROR: give four streets");
				return null;
			}
			
			return _engine.getOutputFromIntersection(streets); // engine
		}
	}
	
	//Create a KDTree from the file
		public void createKDTree(String string) throws IOException {
			Hashtable<String,Coordinates> hashTable = new Hashtable<String,Coordinates>();
			List<String> result = new ArrayList<String>();
			
			KDTree k = new KDTree();
			BufferedReader br = new BufferedReader(new FileReader(string));
			String line = br.readLine();
			line = br.readLine();

			while (line != null) {
				String[] list = line.split(",");

				double x = Double.parseDouble(list[2]);
				double y  = Double.parseDouble(list[3]);

				//put in KDTree
				Coordinates coordinate = new Coordinates(x,y);
				String id = list[0];
				k.insert(id, coordinate);

				//put in HashTable
				String name = list[1];
				hashTable.put(name, coordinate);

				line = br.readLine();
			}
			br.close();
		}
	
	private boolean isDouble(String s){
		boolean b = true;
		try {
			Double.parseDouble(s);
		}
		catch(NumberFormatException e) {
			b = false;
		}
		return b;
	}
}
