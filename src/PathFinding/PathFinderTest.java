package PathFinding;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import cs32.maps.LocationNode;
import cs32.maps.FileReader.MapsIO;

public class PathFinderTest {
	public static final String ways = "/course/cs032/data/maps/ways.tsv";
	public static final String nodes = "/course/cs032/data/maps/nodes.tsv";
	public static final String index = "/course/cs032/data/maps/index.tsv";


	public static final String smallways = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallWays.tsv";
	public static final String smallnodes = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallNodes.tsv";
	public static final String smallindex = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallIndex.tsv";
	//TODO io gets out of bounds error when there is a node with no ways (idk if this ever happens in the real files)

	public static final String smallways2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallWays2.tsv";
	public static final String smallnodes2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallNodes2.tsv";
	public static final String smallindex2 = "/gpfs/main/home/mcashton/course/cs032/map/testfiles/smallIndex2.tsv";
		
	@Test
	public void test() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		PathFinder pf = new PathFinder(io);
		LocationNode s = io.getLocationNode("/n/4016.7374.527767846");
		LocationNode e = io.getLocationNode("/n/4016.7374.527767845");
		List<String> path = pf.getPathIds(s, e);
		
		assertTrue(path.size()==1);
		assertTrue(path.get(0).endsWith("/w/4016.7374.42295268.1.2"));
	}
	
	
	@Test
	public void randoTest() throws IOException {
		MapsIO io = new MapsIO(ways, nodes, index);
		PathFinder pf = new PathFinder(io);
		LocationNode s = io.getLocationNode("/n/4017.7374.527767851");
		LocationNode e = io.getLocationNode("/n/4020.7373.527767853");
		List<String> path = pf.getPathIds(s, e);
		System.out.println("Path: ");
		for(String str : path) {
			System.out.println(str);
		}
		System.out.println("--");
		
	}
	
	@Test
	public void smallTest() throws IOException {

		MapsIO io = new MapsIO(smallways, smallnodes, smallindex); // small files with 4 nodes and 3 ways connecting them
		PathFinder pf = new PathFinder(io);
		
		LocationNode s = io.getLocationNode("/n/1111.2222.333333333");
		LocationNode e = io.getLocationNode("/n/5555.6666.333333333");

		List<String> path = pf.getPathIds(s, e);
		assertTrue(path.size()==3);
		assertTrue(path.get(0).endsWith("/w/7777.7777.77777777.7.7"));
		assertTrue(path.get(1).endsWith("/w/8888.8888.88888888.8.8"));
		assertTrue(path.get(2).endsWith("/w/9999.9999.99999999.9.9"));
	}
	
	
	@Test
	public void smallTestsUsingID() throws IOException {

		MapsIO io = new MapsIO(smallways2, smallnodes2, smallindex2); // small files with 4 nodes and 3 ways connecting them
		PathFinder pf = new PathFinder(io);
		String two = "/n/1111.2222.33";
		String three = "/n/1111.2222.44";
		String four = "/n/1111.2222.55";
		//String five = "/n/2222.3333.00";
	 	String six = "/n/2222.3333.77";
		LocationNode s = io.getLocationNode(two); 
		LocationNode e = io.getLocationNode(six);

		List<String> path = pf.getPathIds(s, e);
		assertTrue(path.size()==2);
		assertTrue(path.get(0).endsWith("/w/22"));
		assertTrue(path.get(1).endsWith("/w/66"));

		
		e = io.getLocationNode(three);
		path = pf.getPathIds(s, e);
		assertTrue(path.size()==1);
		assertTrue(path.get(0).endsWith("/w/11"));
		
		e = io.getLocationNode(four);
		path = pf.getPathIds(s, e);
		assertTrue(path.size()==1);
		assertTrue(path.get(0).endsWith("/w/22"));
		
		s = io.getLocationNode(six);
		e = io.getLocationNode(two);
		path = pf.getPathIds(s, e);
		assertTrue(path.size()==3);
		assertTrue(path.get(0).endsWith("/w/99"));
		assertTrue(path.get(1).endsWith("/w/44"));
		assertTrue(path.get(2).endsWith("/w/33"));
	}

}
