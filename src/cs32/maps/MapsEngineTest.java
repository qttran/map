package cs32.maps;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


import cs32.maps.MapsEngine;
import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class MapsEngineTest {
	
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";

	private static final String home = System.getProperty("user.home");
	public static final String smallways2 = home + "/course/cs032/map/testfiles/smallWays2.tsv";
	public static final String smallnodes2 = home + "/course/cs032/map/testfiles/smallNodes2.tsv";
	public static final String smallindex2 = home + "/course/cs032/map/testfiles/smallIndex2.tsv";
	

//	@Test
//	public void latPointersTest() throws IOException {
//		MapsEngine en = new MapsEngine (ways,nodes,index);
//		assertTrue(en.nodeLatPointers.containsKey("4015"));
//		assertTrue(en.nodeLatPointers.containsKey("4047"));
//		assertTrue(!en.nodeLatPointers.containsKey("4000"));
//	}
//	
//	@Test
//	public void latTest() throws IOException {
//		MapsEngine en = new MapsEngine(smallways2, smallnodes2, smallindex2);
//		System.out.println(en.nodeLatPointers.get("1111"));
//		System.out.println(en.nodeLatPointers.get("2222"));
//	}
	
//	@Test
//	public void waysWithinTest() throws IOException {
//		System.out.println(" street node getting test ");
//		MapsEngine en = new MapsEngine(ways, nodes, index);
//		System.out.println("done creating engine");
//		Point2D.Double t = new Point2D.Double(40.1631743, -73.747346);
//		Point2D.Double b = new Point2D.Double(40.1881004, -73.7385398);
//		Set<StreetNode> sn = en.getStreetNodesWithin(t, b);
//		for(StreetNode s : sn) {
//			System.out.println(sn.toString());
//		}
//		
//		
//		
//		
//		t = new Point2D.Double(40.1631743, -73.747346);
//		b = new Point2D.Double(41.404415, -71.4636);
//		sn = en.getStreetNodesWithin(t, b);
//	}
	

//	@Test
//	public void testKDTreeLatLongBuild() throws IOException {
//		MapsEngine en = new MapsEngine(ways, nodes, index);
//		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4015.7374"));
//		assertTrue(!en.fileReader.nodeLatLongPointers.containsKey("4999.6766"));
//		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4209.7169"));
//		System.out.println(en.fileReader.nodeLatLongPointers.get("4015.7374"));
//		System.out.println(en.fileReader.nodeLatLongPointers.get("4017.7374"));
//		//System.out.println(en.fileReader.nodeLatLongPointers.get("4015.7374"));
//		//System.out.println(en.fileReader.nodeLatLongPointers.get("4017.7374") == 907);
//	}
//	
//	
	@Test
	public void testGetBytePointers() throws IOException {
		MapsEngine e = new MapsEngine(ways, nodes, index);
		System.out.println("---");
		long[] pts = (e.forTestingGetBytes("4016.7374", "4016.7389")); //first one is in HM, second not
		System.out.println(pts[0]); // should be 183
		System.out.println(pts[1]);
		
		Map<String, LocationNode> ln = e.nodesChunkTest("4016.7374", "4016.7389");
		for(String n :ln.keySet()) {
			System.out.println(n);
		}
		System.out.println("next");
		
		ln = e.nodesChunkTest("4018.7372", "4018.7379");
		for(String n :ln.keySet()) {
			System.out.println(n);
		}
		
		
		System.out.println("next");
		
		ln = e.nodesChunkTest("4152.7143", "4152.7145"); //INCLUSIVE these 2 chunks and between
		
		
		List<String> lt = new ArrayList<String>();
		for(String n :ln.keySet()) {
			lt.add(n);
		}
		Collections.sort(lt);
		for(String s : lt) {
			System.out.println(s);
		}
	}
	
	@Test
	public void testGetChunk() throws IOException {
		MapsEngine e = new MapsEngine(ways, nodes, index);
		Map<String, LocationNode> ln = e.nodesChunkTest("4016.7000", "4016.7399"); //INCLUSIVE these 2 chunks and between		
		Set<String> idsInChunk = ln.keySet();
		assertTrue(idsInChunk.size() == 5);
		assertTrue(idsInChunk.contains("/n/4016.7374.527767844"));
		assertTrue(idsInChunk.contains("/n/4016.7374.527767845"));
		assertTrue(idsInChunk.contains("/n/4016.7374.527767846"));
		assertTrue(idsInChunk.contains("/n/4016.7374.527767850"));
		assertTrue(idsInChunk.contains("/n/4016.7374.527767852"));

		
		
	}
//	
//	
//	@Test
//	public void getPathFromNodeID() throws IOException {
//		MapsEngine e = new MapsEngine(ways, nodes, index);
//		System.out.println("-- engine created --");
//		String idStart = "/n/4104.7079.527768981";
//		String idEnd = "/n/4104.7078.527769006";
//		String path = e.getBestPathNodeIDs(idStart, idEnd);
//		System.out.println(path);
//		
//		idStart = "/n/4073.7215.527768450";
//		idEnd = "/n/4074.7212.527768452";
//		path = e.getBestPathNodeIDs(idStart, idEnd);
//		System.out.println(path);
//		System.out.println("DONE");
//	}
//	
//	@Test
//	public void pagingTime() throws IOException{
//		MapsEngine e = new MapsEngine(ways, nodes, index);
//		System.out.println("<---------->");
//		Set<StreetNode> sn = e.getStreetNodesWithin("4072.7218", "4072.7328");
//		System.out.println("DONE "+sn.size());
//	}
	
	@Test
	public void getStreetNodesTest() throws IOException {
//		MapsEngine en = new MapsEngine(ways, nodes, index);
//		System.out.println("kdTree built");
//		Point2D.Double topLeft = new Point2D.Double();
//		topLeft.setLocation(40.1581762, -72.7485663);
//		Point2D.Double botRight = new Point2D.Double();
//		botRight.setLocation(40.190990, -73.900080);
//		Set<StreetNode> set = en.getStreetNodes(topLeft, botRight);
//		for (StreetNode node: set) {
//			System.out.println("x1: " + node.x1);
//			System.out.println("x2: " + node.x2);
//			System.out.println("y1: " + node.y1);
//			System.out.println("y2: " + node.y2);
		
		
//		}
		
		MapsEngine en = new MapsEngine(ways, nodes, index);
		System.out.println("kdTree built");
		Point2D.Double topLeft = new Point2D.Double();
		topLeft.setLocation(41.837835346089896, -71.40527713291807);
		Point2D.Double botRight = new Point2D.Double();
		botRight.setLocation(41.798835346089895, -71.36627713291809);
		Set<StreetNode> set = en.getStreetNodes(topLeft, botRight);
		for (StreetNode node: set) {
			System.out.println("x1: " + node.x1);
			System.out.println("x2: " + node.x2);
			System.out.println("y1: " + node.y1);
			System.out.println("y2: " + node.y2);
		}
	}
}
