import java.sql.Connection;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.bean.DBQueryBean;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;

public class MyTest {

	public static void main(String[] args) {
		Connection conn = null; 
		try {
			conn = ConnectFactory.getInstance("connIndexName").getConnection();
			IDBQuery dbQuery = new DBQueryBean();
			String sql = "";
			dbQuery.setPreparedSql(sql);
			dbQuery.setSql(sql);
			IDBResult result = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, result);
			
			
			
		} catch (QDevelopException e) {
			e.printStackTrace();
		}finally{
			ConnectFactory.close(conn);
		}
	}

}
