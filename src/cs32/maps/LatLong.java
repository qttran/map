package cs32.maps;

import java.awt.geom.Point2D;

public class LatLong {
	public final double lat;
	public final double lon;
	
	public LatLong(double latitude, double longitude) {
		lat = latitude;
		lon = longitude;
	}
	
	public LatLong(String latString, String lonString) {
		lat = Double.parseDouble(latString);
		lon = Double.parseDouble(lonString);
	}
	
	public String toString() {
		return "("+lat+", "+lon+")";
	}
	
	public Point2D.Double getPt(){
		return new Point2D.Double(lat, lon);
	}
}
