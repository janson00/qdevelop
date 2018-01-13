package cn.qdevelop.plugin.id.client;

/**
 * @deprecated
 * @author janson
 *
 */
public class IDGenerate {
	
	private static IDGenerate _IDClient = new IDGenerate();
	public static IDGenerate getInstance() {
		return _IDClient;
	}
	
	public String getUserID() throws Exception {
		return IDClient.getInstance().getUserID();
	}
	
	public String getProductID() throws Exception {
		return IDClient.getInstance().getProductID();
	}
	
	public String getOrderID() throws Exception {
		return IDClient.getInstance().getOrderID();
	}
	
	public String getRandomID() throws Exception {
		return IDClient.getInstance().getRandomID();
	}
	
	public String getIDStr(final String name, final int digit, final int buffer) throws Exception{
		return IDClient.getInstance().getIDStr(name, digit, buffer);
	}
	

}
