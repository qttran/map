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
	public void buildKDTreeAndMethods() throws IOException {
		MapsEngine en = new MapsEngine (ways,nodes,index);
		assertTrue(en.k.lookup(new Coordinates(40.1581762,-73.7485663)));
		assertTrue(en.k.lookup(new Coordinates(-1000,-100000)) == false);
		List<String> empty = new LinkedList<>();
		empty.add("");
		assertTrue(en.k.searchNumber(0, new Coordinates(40.1581762,-73.7485663)).equals(empty));
	}
}
