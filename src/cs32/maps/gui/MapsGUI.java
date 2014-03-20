package cs32.maps.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cs32.maps.LocationNode;
import cs32.maps.MapsEngine;

public class MapsGUI extends JFrame {
	
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 900;
	private static final long serialVersionUID = 1L;
	private Point2D.Double _location;
	private Point2D.Double _destination;
	private Set<StreetNode> _path;
	private MapsEngine _engine;
	
	public MapsGUI(MapsEngine en) throws IOException {
/*		
		 MapPanel (extends JPanel)
			 can: drag, scroll, click, highlight paths
		 OptionPanel (extends JPanel)
			 can: enter latlongs, enter street names, hit "get directions" to highlight best path
			 // optionpanel KNOWS ABOUT MapPanel, tells it to highlight a path
			 
		"MapsEngine" contains all logic methods, give OptionPanel and MapPanel references to it
*/
		super();
		_engine = en;
		this.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		
		MapPanel mp = new MapPanel(en, this);
		
		
		JPanel mpWrap = new JPanel(new FlowLayout());
		mpWrap.add(mp);
		OptionPanel op = new OptionPanel(en, this, mp);
		this.add(mpWrap, BorderLayout.CENTER);
		this.add(op, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setCurrentLocation(Point2D.Double p){
		_location = p;
		//System.out.println("CL:" + _location);
	}
	
	public void setDestination(Point2D.Double p){
		_destination = p;
		System.out.println("D:" + _destination);
	}
	
	public Point2D.Double getCurrentLocation(){
		return _location;
	}
	
	public Point2D.Double getDestination(){
		return _destination;
	}
	
	public void setPath(){
		if (_location != null && _destination != null) {
			try {
				_path = _engine.getPathStreetNodes(_location, _destination);
			} catch (IOException e) {
				_path = null;
			}
		}
		else _path = null;
	}
	
	public Set<StreetNode> getPath(){
		return _path;
	}
}
