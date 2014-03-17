package cs32.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapsCLI {
	

	private MapsEngine _engine;

	public MapsCLI(MapsEngine en) throws IOException {
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

	private String getOutput(String input) throws IOException {
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
