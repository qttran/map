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

import cs32.maps.LatLong;
import cs32.maps.LocationNode;
import cs32.maps.MapsEngine;

public class MapsGUI extends JFrame {
	
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 900;
	private static final long serialVersionUID = 1L;
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
		this.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		
/*		//for testing purposes
		LinkedList<StreetNode> ll = new LinkedList<StreetNode>();
		ll.add(new StreetNode(new Point2D.Double(50,50), new Point2D.Double(80,40), "a"));
		ll.add(new StreetNode(new Point2D.Double(50,50), new Point2D.Double(30, 120), "b"));
		ll.add(new StreetNode(new Point2D.Double(80,40), new Point2D.Double(200,170), "c"));
		ll.add(new StreetNode(new Point2D.Double(80,40), new Point2D.Double(30, 120), "d"));
		ll.add(new StreetNode(new Point2D.Double(200,170), new Point2D.Double(30, 120), "e"));
		ll.add(new StreetNode(new Point2D.Double(50,50), new Point2D.Double(15, 0), "f"));
		*/
		
		Set<StreetNode> set =  en.getStreetsFromFile("/home/bsenturk/course/cs032/map/all_ways.txt");
		MapPanel mp = new MapPanel(en, set);
		
		//mp.setCurrentLocation(new Point2D.Double(15,0));
		//mp.setDestination(new Point2D.Double(200, 170));
		
		JPanel mpWrap = new JPanel(new FlowLayout());
		mpWrap.add(mp);
		//OptionPanel op = new OptionPanel(en, mp);
		this.add(mpWrap, BorderLayout.CENTER);
		//this.add(op, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
