package KDTree.old;

public class Coordinates{
	public final double x;
	public final double y;

	public Coordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double compareTo(Coordinates o) {
		double sumThis = x*x+y*y;
		double	sum0 = o.x*o.x+o.y*o.y;
		return (sumThis - sum0);
	}
	
	public double distance(Coordinates o) {
		return ((x-o.x)*(x-o.x) + (y-o.y)*(y-o.y));
	}
}

