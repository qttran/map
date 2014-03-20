package cs32.maps.FileReader;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

import KDTree.KDTree;
import KDTree.Node;

import com.google.common.base.Preconditions;

import cs32.maps.LocationNode;
import cs32.maps.Way;

public class MapsIO {

	protected final String waysFile;
	protected final String nodesFile;
	protected final String indexFile;
	public int ways_idCol, ways_startCol, ways_endCol, ways_nameCol;  //public for testing right now
	public int nodes_idCol, nodes_latCol, nodes_lonCol, nodes_waysCol;
	public int index_nameCol, index_nodesCol;
	
	//1234.0000 1234.5678
	//private HashMap<String, Long> wayLatPointers;
	public HashMap<String, List<Long>> nodeLatLongPointers;
	public HashMap<String, Long> nodeLatPointers;
	
	public int maxLat;
	public int minLat;
	public int maxLon;
	public int minLon;
	

	public MapsIO(String waysFile, String nodesFile, String indexFile) {
		
		//wayLatPointers = new HashMap<>(); //getAllWays fills this up
		nodeLatLongPointers = new HashMap<>();
		nodeLatPointers = new HashMap<String, Long>();
		
		this.waysFile = waysFile;
		this.nodesFile = nodesFile;
		this.indexFile = indexFile;

		index_nameCol = 0;
		index_nodesCol = 1;

		try {
			ways_idCol = getColumn(waysFile, "id");
			ways_startCol = getColumn(waysFile, "start");
			ways_endCol = getColumn(waysFile, "end");
			ways_nameCol = getColumn(waysFile, "name");
			nodes_idCol = getColumn(nodesFile, "id");
			nodes_latCol = getColumn(nodesFile, "latitude");
			nodes_lonCol = getColumn(nodesFile, "longitude");
			nodes_waysCol = getColumn(nodesFile, "ways");
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
	

	/** Read one line from a file, starting at the file pointer
	 * 
	 * @param f
	 * @return Read one line from a file, starting at the file pointer
	 * @throws IOException
	 */
	protected String[] readOneLine(RandomAccessFile f) throws IOException {
		// seek start of line
		boolean start = false;
		long pointer = f.getFilePointer();
		int byteToRead = 256;
		while (!start) {
			byte[] seg = new byte[byteToRead];
			for (int i = 0; i < seg.length; i++) {
				seg[i] = 0;
			}
			long pointer2 = f.getFilePointer() - byteToRead;
			if (pointer2 < 0) {
				pointer2 = 0;
				start = true;
			}
			f.seek(pointer2);
			f.read(seg);
			for (int i = seg.length - 1; i >= 0; i--) {
				if (seg[i] == 10) {
					if (pointer >= pointer2 + i + 1) {
						pointer2 += i + 1;
						start = true;
						break;
					}
				}
			}
			f.seek(pointer2);
		}

		pointer = f.getFilePointer();

		byte[] result = new byte[0];
		
		int length = 0;
		boolean end = false;
		while (!end) {
			byte[] seg = new byte[byteToRead];
			f.read(seg);

			for (int i = 0; i < seg.length; i++) {
				if (seg[i] == 10) {
					int l = result.length;
					result = Arrays.copyOf(result, result.length + i);
					System.arraycopy(seg, 0, result, l, i);
					length += i;
					end = true;
					break;
				}
				if (i == seg.length - 1) {
					int l = result.length;
					result = Arrays.copyOf(result, result.length + byteToRead);
					System.arraycopy(seg, 0, result, l, byteToRead);
					length += byteToRead;
				}
			}
		}
		String line = new String(result);
		f.seek(pointer+length);

		return line.split("\t", -1);

	}



	public String[] binarySearch(RandomAccessFile file, int whichCol, String toFind) throws IOException {
		file.seek(0);
		nextNewLine(file);
		long start = file.getFilePointer();
		long end = (file.length() - 1);
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file);
			int difference = toFind.compareToIgnoreCase(currentLine[whichCol]);
			if (difference == 0) {
				//file.close();
				return currentLine;
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 
		file.close();
		return null;
	}
	
	/**
	 * binary search that takes in bounds
	 */
	public String[] binarySearch(RandomAccessFile file, int whichCol, String toFind, long top, long bottom) throws IOException {
		file.seek(top);
		long start = top;
		long end = bottom;
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file);
			int difference = toFind.compareToIgnoreCase(currentLine[whichCol]);
			if (difference == 0) {
				//file.close();
				return currentLine;
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 
		file.close();
		return null;
	}

	/**
	 * Given a wayID, search the *ways file* for correct line
	 * and return a Way object with all relevant info
	 */
	public Way getWay(String wayID) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(waysFile, "r");
		// do binary search
		String[] line = binarySearch(raf, ways_idCol, wayID);
		raf.close();
		if(line==null) {
			System.out.printf("No such way ID: %s\n", wayID);
			raf.close();
			return null;
		}

		//convert 'line' to Way object
		String id = line[ways_idCol];
		String startID = line[ways_startCol];
		String endID = line[ways_endCol];
		String name = line[ways_nameCol];

		Preconditions.checkState(id.equals(wayID)); // id should be the same one that was requested


		raf.close();
		return new Way(id, startID, endID, name);
	}


	/* given a street name,
	   read the INDEX file. Find ALL 
	   the streets with this name and create 
	   a set of all the nodes
	   connected to streets with 
	   this name.

	   This is a helper function for getIntersection(..)
	*/
	private Set<String> getNodeIDsFromStreet(String streetName) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(indexFile, "r");
		streetName = streetName.toLowerCase();
		
		String[] line = binarySearch(raf, index_nameCol, streetName);
		if(line==null){
			System.out.printf("No such street: %s\n", streetName);
			return null;
		}
		
		// Create set, add what binary search gave me
		Set<String> nodeIDset = new HashSet<>();
		String[] ids = line[index_nodesCol].split(",");
		Collections.addAll(nodeIDset, ids); //add all ids from this street entry)


		long originalPtr = raf.getFilePointer();


		//look ABOVE
		previousNewLine(raf);
		previousNewLine(raf);
		String nameHere = getWordAt(raf);
		while(nameHere.compareToIgnoreCase(streetName)==0) {
			Collections.addAll(nodeIDset, readOneLine(raf)[index_nodesCol].split(","));
			previousNewLine(raf);
			previousNewLine(raf);
			nameHere = getWordAt(raf); //its the index so we know the street name is first
		}
		// seek original
		raf.seek(originalPtr);
		
		// get everything below
		nextNewLine(raf);
		nameHere = getWordAt(raf);
		while(nameHere.compareToIgnoreCase(streetName)==0) {
			Collections.addAll(nodeIDset, readOneLine(raf)[index_nodesCol].split(","));
			nextNewLine(raf); 
			nameHere = getWordAt(raf);
		}
		return nodeIDset;
	}

