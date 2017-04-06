package cn.qdevelop.core.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.formatter.impl.CheckUpdateHook;

public class TestUpdateHook {

	public static void main(String[] args) {
		CheckUpdateHook cuh = new CheckUpdateHook();
		cuh.argName="test";
		cuh.sql = "select 1 from products where product_name=?";
		cuh.isExsit=true;
		try {
			Connection conn = ConnectFactory.getInstance("qd_product_center_write").getConnection();
			Map<String,Object> query = new HashMap<String,Object>();
			query.put("test", "test_38");
			cuh.init(conn, query);
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
	}

}
