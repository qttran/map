package KDTree;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import cs32.maps.MapsEngine;

public class KDTreeTest {
	
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";

	

	@Test
	public void buildKDTree() throws IOException {
		MapsEngine en = new MapsEngine (ways,nodes,index);
		assertTrue(en.k.lookup(new Coordinates(40.1581762,-73.7485663)));
		assertTrue(en.k.lookup(new Coordinates(-1000,-100000)) == false);
	}
	
	@Test
	public void searchNumberTest() throws IOException {
		MapsEngine en = new MapsEngine (ways,nodes,index);
		assertTrue(en.k.lookup(new Coordinates(40.1581762,-73.7485663)));
		assertTrue(en.k.lookup(new Coordinates(-1000,-100000)) == false);
		List<String> empty = new LinkedList<>();
		empty.add("");
		assertTrue(en.k.searchNumber(0, new Coordinates(40.1581762,-73.7485663)).equals(empty));
		assertTrue(en.k.searchNumber(1, new Coordinates(40.1581762,-73.7485663)).get(0).equals("/n/4015.7374.527767659"));
		assertTrue(en.k.searchNumber(1, new Coordinates(40.3768688,-73.5255778)).get(0).equals("/n/4037.7352.527767969"));
		assertTrue(en.k.searchNumber(1, new Coordinates(40.3768,-73.5255)).get(0).equals("/n/4037.7352.527767969"));
		assertTrue(!en.k.searchNumber(1, new Coordinates(41.3768,-75.5255)).get(0).equals("/n/4037.7352.527767969"));
	}
}
