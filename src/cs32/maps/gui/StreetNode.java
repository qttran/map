package cs32.maps.gui;

import java.awt.geom.Line2D;

public class StreetNode extends Line2D.Double{

	private static final long serialVersionUID = 1L;
	
	public final String name;
	
	public StreetNode(double x1, double y1, double x2, double y2, String name){
		super(x1,y1,x2,y2);
		this.name = name;

	}
	
	
	public String toString() {
		return "("+x1+","+y1+") -> ("+x2+","+y2+")";
	}
}
