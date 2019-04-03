package cn.qdevelop.core.db;

import java.sql.Connection;
import java.util.HashMap;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.standard.IDBQuery;

public class TestMysql {
	
	public static void t(){
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
			DatabaseFactory.closeConnection(conn);
		}
	}
	
	public static void main(String[] args) {
		//getLastOrders
//		String s = "update config_files set `config_file_path`='$[config_file_path]',`config_file_name`='$[config_file_name]',`config_file_desc`='$[config_file_desc]' where config_file_id=$[config_file_id]";
//		Pattern r = Pattern.compile("`?([0-9a-zA-Z]+\\.)?\\b(config_file_name)\\b`? ?= ?'?\\$\\[\\b(config_file_name)\\b\\]'?",Pattern.CASE_INSENSITIVE);
//		System.out.println(r.matcher(s).find());
		t();
//		DatabaseFactory.getInstance().queryDatabase(query)
	}
}
