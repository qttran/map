package PathFinding;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import cs32.maps.LocationNode;
import cs32.maps.FileReader.MapsIO;

public class PathFinderTest {
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";

	
	@Test
	public void test() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		PathFinder pf = new PathFinder(io);
		LocationNode s = io.getLocationNode("/n/4016.7374.527767846");
		LocationNode e = io.getLocationNode("/n/4016.7374.527767845");
		List<String> path = pf.getPath(s, e);
		//System.out.println(path);
		
		assertTrue(path.size()==1);
		assertTrue(path.get(0).endsWith("/w/4016.7374.42295268.1.2"));
	}
	
	
	@Test
	public void randoTest() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		PathFinder pf = new PathFinder(io);
		LocationNode s = io.getLocationNode("/n/4017.7374.527767851");
		LocationNode e = io.getLocationNode("/n/4020.7373.527767853");
		List<String> path = pf.getPath(s, e);
		System.out.println("Path: ");
		for(String str : path) {
			System.out.println(str);
		}
		System.out.println("--");
		
	}
	
	

	@Test
	public void randoTest2() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		PathFinder pf = new PathFinder(io);
		LocationNode s = io.getLocationNode("/n/4017.7374.527767851");
		LocationNode e = io.getLocationNode("/n/4140.7149.201383732");
		List<String> path = pf.getPath(s, e);
		System.out.println("Path: ");
		for(String str : path) {
			System.out.println(str);
		}
		System.out.println("--");
		
	}
}
