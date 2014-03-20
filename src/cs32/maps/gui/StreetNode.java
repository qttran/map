package cs32.maps.gui;

import java.awt.geom.Line2D;

import com.google.common.base.Objects;

import cs32.maps.LocationNode;

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
	

	@Override
	public int hashCode(){
		return Objects.hashCode(x1,y1,x2,y2); //ids are unique 
	}
	
	@Override
	public boolean equals(Object o){  
		final StreetNode other = (StreetNode) o;
		return ((x1 == other.x1) && (x2 == other.x2) &(y1 == other.y1) && (y2 == other.y2)); 
		//ids are unique 
	}
}
