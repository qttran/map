package cs32.maps;

import java.awt.geom.Point2D;

import com.google.common.base.Objects;

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
	
	@Override
	public String toString() {
		return "("+lat+", "+lon+")";
	}
	
	public Point2D.Double getPt(){
		return new Point2D.Double(lat, lon);
	}
	
	
	@Override
	public boolean equals(Object o){  
		final LatLong other = (LatLong) o;
		return this.lat == other.lat && this.lon == other.lon;  
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(lat, lon);
	}
}
