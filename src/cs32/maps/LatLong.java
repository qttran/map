package cs32.maps;

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
}
