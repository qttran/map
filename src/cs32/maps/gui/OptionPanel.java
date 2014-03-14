package cs32.maps.gui;

import javax.swing.JPanel;

import cs32.maps.MapsEngine;

public class OptionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private MapsEngine _engine;
	private MapPanel _map;
	
	public OptionPanel(MapsEngine en, MapPanel mp) { //also needs to know about Autocomplete engine
		_engine = en;
		_map = mp;
		
		// fields to enter latlongs, streetnames
	}
}
