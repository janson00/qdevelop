package cn.qdevelop.core.db;

import java.sql.Connection;
import java.util.HashMap;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.standard.IDBQuery;

public class TestMysql {
	
	public static void main(String[] args) {
		//getLastOrders
		HashMap<String,Object> query = new HashMap<String,Object>();
		query.put("index", "users-remove-action");
		
		Connection conn = null;
		try {
			conn = DatabaseFactory.getInstance().getConnectByQuery(query);
			IDBQuery  db = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			System.out.println(db.getPreparedSql());
		} catch (QDevelopException e) {
			e.printStackTrace();
		}finally{
			DatabaseFactory.getInstance().closeConnection(conn);
		}
		
//		DatabaseFactory.getInstance().queryDatabase(query)
	}
}
