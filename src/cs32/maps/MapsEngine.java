package cs32.maps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import KDTree.Coordinates;
import KDTree.KDTree;
import PathFinding.PathFinder;
import cs32.maps.FileReader.MapsIO;

public class MapsEngine {
	String fpWays;
	String fpNodes;
	String fpIndex;
	public KDTree k;
	MapsIO fileReader;

	public MapsEngine(String fpWays, String fpNodes, String fpIndex) throws IOException {
		this.k = buildKDTree(fpNodes);
		this.fpNodes = fpNodes;
		this.fpIndex = fpIndex;
		this.fpWays = fpWays;
		fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
	}


	/************** for use with CLI ******************/
	
	//CLI version
	public String getOutputFromIntersection(List<String> streetnames) throws IOException {
		// find nearest start node (kdtree)
		String intersection1 = fileReader.getIntersection(streetnames.get(0), streetnames.get(1));
		LocationNode nearestStartNode = fileReader.getLocationNode(intersection1);

		// find nearest end node (kdtree)
		String intersection2 = fileReader.getIntersection(streetnames.get(2), streetnames.get(3));
		LocationNode nearestEndNode = fileReader.getLocationNode(intersection2);

		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPath(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}

	public String getOutputFromLatLongs(LatLong s, LatLong e) throws IOException {
		MapsIO fileReader = new MapsIO(fpWays, fpNodes, fpIndex);
		// find nearest start node (kdtree)
		String nearestStartNodeID = k.searchNumber(1, new Coordinates(s.lat, s.lon)).get(0);
		LocationNode nearestStartNode = fileReader.getLocationNode(nearestStartNodeID);

		// find nearest end node (kdtree)
		String nearestEndNodeID = k.searchNumber(1, new Coordinates(e.lat, e.lon)).get(0);
		LocationNode nearestEndNode = fileReader.getLocationNode(nearestEndNodeID);


		// get shortest path between them (PathFinder)
		PathFinder pF = new PathFinder(fileReader);
		List<String> resultList = pF.getPath(nearestStartNode,nearestEndNode);
		String result = "";
		for (String x: resultList) {
			result += x + "\n";
		}
		return result;
	}

	
	/*************** for use with GUI ***************/
	
	//GUI version
	// node1 - node2    ...   node2 - node3
	// set of StreetNodes that have starting point (2d double), ending point (name not  necessary)
	
	
	//User-Click backend:
	// input: latitude and longitude
	// output: latitude and longitude that is nearest real point
	
	
	//Get Directions backend:
	// input: two pairs of latitude and longitude (that are REAL POINTS)
	// output: set of StreetNodes
	
	
	
	
	
	
	
	

	private KDTree buildKDTree(String nodeFile) throws IOException {
		//Create a KDTree from the file
		Hashtable<String,Coordinates> hashTable = new Hashtable<String,Coordinates>();

		KDTree k = new KDTree();
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] list = line.split("\t");
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
