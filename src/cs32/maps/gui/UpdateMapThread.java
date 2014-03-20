package cs32.maps.gui;

public class UpdateMapThread extends Thread{
	private MapPanel _panel;
	public UpdateMapThread(MapPanel panel){
		_panel = panel;
	}
	
	@Override
	public void run(){
		_panel.updateMap();
		_panel.repaint();
	}
}
