package PathFinding;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import cs32.maps.LocationNode;
import cs32.maps.FileReader.MapsFileReader;

public class PathFinderTest {
	/**
	 * test PathFinder's helper method "getBaconReceivers(..)"
	 * @throws IOException
	 */
	@Test
	public void testGetBaconReceivers() throws IOException {
		MapsFileReader bt = new MapsFileReader("smallWays.tsv", "smallNodes.tsv","smallIndex.tsv");
		PathFinder pf = new PathFinder(bt);
		
		Set<LocationNode> rec = pf.dummyGetReceivers("id");
		for(LocationNode ln : rec){
			System.out.println(ln.toString());
		}
		
	}

}
