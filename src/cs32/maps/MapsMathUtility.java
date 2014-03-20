package cs32.maps;


public class MapsMathUtility {

	//in km
	public static double distance(LatLong start, LatLong end) {
		double R = 6371;
		double dLon = degreeToRad(start.lon - end.lon);
		double dLat = degreeToRad(start.lat - end.lat);
		double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
				Math.cos(degreeToRad(start.lat)) *
				Math.cos(degreeToRad(end.lat)) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		a = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R*a;
	}
	
	private static double degreeToRad(double degree) {
		return (degree * Math.PI / 180.0);
	}
}
