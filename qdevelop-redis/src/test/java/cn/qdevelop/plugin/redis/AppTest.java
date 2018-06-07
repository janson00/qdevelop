package cn.qdevelop.plugin.redis;

import java.util.regex.Pattern;

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
    	
    	Pattern isLikeWXNumber = Pattern.compile("[a-zA-Z]{3,}[0-9]{3,}");
    	String[] s = new String[]{
    		"a0","aa1","a11","Aa","aaa111"
    			
    	};
    	for(String z : s){
    		System.out.println(isLikeWXNumber.matcher(z).find());
    	}
//    	Test t = new Test();
        assertTrue( true );
    }
}
