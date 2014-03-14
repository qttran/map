package KDTree;

public class Coordinates{
	double x;
	double y;
	double z;

	public Coordinates(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double compareTo(Coordinates o) {
		double sumThis = x*x+y*y+z*z;
		double	sum0 = o.x*o.x+o.y*o.y+o.z*o.z;
		return (sumThis - sum0);
	}
	
	public double distance(Coordinates o) {
		return ((x-o.x)*(x-o.x) + (y-o.y)*(y-o.y) + (z-o.z)*(z-o.z));
	}
}

