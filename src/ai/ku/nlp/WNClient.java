package ai.ku.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import adam.RPCBox;
import connections.Connections;
import connections.Connections.NetWireException;

public class WNClient {

	private static WNClient wnclient = null;
	private RPCBox proxy;
	
	// Singleton
	public static WNClient getInstance(){
		if( wnclient == null )
			wnclient = new WNClient();
		return wnclient;
	}

	public static void init(){
		WNClient.getInstance();
	}
	
	public WNClient() {
		try {
			// Create a local surrogate for the server using the id
			System.out.println("Connecting to WNServer...");
			proxy = (RPCBox) Connections.subscribe(ServerConfig.SERVER_ID);
			System.out.println("Connected to WNServer!");
		}
		catch (NetWireException e) {
			e.printStackTrace();
		}
	}
	
	public String getSynsetID(String searchWord, String tag) {
		Object[] arguments = new Object[] { searchWord, tag };		
		String response = (String)proxy.rpc("getSynsetID", arguments);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getSynsetIDs(String searchWord, String tag) {
		Object[] arguments = new Object[] { searchWord, tag };		
		ArrayList<String> response = (ArrayList<String>) proxy.rpc("getSynsetIDs", arguments);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<String> getAvailableSynsetIDs(String searchWord, String tag) {
		Object[] arguments = new Object[] { searchWord, tag };		
		HashSet<String> response = (HashSet<String>) proxy.rpc("getAvailableSynsetIDs", arguments);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<String> getCousins(List<String> ids, String option) {
		Object[] arguments = new Object[] { ids, option };		
		HashSet<String> response = (HashSet<String>) proxy.rpc("getCousins", arguments);
		return response;
	}
	
	public boolean hasNounPhrase(String phrase) {
		Object[] arguments = new Object[] { phrase };		
		boolean response = ((Boolean)proxy.rpc("hasNounPhrase", arguments)).booleanValue();
		return response;
	}
	
	public static void main(String[] args) {
		String string = WNClient.getInstance().getSynsetID("apple","NN");
		System.out.println(string);
	}
}