	/**
	 * Given a nodeID, search the *nodes file* for correct line
	 * and return a list of LocationNode objects that have ids
	 * with the same 8 digits
	 */
	public List<LocationNode> getNodePage(String nodeID) throws IOException {
		// do binary search
		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		String[] line = binarySearch(raf, nodes_idCol, nodeID);

		if(line==null) {
			System.out.printf("No such node ID: %s\n", nodeID);
			return null;
		}

		List<LocationNode> pageList = new ArrayList<>();
		pageList.add(createLocationNode(line));
		
		//! raf is currently at the *END* of the line I just added
		long originalPtr = raf.getFilePointer();
		raf.seek(0);
		nextNewLine(raf);
		long topOfFile = raf.getFilePointer();
		raf.seek(originalPtr);
		
		// get everything above
		 		
		String[] nodeLine;
		String nodeIDhere;

		previousNewLine(raf);
		
		//if anything above, get it
		if(raf.getFilePointer()!=topOfFile) {
			
			previousNewLine(raf); 
			nodeLine = readOneLine(raf);
			nodeIDhere = nodeLine[nodes_idCol];
		
			while(areOnSamePage(nodeID, nodeIDhere)) {
				
				pageList.add(createLocationNode(nodeLine));
				previousNewLine(raf);

				if(raf.getFilePointer()==topOfFile) //if at top of file
					break;
				
				previousNewLine(raf);
				nodeLine = readOneLine(raf);
				nodeIDhere = nodeLine[nodes_idCol];
				
			}
		}
		// seek original
		raf.seek(originalPtr);
		
		// if anything below, get it
		nextNewLine(raf);
		if(raf.getFilePointer() < raf.length()) {
			nodeLine = readOneLine(raf);
			nodeIDhere = nodeLine[nodes_idCol];
			
			while(areOnSamePage(nodeID, nodeIDhere) && raf.getFilePointer()<raf.length()) {
				pageList.add(createLocationNode(nodeLine));
				nextNewLine(raf); 
				
				nodeLine = readOneLine(raf);
				nodeIDhere = nodeLine[nodes_idCol];
			}
		}
		return pageList;
	}


