package cs32.maps;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import KDTree.Coordinates;

import cs32.maps.MapsEngine;

public class MapsEngineTest {
	
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";

	

	@Test
	public void latPointersTest() throws IOException {
		MapsEngine en = new MapsEngine (ways,nodes,index);
		assertTrue(en.latPointers.containsKey("4015"));
		assertTrue(en.latPointers.containsKey("4047"));
		assertTrue(!en.latPointers.containsKey("4000"));
	}

}
