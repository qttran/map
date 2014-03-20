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
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4015.7374"));
		assertTrue(en.fileReader.nodeLatLongPointers.containsKey("4209.7169"));
		assertTrue(!en.fileReader.nodeLatLongPointers.containsKey("4209.7190"));
		assertTrue(en.fileReader.nodeLatPointers.containsKey("4209"));
		assertTrue(en.fileReader.nodeLatPointers.containsKey("4015"));
		assertTrue(en.fileReader.nodeLatLongPointers.get("4015.7374").get(0) == 64);
	}
}
