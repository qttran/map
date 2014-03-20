package cs32.maps.FileReader;

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
	
	
	private HashMap<Integer, Long> wayLatPointers;
	public HashMap<String, Long> nodeLatLongPointers;
	
	public MapsIO(String waysFile, String nodesFile, String indexFile) {
		
		wayLatPointers = new HashMap<>(); 
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
	
	public void setNodeLatLongPtrs(HashMap<String, Long> pointerMap) {
		nodeLatLongPointers = pointerMap;
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
			System.out.println(currentLine[whichCol]+ ", " + toFind);
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

	public String[] binarySearchID(RandomAccessFile file, int whichCol, String toFind) throws IOException {
		file.seek(0);
		nextNewLine(file);
		long start = file.getFilePointer();
		long end = (file.length() - 1);
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file);
			int difference = toFind.compareToIgnoreCase(currentLine[whichCol].substring(3,12));
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
		//  /w/4115.7154.2343242
		String firstFour = wayID.substring(3, 7);
		int firstFourLat = Integer.parseInt(firstFour);
		int count = 0;
		
		// find UPPER for binary search
		// look back max 10 lat-counts to see if any of them are in the hashtable.
		int topChunk = firstFourLat;
		while( !wayLatPointers.containsKey(topChunk) && count < 10) {
			topChunk--;
			count++;
		}
		count=0;
		// find LOWER for binary search
		// look forward max 10 lat-counts to see if any of them are in the hashtable
		int bottomChunk = firstFourLat+1;
		while( !wayLatPointers.containsKey(bottomChunk) && count < 10) {
			topChunk++;
			count++;
		}
		
		
		// do binary search
		RandomAccessFile raf = new RandomAccessFile(waysFile, "r");
		nextNewLine(raf);
		long topFP = raf.getFilePointer();
		long bottomFP = raf.length();
		
		// if I found pointers for chunk, set top and bottom
		if(wayLatPointers.containsKey(topChunk))
			topFP = wayLatPointers.get(topChunk);
		if(wayLatPointers.containsKey(bottomChunk))
			bottomFP = wayLatPointers.get(bottomChunk);
		
	
		raf.seek(topFP);
		long start = topFP;
		long end = bottomFP;
		long mid = (start + end)/2;
		String[] currentLine = null;
		while (end > start) {
			raf.seek(mid);
			currentLine = readOneLine(raf);
			int difference = wayID.compareToIgnoreCase(currentLine[ways_idCol]);
			if (difference == 0) {
				break; //currentLine is correct
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 
		raf.close();
		
		
		if(currentLine==null) {
			System.out.printf("No such way ID: %s\n", wayID);
			return null;
		}

		//convert 'line' to Way object
		String id = currentLine[ways_idCol];
		String startID = currentLine[ways_startCol];
		String endID = currentLine[ways_endCol];
		String name = currentLine[ways_nameCol];

		Preconditions.checkState(id.equals(wayID)); // id should be the same one that was requested

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
		long fileSize = raf.length();
		
		String[] line = binarySearch(raf, nodes_idCol, nodeID);
		//TODO IS IT FASTER TO CALL getLocationNode()   [[it does linear using pointers]]
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
			//previousNewLine(raf);
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
		if(raf.getFilePointer() < fileSize) {
			nodeLine = readOneLine(raf);
			nodeIDhere = nodeLine[nodes_idCol];
			
			while(areOnSamePage(nodeID, nodeIDhere) && raf.getFilePointer()<fileSize) {
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
		LatLong latlong = new LatLong(line[nodes_latCol], line[nodes_lonCol]);
		//create ways list
		List<String> wayList = new ArrayList<>();
		if(ways.length()!=0) {
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
	 * get all street names AND populate the hashtable with WAYS info
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
				if(!wayLatPointers.containsKey(Integer.parseInt(currLat))) {
					wayLatPointers.put(Integer.parseInt(currLat), topOfCurrLine);
				}
				lat = currLat;
			}
			
			nextNewLine(raf);
		}
		raf.close();
		
		return streets;
	}
	
	
	
	
		

	
	
	/**
	 * GET LOCATION NODE
	 * 
	 * 1. find chunk pointer (8-digit-chunk-ID)
	 * 2. do linear search
	 * 3. create LocationNode object
	 */
	public LocationNode getLocationNode(String nodeID) throws IOException {
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
		long fileSize = file.length();
		// get the bounds of where to look
		String chunkID = getKeyValueFromID(nodeID);
		
		long filePointerTop = 0; 
		// set top file pointer
		if(nodeLatLongPointers.containsKey(chunkID)) {
			filePointerTop = nodeLatLongPointers.get(chunkID);
		}
		else {
			System.out.println("ERRORRR why does hashtable not contain "+chunkID);
		}
		
		
		// do a LINEAR search
		String foundLine[] = null;
		file.seek(filePointerTop); // points to beginning of first line of chunk
		foundLine = binarySearchID(file, nodes_idCol, chunkID);
/*		while (file.getFilePointer() < fileSize) {
			System.out.println("not over yet!.");
			String[] currentLine = readOneLine(file);
			String IDhere = currentLine[nodes_idCol];
			if(IDhere.equals(nodeID)) {
				foundLine = currentLine;
				break; // found node
			}System.out.println(foundLine);
			if(!getKeyValueFromID(IDhere).equals(chunkID)) {
				// IF NO longer in this chunk (BADDD)
				break;
			}
			
		} 
		file.close();*/
		
		if(foundLine == null) {
			System.out.printf("ERROR: no such node %s\n", nodeID);
			return null;
		}
		for (String s : foundLine) {
			System.out.println(s);
		}
		
		return createLocationNode(foundLine);
	}

	
	
	
	
	
	
	
	
	
	
	
	// "4017.7343"  "4018.9347"
	// "....7342  7346..
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
	 * 
	 * uses FIRST 8 DIGITS OF ID
	 */
	public long[] getByteBounds(String topEightMapKey, String bottomEightMapKey) throws IOException {
		// find max length
		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		long length = raf.length();
		raf.close();
		
		// set top and bottom bounds
		Long givenUp = getLongValue(topEightMapKey); // "1111.2345"  ->   11112345
		Long givenBottom = getLongValue(bottomEightMapKey);
		int count = 0;
		// to find upper bound, decrement lat until it is min OR until it is in hashtable
		while(!nodeLatLongPointers.containsKey(getKeyValue(givenUp))  && count<10) {
			
			givenUp--;
			count++;
		}
		count = 0;
		// to find lower bound, increment lat until it is max OR until it is in hashtable
		while(!nodeLatLongPointers.containsKey(getKeyValue(givenBottom)) && count<10) {
			count++;
			givenBottom++;
		}

		String topChunk = getKeyValue(givenUp); // 11112222 -> "1111.2222"
		String bottomChunk = getKeyValue(givenBottom);

		// now that you know chunks, get bounds (LONGS) from hashtable 
		long topFilePointer = 0; 
		long bottomFilePointer = length;
		if(nodeLatLongPointers.containsKey(topChunk)) {
			topFilePointer = nodeLatLongPointers.get(topChunk);
		}
		if(nodeLatLongPointers.containsKey(bottomChunk)) {
			bottomFilePointer = nodeLatLongPointers.get(bottomChunk);
		}
		System.out.printf("%s mapped to %s\n%s mapped to %s\n\n", topChunk, topFilePointer, bottomChunk, bottomFilePointer);
		return new long[]{topFilePointer, bottomFilePointer};
	}
	

	
	// first = "1234.7777"
	// second = "1235.7777"   1235.7700    "1235"
	// top = 8hashmap contains "1234.7777" ?  pointer   :   get from 4-dig hashtable ("1234")
	// bottom = 8hashmap contains "1235.7777?    pointer   :   get from 4-dig hashtable ("1236") -> end of 1235
	
	
	
	
	
	/************* LIIITTLE HELPERIntegerS *************/
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
