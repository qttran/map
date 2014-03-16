package cs32.maps.FileReader;

import java.io.IOException;
import java.io.RandomAccessFile;

import cs32.maps.LocationNode;
import cs32.maps.Way;

public class MapsIO {
	
	String waysFile;
	String nodesFile;
	String IndexFile;
	public MapsIO(String waysFile, String nodesFile, String IndexFile) {
		this.waysFile = waysFile;
		this.nodesFile = nodesFile;
		this.IndexFile = IndexFile;
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
		while (!start) {
			byte[] seg = new byte[256];
			for (int i = 0; i < seg.length; i++) {
				seg[i] = 0;
			}
			long pointer2 = f.getFilePointer() - 256;
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
			byte[] seg = new byte[256];
			f.read(seg);

			for (int i = 0; i < seg.length; i++) {
				if (seg[i] == 10) {
					length += i;
					end = true;
					break;
				}
				if (i == seg.length - 1) {
					length += 256;
				}
			}
		}

		// read bytes of line
		f.seek(pointer);
		byte[] bytes = new byte[length];
		if (f.read(bytes) != length) {
			throw new IOException("ERROR: wrong number of bytes");
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
			System.out.println("prepare to seek mid");
			file.seek(mid);
			System.out.println("sook mid");
			String[] currentLine = readOneLine(file).split("\t");

			System.out.println("line splitted");
			int difference = toFind.compareTo(currentLine[whichCol]);
			if (difference == 0) {
				System.out.println("currentLine returned");
				return currentLine;
			}
			else if (difference > 0) 
				start = mid + 1;
			else 
				end = mid - 1;
			mid = (start + end)/2;
			System.out.println("Start: " + start + ". Mid: " + mid + ". End: " + end);
			System.out.println(end > start);
		} 
		// if not found
		return null;
	}



	
	
	public Way getWay(String wayID) {
		// TODO Auto-generated method stub
		return null;
	}



	public LocationNode getLocationNode(String oppositeNodeID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

