package cs32.maps.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JPanel;
import cs32.maps.MapsEngine;
import cs32.maps.gui.StreetNode;

public class MapPanel extends JPanel {
	public static final int MAP_WIDTH = 780;
	public static final int MAP_HEIGHT = 780;
	private static final long serialVersionUID = 1L;
	
	private MapsEngine _engine;
	private Set<StreetNode> _nodes;
	private Double scale = 1000D;
	private Point2D.Double _center = new Point2D.Double(40D,-73D);
	private Point2D.Double _location;
	private Point2D.Double _destination;
	
	public MapPanel(MapsEngine en, Set<StreetNode> nodes) {
		_engine = en;
		_nodes = nodes;
		final MapPanel _map = this;
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
		this.addMouseMotionListener(new MouseMotionListener(){
			Point ip;
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				if (ip == null) ip = e.getPoint();
				_center.x += (-e.getPoint().x + ip.x)/scale;
				_center.y += (-e.getPoint().y + ip.y)/scale;
				ip = e.getPoint();
				_map.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				ip = null;
			}

		});
		
		this.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				scale-=e.getWheelRotation()*10;
				if (scale < 0.1D) scale = 0.1D;
				_map.repaint();
			}
			
		});
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		//get updated points
		Graphics2D g2D = (Graphics2D) g;
//		/g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke((int)(0.0006*scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (StreetNode node: _nodes){
			Point p1 = getCoordinates(node.startingPoint);
			Point p2 = getCoordinates(node.endPoint);
			g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
/*			System.out.println(node.startingPoint  + ", " + p1);
			System.out.println(node.endPoint  + ", " + p2);*/
		}
		
		Point loc = getCoordinates(_location);
		Point des = getCoordinates(_destination);
		if (loc != null) {
			g2D.setColor(Color.BLUE);
			g2D.draw(new Ellipse2D.Double(loc.x-4, loc.y-4, 8, 8));
		}
		if (des != null) {
			g2D.setColor(Color.ORANGE);
			g2D.draw(new Ellipse2D.Double(des.x-4, des.y-4, 8, 8));
		}
		
	}
	
	// engine.getSHortestPath(x, x2)
	
	
	public void highlightPath(/* some path */) { //called by option panel
		
	}

	private Point getCoordinates(Point2D.Double p){
		if (p == null) return null;
		return new Point((int) ((scale*(p.x - _center.x)) + (MAP_HEIGHT/2)), (int) ((scale*(p.y - _center.y)) + (MAP_HEIGHT/2)));
	}
	
	public void setCurrentLocation(Point2D.Double p){
		_location = p;
	}
	
	public void setDestination(Point2D.Double p){
		_destination = p;
	}
	
	public Point2D.Double getCurrentLocation(){
		return _location;
	}
	
	public Point2D.Double getDestination(){
		return _destination;
	}
}
