package cs32.maps.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cs32.maps.LatLong;
import cs32.maps.LocationNode;
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
		super();
		this.setPreferredSize(new Dimension(800, 800));
		this.setLocationRelativeTo(null);
		LinkedList<StreetNode> ll = new LinkedList<StreetNode>();
		ll.add(new StreetNode(new Point2D.Double(50,50), new Point2D.Double(80,40), "a"));
		ll.add(new StreetNode(new Point2D.Double(50,50), new Point2D.Double(30, 120), "b"));
		//ll.add(new StreetNode(new Point2D.Double(100,100)));
		MapPanel mp = new MapPanel(en, ll);
		JPanel mpWrap = new JPanel(new FlowLayout());
		mpWrap.add(mp);
		OptionPanel op = new OptionPanel(en, mp);
		this.add(mpWrap, BorderLayout.CENTER);
		this.add(op, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
