package cs32.maps;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import KDTree.Coordinates;

import cs32.maps.MapsEngine;
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
	

	@Test
	public void testKDTreeLatLongBuild() throws IOException {
		MapsEngine en = new MapsEngine(ways, nodes, index);
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4015.7374"));
		assertTrue(!en.fileReader.nodeLatLongPointers.containsKey("4999.6766"));
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4209.7169"));
		System.out.println(en.fileReader.nodeLatLongPointers.get("4015.7374"));
		System.out.println(en.fileReader.nodeLatLongPointers.get("4017.7374"));
	}
	

}
