package cs32.maps.gui;

import javax.swing.JPanel;

import cs32.maps.MapsEngine;


public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private MapsEngine _engine;
	
	public MapPanel(MapsEngine en) {
		_engine = en;
	}
	
	// engine.getSHortestPath(x, x2)
	
	
	public void highlightPath(/* some path */) { //called by option panel
		
	}

}
