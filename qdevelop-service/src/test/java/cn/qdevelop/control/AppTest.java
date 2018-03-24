package cn.qdevelop.control;

import org.apache.commons.lang.StringEscapeUtils;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
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
     */
    public void testApp()
    {
    	String sql = "where a=? and b=?";
    	Object[] args = new Object[]{"janson",100};
    	
    	System.out.format(sql.replaceAll("\\?", "%s"),args);
    	
    	System.out.println(StringEscapeUtils.escapeHtml("<script>alert(1)</script><img src=\"http://baidu.com\"/>"));
    	System.out.println(StringEscapeUtils.escapeSql("select * from /**da**/ asd"));
    	
    	try {
			DatabaseFactory.getInstance().queryDatabaseCount(null);
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
