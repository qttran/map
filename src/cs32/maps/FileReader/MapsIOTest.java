package cs32.maps.FileReader;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import cs32.maps.Way;

public class MapsIOTest {
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";
	
	
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
		
		System.out.println(way.toString());
	}
	
	@Test
	public void streetNameAggregation() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		Set<String> streetNames = io.getStreetNames();
		for(String s : streetNames) {
			System.out.println(s);
		}
		System.out.printf("%s street names in total.\n", streetNames.size());
	}
}