	private LocationNode createLocationNode(String[] line) {
		//convert 'line' to LocationNode object
		String id = line[nodes_idCol];
		String ways = line[nodes_waysCol];
		double lat = Double.parseDouble(line[nodes_latCol]);
		double lon = Double.parseDouble(line[nodes_lonCol]);
		Point2D.Double latlong = new Point2D.Double(lat, lon);
		//create ways list
		List<String> wayList = new ArrayList<>();
		if(ways.length()>1) {
			for(String s : ways.split(",")) {
				wayList.add(s);
			}
		}
		return new LocationNode(id, wayList, latlong);
	}


	/** Given 2 street names, return the ID of the node 
	 * which is the cross section of the 2 streets
	 * @param street1
	 * @param street2
	 * @return String
	 * @throws IOException 
	 */

	public String getIntersection(String street1, String street2) throws IOException{
		Set<String> idSetOne = this.getNodeIDsFromStreet(street1);
		Set<String> idSetTwo = this.getNodeIDsFromStreet(street2);
		for(Iterator<String> iter = idSetOne.iterator(); iter.hasNext(); ) {
			String id = iter.next();
			if(idSetTwo.contains(id)) {
				return id;
			}
		}
		return "";
	}


	public Set<String> getAllStreetNames() throws IOException{
	
		Set<String> streets = new HashSet<>();
	
		RandomAccessFile raf = new RandomAccessFile(indexFile, "r");
		nextNewLine(raf); //skip header with titles
	
		long fileSize = raf.length();
		String[] line;
		String name;
	
		while(raf.getFilePointer() < fileSize) {
			// read line and add street name to set
			line = readOneLine(raf);
			name = line[index_nameCol];
			streets.add(name.toLowerCase());
			nextNewLine(raf);
		}
		raf.close();
	
		return streets;
	}
	
	
	


	/**
	 * GET LOCATION NODE
	 * 
	 * 1. find bounds
	 * 2. do binary search within those bounds
	 * 3. create LocationNode object
	 */
	public LocationNode getLocationNode(String nodeID) throws IOException {
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
	
		// get the bounds of where to look

		String chunkID = getKeyValueFromID(nodeID);
		
		long filePointerTop = 0; 
		long filePointerBottom = file.length();
		// set file pointers
		if(nodeLatLongPointers.containsKey(chunkID)) {
			filePointerTop = nodeLatLongPointers.get(chunkID).get(0);
			filePointerBottom = nodeLatLongPointers.get(chunkID).get(1);
		}
		else {
			// if it's not in the hashmap then its not in the file
			System.out.printf("ERROR: %s is not a valid node ID.\n", nodeID);
		}

		String foundLine[] = null;
		file.seek(filePointerTop); // points to beginning of first line of chunk
		foundLine = binarySearch(file, nodes_idCol, nodeID, filePointerTop, filePointerBottom);
		file.close();
		
		if(foundLine == null) {
			System.out.printf("ERROR: no such node %s\n", nodeID);
			return null;
		}
		return createLocationNode(foundLine);
	}

	
	
	
	
	
	/**
	 * get all location nodes
	 * input:   "4023.4323" "4023.8909"
	 */
	public Map<String, LocationNode> getAllLocationNodesWithin(String topEight, String bottomEight) throws IOException {

		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		
		long[] bounds = getByteBounds(topEight, bottomEight);
		long top = bounds[0];
		long bottom = bounds[1];


		raf.seek(top);
		long bytesToRead = bottom-top;
		byte[] chunk = new byte[(int) bytesToRead];
		raf.read(chunk);
		
		
		
		Map<String, LocationNode> nodeMap = new HashMap<>();
		String chunkAsString = new String(chunk);
		String[] lines = chunkAsString.split("\\n");
		for(String oneLine : lines){
			
			String[] currLine = oneLine.split("\\t",-1);
			LocationNode node = createLocationNode(currLine);
			nodeMap.put(node.id, node);
			
			nextNewLine(raf);
		}
		raf.close();
		
		

		
		return nodeMap;
	}
	

	
	/**
	 * helper for getLocationNode to find which bounds to look between
	 * (by looking in the hashmap)
	 */
	public long[] getByteBounds(String top8, String bottom8) throws IOException {

		// find max length
		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		raf.close();
		
		String first4 = top8.substring(0,4);
		
		// given that they will start with same four (?)
		Preconditions.checkState(first4.equals(bottom8.substring(0,4)));
		
		long topFilePointer; 
		long bottomFilePointer;
		
		if(nodeLatLongPointers.containsKey(top8)) {
			topFilePointer = nodeLatLongPointers.get(top8).get(0); //upper bound of thie 8seq
		}
		else {
			// if it didnt contain "1234.4223"
			// look in "1234"
			// binary search, -- the byte bounds of t.ex.  "1234.4222"
			
			//binary search in this 4-chunk of the latitude I want, return its FP (by finding it in HM)
			List<Long> ptrs = getFilePtrsFrom4(first4, top8.substring(5));
			
			//top points to top of first4
			topFilePointer = ptrs.get(0);
			
		}
		
		
		if(nodeLatLongPointers.containsKey(bottom8)) {
			bottomFilePointer = nodeLatLongPointers.get(bottom8).get(1); //lower bound of this 8seq
		}
		else {
			// if it didn't contain "1234.4278"
			// look in "1235"
			// binary search, -- the byt bounds of t.ex. 
			
			//bottom points to top of (first4+1)
			int val = Integer.parseInt(first4);
			String incremented = Integer.toString(val+1);
			
			List<Long> ptr = getFilePtrsFrom4(incremented, bottom8.substring(5));
			
			bottomFilePointer = ptr.get(0);
		}
		
		
	
		//System.out.printf("%s mapped to %s\n%s mapped to %s\n\n", topChunk, topFilePointer, bottomChunk, bottomFilePointer);
		return new long[]{topFilePointer, bottomFilePointer};
	}
	

