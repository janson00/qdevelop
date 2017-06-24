package cn.qdevelop.words;

import java.util.regex.Pattern;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

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
	private final static Pattern isPY = Pattern.compile("^[A-Z]+?$") ;

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        try {
        	String py = PinyinHelper.getShortPinyin("0从战略层面而言").toUpperCase();
			System.out.println(py);
			System.out.println(isPY.matcher(py).find());
		} catch (PinyinException e) {
			e.printStackTrace();
		}
    }
}
