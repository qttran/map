package cs32.maps;

import java.util.List;

public class LocationNode {
	public final String id;
	public final List<String> ways;
	public final LatLong latlong;
	public LocationNode(String uniqueid, List<String> wayslist, LatLong ll) {
		id = uniqueid;
		ways = wayslist;
		latlong = ll;
	}

	@Override
	public String toString() {
		String s = "\""+id+"\" ["+latlong.lat+", "+latlong.lon+"]  Ways:  ";
		for(String w : ways) {
			s += w + ", ";
		}
		return s.substring(0, s.length()-2);
	}
}
