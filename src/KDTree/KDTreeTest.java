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
	
	public static final String nodesTest = "/home/qttran/course/cs032/map/kdTreeTest/nodes.tsv";
	//public static final String waysTest = "/home/qttran/course/cs032/map/kdTreeTest/nodes.tsv";
	//public static final String indexTest = "~/course/cs032/map/kdTreeTest/index.tsv";
	

	@Test
	public void buildKDTree() throws IOException {
		//MapsEngine en = new MapsEngine (ways,nodes,index);
		//assertTrue(en.k.lookup(new Coordinates(40.1581762,-73.7485663)));
		//assertTrue(en.k.lookup(new Coordinates(-1000,-100000)) == false);
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
		

		Coordinates coor1 = new Coordinates(40.1581762,-73.7485663);
		assertTrue(en.k.searchNumberCoordinates(0, coor1).size() == 0);

		//Coordinates coor2 = new Coordinates(40.1581762,-73.7485663);
		//assertTrue(en.k.searchNumberCoordinates(1, coor2).get(0).x == 40.1581762);
		//assertTrue(en.k.searchNumberCoordinates(1, coor2).get(0).y == -73.7485663);
		
		Coordinates coor3 = new Coordinates(41.821135346089875,-71.38717713291805);
		System.out.println(en.k.searchClosest(coor3).x);
		System.out.println(en.k.searchClosest(coor3).y);

	}
	
	@Test
	public void searchTest() throws IOException {
	/*	MapsEngine en = new MapsEngine (ways,nodesTest,index);
		Coordinates coor1 = new Coordinates(0,0);
		assertTrue(en.k.searchNumberCoordinates(1, coor1).get(0).x == 0);
		assertTrue(en.k.searchNumberCoordinates(1, coor1).get(0).x == 0);
		
		Coordinates coor2 = new Coordinates(0.1,0.1);
		assertTrue(en.k.searchNumberCoordinates(1, coor2).get(0).x == 0);
		assertTrue(en.k.searchNumberCoordinates(1, coor2).get(0).x == 0);*/
		

	}
	
}
