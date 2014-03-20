package cs32.maps.gui;

public class PathRequestThread extends Thread{
	private MapsGUI _gui;
	public PathRequestThread(MapsGUI gui){
		_gui = gui;
	}
	
	public void run(){
		_gui.setPath();
		_gui.repaint();
	}
}
