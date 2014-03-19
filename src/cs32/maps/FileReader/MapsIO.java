package cs32.maps.FileReader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

	public MapsIO(String waysFile, String nodesFile, String indexFile) {
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
	private String readOneLine(RandomAccessFile f) throws IOException {
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
		return line;
	}



	public String[] binarySearch(RandomAccessFile file, int whichCol, String toFind) throws IOException {
		file.seek(0);
		long start = readOneLine(file).length();
		long end = (file.length() - 1);
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file).split("\t");

			int difference = toFind.compareTo(currentLine[whichCol]);
			if (difference == 0) {
				file.close();
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
	public LocationNode getLocationNode(String nodeID) throws IOException {
		// do binary search
		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		String[] line = binarySearch(raf, nodes_idCol, nodeID);
		if(line==null) {
			System.out.printf("No such node ID: %s\n", nodeID);
			return null;
		}

		LocationNode ln = createLocationNode(line);

		Preconditions.checkState(ln.id.equals(nodeID)); // id should be the same one that was requested

		return ln;		
	}



	/**
	 * Given a nodeID, search the *nodes file* for correct line
	 * and return a list of LocationNode objects that have ids
	 * with the same 8 digits
	 * TODO
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

		//TODO read a block and add others to list!

		return pageList;
	}


	private LocationNode createLocationNode(String[] line) {
		//convert 'line' to LocationNode object
		String id = line[nodes_idCol];
		String ways = line[nodes_waysCol];
		LatLong latlong = new LatLong(line[nodes_latCol], line[nodes_lonCol]);
		//create ways list
		List<String> wayList = new ArrayList<>();
		for(String s : ways.split(",")) {
			wayList.add(s);
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
	public String getIntersection(String street1, String street2) throws IOException {
		//TODO
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
		List<String> nodes = new LinkedList<>();
		String toFind = street1;
		for (int i = 0; i<2; i++) {
			if (i == 1) toFind = street2;

			//binary search for the street
			file.seek(0);
			long start = readOneLine(file).length();
			long end = (file.length() - 1);
			long mid = (start + end)/2;
			while (end > start) {
				file.seek(mid);
				String[] currentLine = readOneLine(file).split("\t");
				System.out.println("binary: " + file.getFilePointer());

				int difference = toFind.compareTo(currentLine[index_nameCol]);
				if (difference == 0) {
					for (String node: currentLine[index_nodesCol].split(",")) {
						if (i == 1 && nodes.contains(node)) 
							return node;
						else nodes.add(node);
					}
				}
				else if (difference > 0) 
					start = mid + 1;
				else 
					end = mid - 1;
				mid = (start + end)/2;
			} 

			//continue reading upwards
			int difference = 0;
			while (true) { 
				String[] currentLine = readOneLine(file).split("\t");
				difference = toFind.compareTo(currentLine[index_nameCol]);
				System.out.println("upwards: " + file.getFilePointer());
				if (difference == 0) {
					for (String node: currentLine[index_nodesCol].split(",")) {
						if (i == 1 && nodes.contains(node)) 
							return node;
						else nodes.add(node);
					}
				} else break;
			}

			// move downwards
			long lowMid = mid;

			difference = 0;
			while (true) { 
				file.seek(lowMid);
				String current = readOneLine(file);
				String[] currentLine = current.split("\t");
				difference = toFind.compareTo(currentLine[index_nameCol]);
				lowMid -= file.getFilePointer() - current.length() - 1; 
				System.out.println("downwards: " + file.getFilePointer());

				if (difference == 0) {
					for (String node: currentLine[index_nodesCol].split(",")) {
						if (i == 1 && nodes.contains(node)) 
							return node;
						else nodes.add(node);
					}
				} else break;
			}
		}
		file.close();
		return null;
	}


	/**
	 * Get a set of all street names from the index file. To be used for
	 * populating the autocomplete Trie.
	 * 
	 * @return Set<String>
	 * @throws IOException
	 */
	public Set<String> getStreetNames() throws IOException { // for adding to Trie

		Set<String> streetNames = new HashSet<>();

		RandomAccessFile raf = new RandomAccessFile(indexFile, "r");
		nextNewLine(raf); //skip header with titles
		String line = "";
		long fileSize = raf.length();

		while(raf.getFilePointer() < fileSize) {
			line = this.readOneLine(raf);
			String[] spl = line.split("\t");
			streetNames.add(spl[index_nameCol]);
			nextNewLine(raf);
		}
		raf.close();
		return streetNames;
	}

	/**
	 * Get a list of all LatLongs from the nodes file. To be used for
	 * populating the KDTree.
	 * 
	 * @return List<LatLong>
	 * @throws IOException
	 */
	public List<LatLong> getLatLongs() throws IOException { // for adding to KDTree
		List<LatLong> points = new ArrayList<>();

		RandomAccessFile raf = new RandomAccessFile(nodesFile, "r");
		nextNewLine(raf); //skip header with titles
		String line = "";
		long fileSize = raf.length();

		while(raf.getFilePointer() < fileSize) {
			line = this.readOneLine(raf);
			String[] spl = line.split("\t");
			LatLong ll = new LatLong(spl[nodes_latCol], spl[nodes_lonCol]);
			points.add(ll);
			nextNewLine(raf);
		}
		raf.close();
		return points;
	}


	/**Get page: Return a list of location nodes that have the same first 4 digits
	 * of latitude and first 4 digits of longitude @qttran
	 * @param node
	 * @return List<LocationNode>
	 * @throws IOException
	 */
	public List<LocationNode> getPage(LocationNode node) throws IOException {
		RandomAccessFile file = new RandomAccessFile(nodesFile, "r");
		String toFind = node.id.substring(0, 12);
		List<LocationNode> result = new LinkedList<LocationNode>();

		file.seek(0);
		long start = readOneLine(file).length();
		long end = (file.length() - 1);
		long mid = (start + end)/2;
		while (end > start) {
			file.seek(mid);
			String[] currentLine = readOneLine(file).split("\t");

			int difference = toFind.compareTo(currentLine[nodes_idCol].substring(0, 12));
			if (difference == 0) {
				LocationNode toAdd = createLocationNode(currentLine);
				result.add(toAdd);
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
		} 

		long lowMid = mid - 1;

		int difference = 0;
		while (true) { 
			file.seek(lowMid);
			String[] currentLine = readOneLine(file).split("\t");
			difference = toFind.compareTo(currentLine[nodes_idCol].substring(0, 12));
			lowMid -= 1;

			if (difference == 0) {
				LocationNode toAdd = createLocationNode(currentLine);
				result.add(toAdd);
			} else break;
		}


		long highMid = mid + 1;
		difference = 0;
		while (true) { 
			file.seek(highMid);
			String[] currentLine = readOneLine(file).split("\t");
			difference = toFind.compareTo(currentLine[nodes_idCol].substring(0, 12));
			highMid +=1;

			if (difference == 0) {
				LocationNode toAdd = createLocationNode(currentLine);
				result.add(toAdd);
			} else break;
		}

		file.close();
		return result;
	}


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
}

