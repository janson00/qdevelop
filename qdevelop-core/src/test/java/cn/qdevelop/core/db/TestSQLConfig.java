package cn.qdevelop.core.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.standard.IDBQuery;
import junit.framework.TestCase;

public class TestSQLConfig  extends TestCase{
	
	public void testLeftQueryAutoSearch(){
		Map<String,String> query = new HashMap<String,String>();
		query.put("index", "products_left_test");
		query.put("a.uid", "7358|7343|7387");
		query.put("b.action", "insert");
		query.put("b.ctime", ">2010-10-11&<2010-11-11");
		try {
			Connection conn = DatabaseFactory.getInstance().getConnectByQuery(query);
			IDBQuery qb = SQLConfigParser.getInstance().getDBQueryBean(query,conn);
			System.out.println(qb.getPreparedSql());
			for(int i=0;i<qb.getPreparedColumns().length;i++){
				System.out.println("param\t "+qb.getPreparedColumns()[i]+" : "+qb.getPreparedValues()[i]);
			}
			DatabaseFactory.getInstance().closeConnection(conn);
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
