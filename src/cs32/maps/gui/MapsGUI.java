package cs32.maps.gui;

import javax.swing.JFrame;

import cs32.maps.MapsEngine;

public class MapsGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public MapsGUI(MapsEngine en) {
		
/*		
		 MapPanel (extends JPanel)
			 can: drag, scroll, click, highlight paths
		 OptionPanel (extends JPanel)
			 can: enter latlongs, enter street names, hit "get directions" to highlight best path
			 // optionpanel KNOWS ABOUT MapPanel, tells it to highlight a path
			 
		"MapsEngine" contains all logic methods, give OptionPanel and MapPanel references to it
*/
		MapPanel mp = new MapPanel(en);
		OptionPanel op = new OptionPanel(en, mp);
		
	}
}
