package cn.qdevelop.id.client;

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
//        assertTrue( true );
    	Pattern cleanPrefix = Pattern.compile("^.*(/|\\\\)");
    	String ss = "/asd/fsf23/2123/dasdw/.xml";
    	String tt = "\\targe\\dsqw\\saas\\dasdw\\.xml";
//    	if(){
//    		
//    	}
    	System.out.println(cleanPrefix.matcher(ss).replaceAll("") +" "+ cleanPrefix.matcher(tt).replaceAll(""));
    	
    }
}
