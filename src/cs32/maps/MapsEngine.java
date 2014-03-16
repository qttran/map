package cs32.maps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import KDTree.Coordinates;
import KDTree.KDTree;

public class MapsEngine {
	String nodeFile;
	KDTree k;
	
	public MapsEngine() throws IOException {
		this.k = buildKDTree(nodeFile);
	}
	
	protected String getOutputFromIntersection(List<String> streetnames) throws IOException {
		// find nearest start node (kdtree)
		// find nearest end node (kdtree)
		
		// get shortest path between them (PathFinder)
		return null;
	}

	protected String getOutputFromLatLongs(LatLong s, LatLong e) throws IOException {
		// find nearest start node (kdtree)
		String nearestStartNode = k.searchNumber(1, new Coordinates(s.lat, s.lon)).get(0);
		// find nearest end node (kdtree)
		String nearestEndNode = k.searchNumber(1, new Coordinates(e.lat, e.lon)).get(0);
		// get shortest path between them (PathFinder)
		return null;
	}
	
	private KDTree buildKDTree(String nodeFile) throws IOException {
		//Create a KDTree from the file
		Hashtable<String,Coordinates> hashTable = new Hashtable<String,Coordinates>();

		KDTree k = new KDTree();
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] list = line.split(",");

			double x = Double.parseDouble(list[1]);
			double y  = Double.parseDouble(list[2]);

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
		return k;
	}
}
