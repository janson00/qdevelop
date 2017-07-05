package cn.qdevelop.core;

import java.sql.SQLException;
import java.util.List;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.standard.IUpdateHook;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws SQLException 
     */
    public void testApp() throws SQLException
    {
    	UpdateBean ub = new UpdateBean();
    	ub.setFullSql("xxxxxx");
    	try {
			List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks("products_log-add-action");
			if(updateHooks==null)return;
			System.out.println("aaa "+updateHooks.size());
			for(IUpdateHook uh : updateHooks){
				uh.execHook(null, ub, 0, 1);
			}
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
