package cs32.maps.FileReader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import cs32.maps.LatLong;
import cs32.maps.LocationNode;
import cs32.maps.Way;

public class MapsIOTest {
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";
	
	public static final String smallways2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallWays2.tsv";
	public static final String smallnodes2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallNodes2.tsv";
	public static final String smallindex2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallIndex2.tsv";
	
	
	@Test
	public void testColumnFinder() {
		MapsIO io = new MapsIO(ways, nodes, index);
		assertTrue(io.ways_idCol == 0);
		assertTrue(io.ways_nameCol == 1);
		assertTrue(io.ways_startCol == 7);
		assertTrue(io.ways_endCol == 8);
	}

	
	@Test
	public void testGetWay() throws FileNotFoundException, IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		
		Way way = io.getWay("/w/4016.7374.42295268.1.2"); // fourth item in ways.tsv
		
		
		assertTrue(way.id.equals("/w/4016.7374.42295268.1.2"));
		assertTrue(way.name.equals("United States of America"));
		assertTrue(way.startNodeID.equals("/n/4016.7374.527767846"));
		assertTrue(way.endNodeID.equals("/n/4016.7374.527767845"));
		
		//System.out.println(way.toString());
	}
	
	@Test
	public void streetNameAggregation() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		Set<String> streetNames = io.getStreetNames();
		//for(String s : streetNames) {
			//System.out.println(s);
		//}
		//System.out.printf("%s street names in total.\n", streetNames.size());
	}
	
	@Test
	public void latLongAggregation() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		List<LatLong> pts = io.getLatLongs();
		/*for(LatLong s : pts) {
			System.out.println(s.toString());
		}*/
		//System.out.printf("%s latlong pairs in total.\n", pts.size());
	}
	
	@Test
	public void getIntersection() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		String street1 = "10 Avenue";
		String street2 = "Buttonwoods Avenue";
		String intersection = io.getIntersection(street1, street2);
		System.out.println(intersection);
		assertTrue(intersection.equals("/n/4168.7141.201141885"));
	}
	
	
	@Test
	public void getIntersectionTest() throws IOException {
		MapsIO io = new MapsIO(smallways2, smallnodes2, smallindex2);
		String nodeID = io.getIntersection("Main Street", "One Wai Street");
		assertTrue(nodeID.equals("/n/1111.2222.33"));
		
		nodeID = io.getIntersection("Banana Court", "One Wai Street");
		assertTrue(nodeID.equals("/n/1111.2222.55"));
		
		nodeID = io.getIntersection("Main Street", "One Wai Street");
		assertTrue(nodeID.equals("/n/1111.2222.33"));
		
		nodeID = io.getIntersection("Scorpio", "solo");
		assertTrue(nodeID.equals("/n/1111.2222.55"));
		
		nodeID = io.getIntersection("1 Court", "Scorpio");
		assertTrue(nodeID.equals("/n/2222.3333.77"));
		
		
	}
	
	
	@Test
	public void getNodePageTest() throws IOException {
		MapsIO io = new MapsIO(smallways2, smallnodes2, smallindex2);
		
		// top of page
		List<LocationNode> page = io.getNodePage("/n/1111.2222.33");
		assertTrue(page.size()==3);
		List<String> ids = new ArrayList<String>();
		for(LocationNode n : page) {
			ids.add(n.id);
		}
		assertTrue(ids.contains("/n/1111.2222.33"));
		assertTrue(ids.contains("/n/1111.2222.44"));
		assertTrue(ids.contains("/n/1111.2222.55"));
		
		// middle of page
		page = io.getNodePage("/n/1111.2222.44");
		assertTrue(page.size()==3);
		ids = new ArrayList<String>();
		for(LocationNode n : page) {
			ids.add(n.id);
		}
		assertTrue(ids.contains("/n/1111.2222.33"));
		assertTrue(ids.contains("/n/1111.2222.44"));
		assertTrue(ids.contains("/n/1111.2222.55"));
		
		
		// bottom of file and page
		page = io.getNodePage("/n/2222.3333.77");
		assertTrue(page.size()==2);
		ids = new ArrayList<String>();
		for(LocationNode n : page) {
			ids.add(n.id);
		}
		
		assertTrue(ids.contains("/n/2222.3333.77"));
		assertTrue(ids.contains("/n/2222.3333.00"));
	}
	
	
}
