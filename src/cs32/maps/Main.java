package cs32.maps;

import java.io.File;
import java.io.IOException;

import cs32.maps.gui.MapsGUI;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
		
		/* maps [--gui] ways.tsv nodes.tsv index.tsv */
		int len = args.length;
		boolean gui = false;
		String fpWays, fpNodes, fpIndex;
		
		if(len!=4 && len!=3){
			System.exit(0); //print something
		}
	
		if(len==4){
			if(!args[0].equals("--gui")) {
				System.exit(0); //print something
			}
			gui = true;
		}
		
		
		fpWays = gui ? args[1] : args[0];
		fpNodes = gui ? args[2] : args[1];
		fpIndex = gui ? args[3] : args[2];
		
		checkFilePath(fpWays);
		checkFilePath(fpNodes);
		checkFilePath(fpIndex);
		

		MapsEngine e = new MapsEngine(fpWays,fpNodes,fpIndex);
		
		//new MapsFileReader
		//MapsFileReader, read all latlongs
		//
		//new Tree2d(pointlist)
		//
		if(gui) {
		//  MapsFileReader, read all street names
		//  new autocomplete engine
			new MapsGUI(e);
		}
		else {
			new MapsCLI(e);
		}
	}

	private static void checkFilePath(String fp) {
		if(!fp.endsWith(".tsv")){
			System.out.printf("ERROR: %s is not a .tsv file\n", fp);
			System.exit(0);
		}
		File f = new File(fp);
		if(!f.exists()){
			System.out.printf("ERROR: File %s does not exist.\n", fp);
			System.exit(0);
		}
		if(!f.canRead()){
			System.out.printf("ERROR: File %s is not readable.\n", fp);
			System.exit(0);
		}
	}
}
