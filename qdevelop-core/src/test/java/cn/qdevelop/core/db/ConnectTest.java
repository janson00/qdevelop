package cn.qdevelop.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.db.connect.ConnectFactory;
import junit.framework.TestCase;

public class ConnectTest extends TestCase{
	public void testConnect(){
		try {
			String s = ConnectFactory.getInstance("ep_user").getDataBase();
			Connection conn = ConnectFactory.getInstance("ep_user").getConnection();
			System.out.println(">>"+conn.getMetaData().getIdentifierQuoteString());
			System.out.println("<<"+conn.getCatalog());
//			ConnectFactory.initAllConnect();
			System.out.println(s);
			ConnectFactory.watchConnect();
			ConnectFactory.clear();
		} catch (QDevelopException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testDatabaseFactoryConnect(){
		try {
			Connection con = DatabaseFactory.getInstance().getConnectByQuery("mytest_insert");
			System.out.println(con.toString());
			Map<String,Object> query = new HashMap();
			query.put("index", "mytest_insert");
			con = DatabaseFactory.getInstance().getConnectByQuery(query);
			System.out.println(con.toString());
			HashMap<String,Object> query2 = new HashMap();
			query2.put("index", "mytest_insert");
			con = DatabaseFactory.getInstance().getConnectByQuery(query2);
			System.out.println(con.toString());
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
