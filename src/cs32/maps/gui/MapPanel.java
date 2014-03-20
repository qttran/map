package cs32.maps.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.HashSet;
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
	private Set<StreetNode> _path;
	private MapsGUI _gui;
	private Double scale = 60000D;
	private Point2D.Double _center = new Point2D.Double(41.82163534608988, -71.38882713291805);
	private int _currentVariable = 1;
	private Point2D.Double topLeft;
	private Point2D.Double bottomRight;
	private UpdateMapThread _umthread;
	
	public MapPanel(MapsEngine en, MapsGUI gui) {
		_engine = en;
		_nodes = new HashSet<StreetNode>();
		_gui = gui;
		
		final MapPanel _map = this;
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
		this.addMouseMotionListener(new MouseMotionListener(){
			Point ip;
			@Override
			public void mouseDragged(MouseEvent e) {
				if (ip == null) ip = e.getPoint();
				_center.x += (e.getPoint().y - ip.y)/scale;
				_center.y -= (e.getPoint().x - ip.x)/scale;
				ip = e.getPoint();
				_map.repaint();
			}
			//System.out.println("CL:" + _location);
			@Override
			public void mouseMoved(MouseEvent e) {
				ip = null;
			}

		});
		
		this.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scale = Math.pow(scale, 1 - e.getWheelRotation()*0.02);
				if (scale < 35000D) scale = 35000D;
				_map.repaint();
			}
			
		});
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (_currentVariable == 1){
					_gui.setCurrentLocation(_engine.getNearestPoint(getCoordinates(e.getPoint().x, e.getPoint().y)));
					_currentVariable = 2;
					_map.repaint();
				}
				else {
					_gui.setDestination(_engine.getNearestPoint(getCoordinates(e.getPoint().x, e.getPoint().y)));
					_currentVariable = 1;
					_map.repaint();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		//get updated points_location
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(Color.BLACK);
		//g2D.setStroke(new BasicStroke((int)(0.00002*scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Rectangle2D.Double bb = new Rectangle2D.Double(_center.x - (MAP_WIDTH/2)/scale, _center.y - (MAP_HEIGHT/2)/scale, MAP_WIDTH/scale, MAP_HEIGHT/scale);

		bottomRight = getCoordinates(MAP_WIDTH, 0);
		topLeft = getCoordinates(0, MAP_HEIGHT);
		
		if(_umthread == null || _umthread.getState() == State.TERMINATED) {
			_umthread = new UpdateMapThread(this);
			_umthread.start();
		}
		
		for (StreetNode node: _nodes){
			if(bb.intersectsLine(node)){
				Point p1 = getPixelCoordinates(node.x1, node.y1);
				Point p2 = getPixelCoordinates(node.x2, node.y2);
				g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
		}

		if (_gui.getPath() != null) {
			g2D.setColor(Color.GREEN);
			g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			_path = _gui.getPath();
			
			for (StreetNode node: _path){
				if(bb.intersectsLine(node)){
					Point p1 = getPixelCoordinates(node.x1, node.y1);
					Point p2 = getPixelCoordinates(node.x2, node.y2);
					g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
		
		Point2D.Double location = _gui.getCurrentLocation();
		Point2D.Double destination = _gui.getDestination();

		if (location != null) {
			Point loc = getPixelCoordinates(location.x, location.y);
			g2D.setColor(Color.BLUE);
			g2D.fill(new Ellipse2D.Double(loc.x-4, loc.y-4, 8, 8));
		}
		if (destination != null) {
			Point des = getPixelCoordinates(destination.x, destination.y);
			g2D.setColor(Color.ORANGE);
			g2D.fill(new Ellipse2D.Double(des.x-4, des.y-4, 8, 8));
		}
	}

	private Point getPixelCoordinates(double x, double y){
		return new Point((int) ((scale*(y - _center.y)) + (MAP_WIDTH/2)), (int) -((scale*(x - _center.x)) - (MAP_HEIGHT/2)));
	}
	
	private Point2D.Double getCoordinates(int x, int y){
		return new Point2D.Double(_center.x + (-y + MAP_HEIGHT/2)/scale, _center.y + (x - MAP_WIDTH/2)/scale);
	}
	
	public void updateMap() {
		try {
			_nodes = _engine.getStreetNodes(topLeft, bottomRight);
		} catch (IOException e) {}
	}
}
