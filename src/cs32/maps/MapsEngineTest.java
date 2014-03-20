package cs32.maps;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import cs32.maps.gui.StreetNode;

public class MapsEngineTest {

	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";

	private static final String home = System.getProperty("user.home");
	public static final String smallways2 = home + "/course/cs032/map/testfiles/smallWays2.tsv";
	public static final String smallnodes2 = home + "/course/cs032/map/testfiles/smallNodes2.tsv";
	public static final String smallindex2 = home + "/course/cs032/map/testfiles/smallIndex2.tsv";


	@Test
	public void latPointersTest() throws IOException {
		MapsEngine en = new MapsEngine (ways,nodes,index);
		assertTrue(en.fileReader.nodeLatPointers.containsKey("4015"));
		assertTrue(en.fileReader.nodeLatPointers.containsKey("4047"));
		assertTrue(!en.fileReader.nodeLatPointers.containsKey("4000"));
	}


	@Test
	public void waysWithinTest() throws IOException {
		/*System.out.println(" street node getting test ");
		MapsEngine en = new MapsEngine(ways, nodes, index);
		System.out.println("done creating engine");
		Point2D.Double t = new Point2D.Double(40.1631743, -73.747346);
		Point2D.Double b = new Point2D.Double(40.1881004, -73.7385398);
		Set<StreetNode> sn = en.getStreetNodes(t, b);
		for(StreetNode s : sn) {
			System.out.println(sn.toString());
		}*/
	}


	@Test
	public void testKDTreeLatLongBuild() throws IOException {
		MapsEngine en = new MapsEngine(ways, nodes, index);
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4015.7374"));
		assertTrue(!en.fileReader.nodeLatLongPointers.containsKey("4999.6766"));
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4209.7169"));
		//System.out.println(en.fileReader.nodeLatLongPointers.get("4015.7374"));
		//System.out.println(en.fileReader.nodeLatLongPointers.get("4017.7374"));
		//System.out.println(en.fileReader.nodeLatLongPointers.get("4015.7374"));
		//System.out.println(en.fileReader.nodeLatLongPointers.get("4017.7374") == 907);
	}


	@Test
	public void testGetBytePointers() throws IOException {
		MapsEngine e = new MapsEngine(ways, nodes, index);
		long[] pts = (e.forTestingGetBytes("4016.7374", "4016.7389")); //first one is in HM, second not
		assertTrue(pts[0] == 183); // should be 183

		//Map<String, LocationNode> ln = e.nodesChunkTest("4016.7374", "4016.7389");
		//for(String n :ln.keySet()) {
		//	System.out.println(n);
		//}
		//System.out.println("next");

		//ln = e.nodesChunkTest("4018.7372", "4018.7379");
		//for(String n :ln.keySet()) {
		//	System.out.println(n);
		//}



		//ln = e.nodesChunkTest("4152.7143", "4152.7145");
		//for(String n :ln.keySet()) {
		//	System.out.println(n);
		//}


	}


	@Test
	public void getPathFromNodeID() throws IOException {
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
	}

	@Test
	public void pagingTime() throws IOException{
		//MapsEngine e = new MapsEngine(ways, nodes, index);
		//System.out.println("<---------->");
		//Set<StreetNode> sn = e.getStreetNodesWithin("4072.7218", "4072.7328");
		//System.out.println("DONE "+sn.size());
	}

	@Test
	public void getStreetNodesTest() throws IOException {
		MapsEngine en = new MapsEngine(ways, nodes, index);
		Point2D.Double topLeft = new Point2D.Double();
		topLeft.setLocation(40.150000, -73.7485663);
		Point2D.Double botRight = new Point2D.Double();
		botRight.setLocation(40.15999999, -72.900080);
		Set<StreetNode> set = en.getStreetNodes(topLeft, botRight);
		assertTrue(set.size() == 1);
		for (StreetNode node: set) {
			assertTrue(node.x1 == 40.1581762);
			assertTrue(node.x2 == 40.1615135);
			assertTrue(node.y1 == -73.7485663);
			assertTrue(node.y2 == -73.7479794);
		}
	}
}
