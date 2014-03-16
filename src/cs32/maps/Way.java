package cs32.maps;

public class Way {
	public final String id;
	public final String startNodeID;
	public final String endNodeID;
	public final String name;
	
	public Way(String i, String sn, String en, String nm) {
		id = i;
		startNodeID = sn;
		endNodeID = en;
		name = nm;
	}
	
	@Override
	public String toString() {
		String s = "\""+id+"\" ["+startNodeID+" -> "+endNodeID+"]  Name: "+name;
		return s;
	}
}