	public List<Long> getFilePtrsFrom4(String firstFour, String secondFour) throws IOException {
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
		String toFind = "/n/"+firstFour+"."+secondFour;
		String incremented = Integer.toString(Integer.parseInt(firstFour)+1);
		long start = nodeLatPointers.get(firstFour);
		long end = nodeLatPointers.get(incremented);
		long mid = (start + end)/2;
		
		file.seek(start);
		

		String[] currentLine = null;
		while (end > start) {
			file.seek(mid);
			currentLine = readOneLine(file);
			if(areOnSamePage(toFind, currentLine[nodes_idCol])) {
				break; //shouldnt happen (it would already be in the 8pt hashmap)
			}
			int difference = toFind.compareToIgnoreCase(currentLine[nodes_idCol]);
			if (difference == 0) {
				break; //shouldnt happen
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 
		file.close();
		// currentline -- is it above or below?
		String chunk8 = getKeyValueFromID(currentLine[nodes_idCol]);
		return nodeLatLongPointers.get(chunk8);
	}
	
	
	
	/************* LIIITTLE HELPERS *************/
	public static long getLongValue(String mapKey) {
		Preconditions.checkState(mapKey.length() == 9);
		mapKey = mapKey.substring(0, 4) + mapKey.substring(5, 9);
		return Long.parseLong(mapKey);
	}
	
	public static String getKeyValue(long num) {
		String s = Long.toString(num);
		Preconditions.checkState(s.length() == 8);
		s = s.substring(0, 4) + "." + s.substring(4, 8);
		return s;
	}
	
	//  "/w/1122.23453454"  ==>   "1122.2345"
	public static String getKeyValueFromID(String id) {
		return id.substring(3,7) + "." + id.substring(8, 12);
	}
	
	//helper for getNodePage
	private boolean areOnSamePage(String origID, String queryID) {
		return getKeyValueFromID(origID).equals(getKeyValueFromID(queryID));
	}
	
	/********* manipulating the RandomAccessFile pointer, and finding columns *****/
	
	
	
	/** for finding column info @mcashton */
	private int getColumn(String filePath, String colName) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		raf.seek(0);
		String title = getWordAt(raf);
		int col = 0;
		int p;
		while(!title.equals(colName) && (p=raf.read())!='\n' && p!=-1) {
			raf.seek(raf.getFilePointer()-1);

			while(raf.read() != 9); //read until next tab

			title = getWordAt(raf);
			col++;
		}
		raf.close();
		if(!title.equals(colName)){
			return -1;
		}
		return col;
	}

	private String getWordAt(RandomAccessFile raf) throws IOException {
		int c;
		int bytesRead = 0;
		long originalLoc = raf.getFilePointer();

		//figure out how many bytes to read
		while((c=raf.read())!='\n' && c!=9 && c!=-1){
			bytesRead++;
		}

		//go back to starting point
		raf.seek(originalLoc);

		//read that number of bytes
		byte[] b = new byte[bytesRead];
		raf.readFully(b);

		//seek back to original spot
		raf.seek(originalLoc);

		return new String(b);
	}

	//helper method -- extends raf to next newline
	public static void nextNewLine(RandomAccessFile raf) throws IOException{
		int c;
		while((c = raf.read()) != '\n'  && c!=-1);
	}


	//helper method -- move raf to previous newline
	public static void previousNewLine(RandomAccessFile raf) throws IOException {		
		long currPtr = raf.getFilePointer();
		raf.seek(currPtr-2);
		while(raf.getFilePointer()>1) {
			if(raf.read()=='\n')
				break;
			raf.seek(raf.getFilePointer()-2);
		}		
	}

	
	
	
	
	/******* method called for building KDTree ******/
	
	public List<Node> getKDNodes() throws IOException {
		ArrayList<Node> kdNodes = new ArrayList<Node>();
		
		int minLat = 99999;
		int maxLat = 0;
		int minLon = 99999;
		int maxLon = 0;


		//Create list of all latlongs and IDs from file

		BufferedReader br = new BufferedReader(new FileReader(nodesFile));
		long bytes = 0;

		// read the first - unnecessary line
		String line = br.readLine();

		bytes += line.getBytes().length + 1;

		String latLong = "9999.9999";
		List<Long> dummy = new LinkedList<>();
		dummy.add((long) 0);
		dummy.add((long) 0);

		String lat = "0000";
		nodeLatLongPointers.put(latLong, dummy);

		while (line != null) {
			line = br.readLine();
			if(line==null)
				break;


			String[] list = line.split("\t");
			double x = Double.parseDouble(list[1]);
			double y = Double.parseDouble(list[2]);
			//put in KDTree
			String id = list[0];
			String currLat = list[0].substring(3,7);
			int currLatNum = Integer.parseInt(currLat);
			int currLonNum = Integer.parseInt(list[0].substring(8,12));
			Node coordinate = new Node(id, x, y);
			kdNodes.add(coordinate);

			String currLatLong = list[0].substring(3,12) ;

			//set max min lat lons
			minLat = Math.min(minLat, currLatNum);
			maxLat = Math.max(maxLat, currLatNum);
			minLon = Math.min(minLon, currLonNum);
			maxLon = Math.max(maxLon, currLonNum);

			//keep track of latLong pointers:
			if (!latLong.equals(currLatLong)) {
				//update the previous latlong in the hashMap
				Long previous = nodeLatLongPointers.get(latLong).get(0);
				List<Long> toPut = new LinkedList<>();
				toPut.add(previous);
				toPut.add(bytes);
				nodeLatLongPointers.put(latLong, toPut);
				//update the current latlong in the hashMap
				List<Long> toPut2 = new LinkedList<>();
				toPut2.add(bytes);
				toPut2.add(bytes);
				nodeLatLongPointers.put(currLatLong, toPut2);

				//update latLong
				latLong = currLatLong;
			}

			//keep track of lat pointers: 
			if (!lat.equals(currLat)) {
				if(!nodeLatPointers.containsKey(currLatLong)) {
					nodeLatPointers.put(currLat, bytes);
				}
				//update Lat
				lat = currLat;
			}

			bytes += line.getBytes().length +1;
		}

		// put the final coordinate
		if(!nodeLatLongPointers.containsKey(latLong)) {
			List<Long> toPut = new LinkedList<>();
			Long previous = nodeLatLongPointers.get(latLong).get(0);
			toPut.add(previous);
			toPut.add(bytes);
			nodeLatLongPointers.put(latLong,toPut);
		}

		Integer finalLat = Integer.parseInt(lat) + 1;
		lat = finalLat.toString();
		if(!nodeLatPointers.containsKey(lat)) {
			nodeLatPointers.put(lat,bytes);
		}
		br.close();


		return kdNodes;
	}
	
	
	
	

	
}

