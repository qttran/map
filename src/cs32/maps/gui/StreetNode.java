package cs32.maps.gui;

import java.awt.geom.Point2D;

public class StreetNode {
	public final Point2D.Double startingPoint;
	public final Point2D.Double endPoint;
	private Boolean isHighlighted = false;
	public final String name;
	
	public StreetNode(Point2D.Double startingPoint, Point2D.Double endPoint, String name){
		this.startingPoint = startingPoint;
		this.endPoint = endPoint;
		this.name = name;
	}
	
	public Boolean isHighligted(){
		return isHighlighted;
	}
	
	public void setHiglighted(Boolean b){
		isHighlighted = b;
	}
}
