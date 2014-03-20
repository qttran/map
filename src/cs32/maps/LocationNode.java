package cs32.maps;

import java.awt.geom.Point2D;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class LocationNode {
	public final String id;
	public final List<String> ways;
	public final Point2D.Double latlong;
	public LocationNode(String uniqueid, List<String> wayslist, Point2D.Double ll) {
		id = uniqueid;
		ways = wayslist;
		latlong = ll;
		Preconditions.checkState(id.startsWith("/n/"));
		for(String s: wayslist) {
			Preconditions.checkState(s.startsWith("/w/"));
		}
	}

	@Override
	public String toString() {
		String s = "\""+id+"\" ["+latlong.x+", "+latlong.y+"]  Ways:  ";
		for(String w : ways) {
			s += w + ", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	@Override
	public boolean equals(Object o){  
		final LocationNode other = (LocationNode) o;
		return id.equals(other.id); //ids are unique 
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(id); //ids are unique 
	}
}
