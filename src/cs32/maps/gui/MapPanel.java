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
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JPanel;

import cs32.maps.LocationNode;
import cs32.maps.MapsEngine;


public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private MapsEngine _engine;
	private LinkedList<StreetNode> _nodes;
	private Double scale = 1D;
	private Point2D.Double _center = new Point2D.Double(100D,100D);
	
	public MapPanel(MapsEngine en, LinkedList<StreetNode> nodes) {
		_engine = en;
		_nodes = nodes;
		final MapPanel _map = this;
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(500, 500));
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
				scale-=e.getWheelRotation()*0.3;
				if (scale < 0.1D) scale = 0.1D;
				_map.repaint();
			}
			
		});
		
		this.setFocusable(true);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(Color.RED);
		g2D.setStroke(new BasicStroke((int)(6*scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (StreetNode node: _nodes){
			g2D.drawLine((int) ((scale*(node.startingPoint.x - _center.x)) + 250), (int) ((scale*(node.startingPoint.y - _center.y)) + 250), (int) ((scale*(node.endPoint.x - _center.x)) + 250), (int) ((scale*(node.endPoint.y - _center.y)) + 250));
		}
			
	}
	
	// engine.getSHortestPath(x, x2)
	
	
	public void highlightPath(/* some path */) { //called by option panel
		
	}

}
