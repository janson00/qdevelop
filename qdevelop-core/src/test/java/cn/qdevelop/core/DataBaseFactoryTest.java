package cn.qdevelop.core;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import junit.framework.TestCase;

public class DataBaseFactoryTest extends TestCase {
	
	public void testDriect(){
		Connection conn = null;
		try {
			 conn = ConnectFactory.getInstance("default").getConnection();
			 IDBResult result = new DBResultBean();
			 new DatabaseImpl().queryDB(conn, "show tables", result);
			 System.out.println(result.getSize());
			 System.out.println(result);
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
		ConnectFactory.close(conn);
	}
	
	public void testQuery(){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("index", "users-search-action");
		query.put("status", "1");
		query.put("usr.pid", "11");
		query.put("age", "2|3|4");
		query.put("mySelf", "2 > 0");
		query.put("user_name", ">='2012-01-01 00:00:00'&<='2012-01-01 23:59:59'");
		query.put("page_size", 10);
		try {
			
//			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
//			DatabaseFactory.getInstance().updateDatabase(query);
			
			Connection conn = DatabaseFactory.getInstance().getConnectByQuery(query);
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			System.out.println(dbQuery.getPreparedSql());
			String[] columns =  dbQuery.getPreparedColumns();
			for(int i=0;i<columns.length;i++){
				System.out.println(columns[i]+" = "+dbQuery.getPreparedValues()[i]);
			}
			IDBResult rb  = DatabaseFactory.getInstance().queryDatabase(query);
			for(int i=0;i<rb.getSize();i++){
				System.out.println(rb.getResult(i));
			}
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
		
	}
	
//	public void testUpdate(){
//		Map<String,Object> query = new HashMap<String,Object>();
//		query.put("index", "users-add-action");
//		query.put("uid", "1");
//		query.put("utime", System.currentTimeMillis());
////		query.put("reason", "1");
////		query.put("epba_id", "1");
//		try {
//			Connection conn = DatabaseFactory.getInstance().getConnectByQuery(query);
//			IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(query, conn);
//			for(UpdateBean ub : dbUpdate.getUpdateBeans()){
//				System.out.println(ub.getPreparedSql()+" => "+ub.getFullSql());
//			}
//		} catch (QDevelopException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public void testUpdate(){
//		Map<String,Object> query = new HashMap<String,Object>();
//		query.put("index", "mytest_insert");
//		query.put("source", "insert");
//		query.put("ep_user_id", "2");
//		query.put("amount", "10.00");
//		query.put("type", 1);
//		query.put("status", new Integer(0));
//		query.put("ctime", new Date());
//		query.put("ictime", "2016-11-25");
//		query.put("remark", "xxxxx");
//		query.put("record", "ha");
//		query.put("record_id", "10");
//		try {
//			DatabaseFactory.getInstance().updateDatabase(query);
//		} catch (QDevelopException e) {
//			e.printStackTrace();
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	public void testUpdatesss(){
//		Map<String,Object> query = new HashMap<String,Object>();
//		query.put("index", "mytest_insert");
//		query.put("source", "insert");
//		query.put("ep_user_id", "2");
//		query.put("amount", "10.00");
//		query.put("type", 1);
//		query.put("status", new Integer(0));
//		query.put("ctime", new Date());
//		query.put("ictime", "2016-11-25");
//		query.put("remark", "ca");
//		query.put("record", "ha");
//		query.put("record_id", "10");
//		
//		
//		Map<String,Object> query2 = new HashMap<String,Object>();
//		query2.put("index", "mytest_insert");
//		query2.put("source", "insert^update");
//		query2.put("ep_user_id", "2");
//		query2.put("amount", "20.00");
//		query2.put("type", 1);
//		query2.put("status", new Integer(0));
//		query2.put("ctime", new Date());
//		query2.put("ictime", "2016-11-25");
//		query2.put("remark", "jjj");
//		query2.put("record", "zxc");
//		query2.put("record_id", "10");
//		try {
//			DatabaseFactory.getInstance().updateDataBaseMulti(query,query2);
//		} catch (QDevelopException e) {
//			e.printStackTrace();
//		}
//	}
	
}
