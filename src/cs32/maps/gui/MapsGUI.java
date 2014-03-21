package cs32.maps.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import cs32.maps.MapsEngine;

public class MapsGUI extends JFrame {
	
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 900;
	private static final long serialVersionUID = 1L;
	private Point2D.Double _location;
	private Point2D.Double _destination;
	private Set<StreetNode> _path;
	private MapsEngine _engine;
	private boolean _isSearching = false;
	
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

	/*
	 * Getters and Setters for current location (starting node) and destination (end node).
	 */
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
	
	/*
	 * setPath uses MapEngine's getPathStreetNodes method to set the streets that are going to be
	 * highlighted as a part of a path.
	 */
	public void setPath(){
		if (_location != null && _destination != null) {
			_isSearching = true;
			try {
				_path = _engine.getPathStreetNodes(_location, _destination);
				_isSearching = false;
				
			} catch (IOException e) {
				_path = null;
			}
		}
		else _path = null;
	}
	
	public Set<StreetNode> getPath(){
		return _path;
	}
	
	public void setSearching(boolean b) {
		_isSearching = b;
	}
	
	public boolean getSearching() {
		return _isSearching;
	}
}
