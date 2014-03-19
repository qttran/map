package cs32.maps;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs32.maps.FileReader.MapsIO;
import cs32.maps.gui.StreetNode;

public class GUIBackend {
	/*** gui backend: displaying map ***/
	//GetStreetNodes(latlong top left, latlong bottom right)
	private MapsIO _io;
	public GUIBackend(MapsIO io) {
		_io = io;
	//maintain a set of  streetNodesOnScreen
	}
	
	
	public Set<StreetNode> getAllStreetNodes() throws IOException{
		List<Way> ws = _io.getAllWays();
		Set<StreetNode> hs = new HashSet<>();
		for(Way w : ws) {
			LocationNode start = _io.getLocationNode(w.startNodeID);
			LocationNode end = _io.getLocationNode(w.endNodeID);
			hs.add(new StreetNode(new Point2D.Double(start.latlong.lat, start.latlong.lon), 
					new Point2D.Double(end.latlong.lat, end.latlong.lon), w.name));
		}
		return hs;
	}
}
