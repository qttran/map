package cs32.maps.FileReader;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import cs32.maps.LatLong;
import cs32.maps.LocationNode;
import cs32.maps.Way;

public class MapsIO {

	private final String waysFile;
	private final String nodesFile;
	private final String indexFile;
	public int ways_idCol, ways_startCol, ways_endCol, ways_nameCol;  //public for testing right now
	public int nodes_idCol, nodes_latCol, nodes_lonCol, nodes_waysCol;
	public int index_nameCol, index_nodesCol;
	
	//1234.0000 1234.5678
	private HashMap<String, Long> wayLatPointers;
	public HashMap<String, List<Long>> nodeLatLongPointers;
	public HashMap<String, Long> nodeLatPointers;
	
	private int maxLat;
	private int minLat;
	private int maxLon;
	private int minLon;
	
	
	private long nodes_maxLat = Long.MIN_VALUE;
	private long nodes_minLat = Long.MAX_VALUE;
	private long ways_maxLat = Long.MIN_VALUE;

	public MapsIO(String waysFile, String nodesFile, String indexFile) {
		
		wayLatPointers = new HashMap<>(); //getAllWays fills this up
		nodeLatLongPointers = new HashMap<>();
		
		
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
	

	public void setNodeLatLongPtrs(HashMap<String, List<Long>> pointerMap) {
		nodeLatLongPointers = pointerMap;
	}
	public void setMaxMinLatLong(int maxLat, int minLat, int maxLon, int minLon) {
		this.maxLat = maxLat;
		this.minLat = minLat;
		this.maxLon = maxLon;
		this.minLon = minLon;
	}
	
	
	public void setNodeLatPtrs(HashMap<String,Long> hm) {
		nodeLatPointers=hm;
	}


	/** Read one line from a file, starting at the file pointer
	 * 
	 * @param f
	 * @return Read one line from a file, starting at the file pointer
	 * @throws IOException
	 */
	private String[] readOneLine(RandomAccessFile f) throws IOException {
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

		int length = 0;
		boolean end = false;
		while (!end) {
			byte[] seg = new byte[byteToRead];
			f.read(seg);

			for (int i = 0; i < seg.length; i++) {
				if (seg[i] == 10) {
					length += i;
					end = true;
					break;
				}
				if (i == seg.length - 1) {
					length += byteToRead;
				}
			}
		}

		// read bytes of line
		f.seek(pointer);
		byte[] bytes = new byte[length];
		if (f.read(bytes) != length) {
			throw new IOException("ERROR: incorrect number of bytes");
		}

		String line = new String(bytes);
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
	 * Given a wayID, search the *ways file* for correct line
	 * and return a Way object with all relevant info
	 */
	public Way getWay(String wayID) throws IOException {

		// do binary search
		RandomAccessFile raf = new RandomAccessFile(waysFile, "r");
		String[] line = binarySearch(raf, ways_idCol, wayID);
		if(line==null) {
			System.out.printf("No such way ID: %s\n", wayID);
			return null;
		}

		//convert 'line' to Way object
		String id = line[ways_idCol];
		String startID = line[ways_startCol];
		String endID = line[ways_endCol];
		String name = line[ways_nameCol];

		Preconditions.checkState(id.equals(wayID)); // id should be the same one that was requested

		return new Way(id, startID, endID, name);
	}


	/**
	 * Given a nodeID, search the *nodes file* for correct line
	 * and return a single LocationNode object with all relevant info
	 */
//	public LocationNode getLocationNode(String nodeID) throws IOException {
//		// do binary search
//		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
//		String[] line = binarySearch(raf, nodes_idCol, nodeID);
//		if(line==null) {
//			System.out.printf("No such node ID: %s\n", nodeID);
//			return null;
//		}
//
//		LocationNode ln = createLocationNode(line);
//
//		Preconditions.checkState(ln.id.equals(nodeID)); // id should be the same one that was requested
//		raf.close();////
//		return ln;		
//	}

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
		while(nameHere.equals(streetName)) {
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
		while(nameHere.equals(streetName)) {
			Collections.addAll(nodeIDset, readOneLine(raf)[index_nodesCol].split(","));
			nextNewLine(raf); 
			nameHere = getWordAt(raf);
		}

		return nodeIDset;
	}

	/**idSetOne
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
		//previousNewLine(raf);
		
		//if anything above, get it
		if(raf.getFilePointer()!=topOfFile) {
			
			previousNewLine(raf); 
			//previousNewLine(raf);
			nodeLine = readOneLine(raf);
			nodeIDhere = nodeLine[nodes_idCol];
		
			while(isOnSamePage(nodeID, nodeIDhere)) {
				
				pageList.add(createLocationNode(nodeLine));
				//previousNewLine(raf); 
				previousNewLine(raf);

				if(raf.getFilePointer()==topOfFile) //if at top of file
					break;
				
				previousNewLine(raf);
				nodeLine = readOneLine(raf);
				nodeIDhere = nodeLine[nodes_idCol];
				//previousNewLine(raf);
				//previousNewLine(raf);
				
			}
		}
		// seek original
		raf.seek(originalPtr);
		
		// if anything below, get it
		nextNewLine(raf);
		if(raf.getFilePointer() < raf.length()) {
			nodeLine = readOneLine(raf);
			nodeIDhere = nodeLine[nodes_idCol];
			
			while(isOnSamePage(nodeID, nodeIDhere) && raf.getFilePointer()<raf.length()) {
				pageList.add(createLocationNode(nodeLine));
				nextNewLine(raf); 
				
				nodeLine = readOneLine(raf);
				nodeIDhere = nodeLine[nodes_idCol];
			}
		}
		return pageList;
	}

	/**
	  helper for getNodePage
	  return true if the two IDs have the same first eight digits
	**/
	private boolean isOnSamePage(String origID, String queryID) {
		//   /n/xxxx.yyyy.....
		String eightDigs = origID.substring(3,7) + origID.substring(8,12);
		String eightDigs2 = queryID.substring(3,7) + queryID.substring(8,12);
		return eightDigs.equals(eightDigs2); 
	}

	private LocationNode createLocationNode(String[] line) {
		//convert 'line' to LocationNode object
		String id = line[nodes_idCol];
		String ways = line[nodes_waysCol];
		LatLong latlong = new LatLong(line[nodes_latCol], line[nodes_lonCol]);
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
		System.out.printf("No intersection found between %s and %s\n", street1, street2);
		return "";
	}



	/**
	 * Given a wayID, search the *ways file* for correct line
	 * and return a Way object with all relevant info
	 */
	public Set<String> getAllStreetNames() throws IOException {
		Set<String> streets = new HashSet<>();
		
		RandomAccessFile raf = new RandomAccessFile(waysFile, "r");
		nextNewLine(raf); //skip header with titles

		long fileSize = raf.length();
		long topOfCurrLine;
		String lat = "9999";
		String[] line;
		String id, name, currLat;
		
		while(raf.getFilePointer() < fileSize) {
			// read line
			topOfCurrLine = raf.getFilePointer();
			line = readOneLine(raf);
			id = line[ways_idCol];
			name = line[ways_nameCol];
			name = name.toLowerCase();
			// add street name to set
			streets.add(name);
			
			// add latitude to hashmap
			currLat = id.substring(3, 7);
			if(!lat.equals(currLat)) {
				if(!wayLatPointers.containsKey(currLat)) {
					wayLatPointers.put(currLat, topOfCurrLine);
				}
				lat = currLat;
			}
			
			nextNewLine(raf);
		}
		raf.close();
		
		return streets;
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * @param latTop
	 * @param latBottom
	 * @return
	 * @throws IOException
	 */
	public List<Way> getAllWaysWithin(String latTop, String latBottom) throws IOException {

		RandomAccessFile raf = new RandomAccessFile(waysFile, "r");
		
		
		long top = 0;
		long bottom = raf.length();
		
		//TODO extend this so it checks if it contains the incremented key etc
		if(wayLatPointers.containsKey(latTop)) {
			top = wayLatPointers.get(latTop);
		}
		if (wayLatPointers.containsKey(latBottom)) {
			bottom = wayLatPointers.get(latBottom);
		}

		raf.seek(top);

		List<Way> wl = new ArrayList<>();
		
		//long fileSize = raf.length();
		while(raf.getFilePointer() < bottom) {
			String[] line = readOneLine(raf);
			String id = line[ways_idCol];
			String startID = line[ways_startCol];
			String endID = line[ways_endCol];
			String name = line[ways_nameCol];
			wl.add(new Way(id, startID, endID, name));
			nextNewLine(raf);
		}
		raf.close();

		
		return wl;
	}


	
	//  "/w/11.22.234.53454"  ==>   "1122"
	public static String getFirstFourFromID(String id) {
		return id.substring(3, 7);
	}
	
	//  "/w/1122.23453454"  ==>   "11222345"
//	public static String getFirstEightFromID(String id) {
//		return id.substring(3,7) + id.substring(9, 13);
//	}
	
	
	/**
	 * GET LOCATION NODE
	 * 
	 * 1. find bounds
	 * 2. do binary search
	 * 3. create LocationNode object
	 */
	public LocationNode getLocationNode(String nodeID) throws IOException {
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
	
		// get the bounds of where to look

		String chunkID = getKeyValueFromID(nodeID);
		
		long filePointerTop = 0; 
		long filePointerBottom = 0;//fileSize;
		// set top file pointer
		if(nodeLatLongPointers.containsKey(chunkID)) {
			filePointerTop = nodeLatLongPointers.get(chunkID).get(0);
			filePointerBottom = nodeLatLongPointers.get(chunkID).get(1);
		}
		else {
			System.out.println("ERRORRR why does hashtable not contain "+chunkID);
		}

//		long[] bounds = getByteBoundsNodeID(nodeID);
//		long latTop = bounds[0];
//		long latBottom = bounds[1];
		
		
		// do a binary search
		String foundLine[] = null;

		file.seek(filePointerTop); // points to beginning of first line of chunk
		foundLine = binarySearch(file, nodes_idCol, nodeID);
//		foundLine = binarySearchID(file, nodes_idCol, chunkID);
/*		while (file.getFilePointer() < fileSize) {
			System.out.println("not over yet!.");

		file.seek(latTop);

		long start = latTop;
		long end = latBottom;
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file);
			int difference = nodeID.compareToIgnoreCase(currentLine[nodes_idCol]);
			if (difference == 0) {
				file.close();
				foundLine = currentLine;
				break;
				
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 
		file.close();
		*/
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
		
		Map<String, LocationNode> nodeMap = new HashMap<>();
		
		while(raf.getFilePointer() < bottom) {
			
			String[] line = readOneLine(raf);
			LocationNode node = createLocationNode(line);
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
	
		// first = "1234.7777"
	// second = "1235.7777" 
	// top = 8hashmap contains "1234.7777" ?  pointer   :   get from 4-dig hashtable ("1234")
	// bottom = 8hashmap contains "1235.7777?    pointer   :   get from 4-dig hashtable ("1236") -> end of 1235

	// "1234","2234"   -->  find the closest 8-digit thing
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
	


	
	
	
	
	
	
	

	
}
//	
//	
//	
//	
//	
//	public List<LocationNode> getAllLocationNodesWithin(String latTop, String latBottom) throws IOException {
//
//		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
//		
//		
//		long top = 0;
//		long bottom = raf.length();
//		
//		//TODO extend this so it checks if it contains the incremented key etc
//		if(wayLatPointers.containsKey(latTop)) {
//			top = wayLatPointers.get(latTop);
//		}
//		if (wayLatPointers.containsKey(latBottom)) {
//			bottom = wayLatPointers.get(latBottom);
//		}
//
//		raf.seek(top);
//
//		List<Way> wl = new ArrayList<>();
//		
//		//long fileSize = raf.length();
//		while(raf.getFilePointer() < bottom) {
//			String[] line = readOneLine(raf);
//			String id = line[ways_idCol];
//			String startID = line[ways_startCol];
//			String endID = line[ways_endCol];
//			String name = line[ways_nameCol];
//			wl.add(new Way(id, startID, endID, name));
//			nextNewLine(raf);
//		}
//		raf.close();
//
//		
//		return wl;
//	}
//	
//	
//	
//	/**
//	 * helper for getLocationNode to find which bounds to look between
//	 * (by looking in the hashmap)
//	 */
//	public long[] getByteBoundsNodeID(String id) throws IOException{
//		// find max length
//		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
//		long length = raf.length();
//		raf.close();
//		
//		// set top and bottom bounds
//		String givenLat = getFirstFourFromID(id);
//		Long longVal = Long.parseLong(givenLat);
//
//		
//		// to find upper bound, decrement lat until it is min OR until it is in hashtable
//		Long upper = longVal;
//		while(!(nodeLatLongPointers.containsKey(Long.toString(upper)) && upper > nodes_minLat)) {
//			upper--;
//		}
//		
//		// to find lower bound, increment lat until it is max OR until it is in hashtable
//		
//		Long lower = longVal+1;
//		while(!(nodeLatLongPointers.containsKey(Long.toString(lower)) && lower < nodes_maxLat)) {
//			longVal++;
//		}
//		
//		String topChunk = Long.toString(upper);
//		String bottomChunk = Long.toString(lower);
//
//		// now that you know chunks, get bounds (LONGS) from hashtable 
//		long latTop = 0; 
//		long latBottom = length;
//		if(nodeLatLongPointers.containsKey(topChunk)) {
//			latTop = nodeLatLongPointers.get(topChunk);
//		}
//		if(nodeLatLongPointers.containsKey(bottomChunk)) {
//			latBottom = nodeLatLongPointers.get(bottomChunk);
//		}
//		return new long[]{latTop, latBottom};
//	}

