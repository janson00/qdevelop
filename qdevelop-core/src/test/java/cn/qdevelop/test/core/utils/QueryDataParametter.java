package cn.qdevelop.test.core.utils;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import junit.framework.TestCase;

public class QueryDataParametter extends TestCase{

	public void testMain(){
		try {
			DatabaseFactory.getInstance().updateDatabase(new DBArgs("QueryDataParametterUpdate").put("uid", "6677"));
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
