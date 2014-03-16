package PathFinding;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import cs32.maps.LocationNode;
import cs32.maps.FileReader.MapsIO;

public class PathFinderTest {
	/**
	 * test PathFinder's helper method "getBaconReceivers(..)"
	 * @throws IOException
	 */
	@Test
	public void testGetBaconReceivers() throws IOException {
		MapsIO io = new MapsIO("smallWays.tsv", "smallNodes.tsv","smallIndex.tsv");
		PathFinder pf = new PathFinder(io);
		
		Set<LocationNode> rec = pf.dummyGetReceivers("id");
		for(LocationNode ln : rec){
			System.out.println(ln.toString());
		}
		
	}

}
