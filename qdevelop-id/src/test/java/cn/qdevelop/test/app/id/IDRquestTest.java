package cn.qdevelop.test.app.id;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.qdevelop.plugin.idgenerate.bean.IDRequestBean;
import cn.qdevelop.plugin.idgenerate.cores.GenerateCoreImpl;
import junit.framework.TestCase;

public class IDRquestTest extends TestCase{
	String key = "Janson";
	
	/**
	 * 测试循环生成4位数
	 */
	public void testRandom(){
		GenerateCoreImpl.getInstance().changePropertyValue(key, "9999");
		IDRequestBean req = new IDRequestBean(key,4);
		req.setRandom(true);
		GenerateCoreImpl.getInstance().getID(req);//get 9999 
		
		long v = GenerateCoreImpl.getInstance().getID(req);//next return to 0
		long s = GenerateCoreImpl.getInstance().encode(0, 4);
		assertEquals(s, v);
	}
	
	public void testDateRanke(){
		GenerateCoreImpl.getInstance().changePropertyValue(key, "9999");
		IDRequestBean req = new IDRequestBean(key,4);
		req.isDateRange = true;
		System.out.println(GenerateCoreImpl.getInstance().getID(req));
		long s = GenerateCoreImpl.getInstance().encode(10000, 4);
		
		//当天的数据达到最大时，自动递增
		assertEquals(getDateRankeVal(s),GenerateCoreImpl.getInstance().getID(req));
		
		
		GenerateCoreImpl.getInstance().clearQueue(key);
		GenerateCoreImpl.getInstance().changePropertyValue(key, "9999");
		GenerateCoreImpl.getInstance().setLastDay(key, "");
		
		
		long v = GenerateCoreImpl.getInstance().encode(0, 4);
		GenerateCoreImpl.getInstance().getID(req);
		long v1 = GenerateCoreImpl.getInstance().getID(req);
		
		System.out.println(v1+" " +getDateRankeVal(v)+" "+v );
		
		//非当前天数据，自动归集到0
//		assertEquals(getDateRankeVal(v),v1);
		assertEquals(new SimpleDateFormat("yyMMdd").format(new Date()),GenerateCoreImpl.getInstance().getLastCreateDate(key));
//		System.out.println(GenerateCoreImpl.getInstance().getID(req));
	}
	
	private long getDateRankeVal(long v){
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String day = sdf.format(new Date());
		return Long.parseLong(new StringBuilder().append(day).append(v).toString());
	}
	
	
	public void tearDown()  {
		//		IDGenerate.getInstance().changePropertyValue(testConfig, "0");
		//		IDGenerate.getInstance().changePropertyValue("test_unit", "0");
		GenerateCoreImpl.getInstance().shutdown();
	}
}
