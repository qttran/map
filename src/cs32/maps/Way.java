package cs32.maps;

import com.google.common.base.Preconditions;

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
		Preconditions.checkState(id.startsWith("/w/"));
	}
	
	@Override
	public String toString() {
		String s = "\""+id+"\" ["+startNodeID+" -> "+endNodeID+"]  Name: "+name;
		return s;
	}
}
