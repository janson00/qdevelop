package cn.qdevelop.test.app.id;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cn.qdevelop.app.id.server.IDRule;
import cn.qdevelop.app.id.server.impl.IDGenerate;
import cn.qdevelop.app.id.server.impl.RandomRule;

/**
 * Unit test for simple App.
 */
public class AppTest  extends TestCase
{

	public String testConfig = "TESTUNIT";

	public void setUp(){
		//		IDGenerate.getInstance().getID("test_unit", 8);
	}

	public void tearDown()  {
		//		IDGenerate.getInstance().changePropertyValue(testConfig, "0");
		//		IDGenerate.getInstance().changePropertyValue("test_unit", "0");
	}
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
	public void testIDGenerate()
	{
		IDGenerate.getInstance().changePropertyValue(testConfig, "0");
		long v = IDGenerate.getInstance().getID(testConfig, 8);
//		    	System.out.println(v);
		assertEquals(63398230, v);
		IDGenerate.getInstance().changePropertyValue(testConfig, "0");
		//    	long s = System.currentTimeMillis();
		for(int i=0;i<900;i++){
			long vv = IDGenerate.getInstance().getID(testConfig, 8);
			if(vv < 1){
				System.out.println("@@@@@"+vv);
			}
		}
		//    	System.out.println(">>>"+(System.currentTimeMillis()-s));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		IDGenerate.getInstance().watch();
	}

	private long get(String name){
		return IDGenerate.getInstance().getID(name, 8);
	}
	public void testReload(){
		String name = "myTests";
		IDGenerate.getInstance().changePropertyValue(name, "100");
		System.out.println("100>>"+get(name));
		PropertiesControl.resetValueByKey(name, "100");
		IDGenerate.getInstance().reloadProperties();
		for(int i=0;i<99;i++)get(name);
		long v  = get(name);
//		System.out.println(v);
				assertEquals(63398430, v);
//		PropertiesControl.resetValueByKey(name, "1000");
//		IDGenerate.getInstance().reloadProperties();
//		for(int i=0;i<100;i++)
//			System.out.println(get(name));
	}

	public void testRandomRule(){
//		IDRule rule = new RandomRule(6);
//		IDGenerate.getInstance().addRule("random", rule);
//		IDGenerate.getInstance().changePropertyValue("random", "999999");
//		IDGenerate.getInstance().getID("random",6);//first base number is 999999,next start with 0;
////		IDGenerate.getInstance().getID("random", 6)
//		assertEquals(628330, IDGenerate.getInstance().getID("random", 6));
	}
	
	public void testMultiRequest(){
		ExecutorService exec = Executors.newFixedThreadPool(100);
		final String [] config = new String[]{"aa","bb","ccc"};
		final Random r = new Random();
		
		for(int i=0;i<1000;i++){
			exec.execute(new Runnable(){
				public void run() {
					long v = IDGenerate.getInstance().getID(config[r.nextInt(3)], 8);
//					System.out.println(v);
//					if(v<1){
//						System.out.println("@@@@@@@@");
//					}
				}
			});
		}
		exec.shutdown();
		try {
			exec.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void testShutdown(){
		String testConfig = "mytest";
		IDGenerate.getInstance().changePropertyValue(testConfig, "0");
		long v1 = get(testConfig);
		System.out.println(v1);
		assertEquals(63398230, v1);
		IDGenerate.getInstance().shutdown();
		v1 = get(testConfig);
		System.out.println(v1);
		assertEquals(33982351, v1);
	}

}
