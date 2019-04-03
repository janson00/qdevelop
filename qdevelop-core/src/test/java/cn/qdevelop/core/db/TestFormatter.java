package cn.qdevelop.core.db;

import cn.qdevelop.core.formatter.impl.KeyValueFormatter;
import junit.framework.TestCase;

public class TestFormatter  extends TestCase{
	
	public void testFormatter(){
		KeyValueFormatter irf = new KeyValueFormatter();
//		irf.init();
		KeyValueFormatter i1 = (KeyValueFormatter)irf.clone();
		i1.init();
		KeyValueFormatter i2 = (KeyValueFormatter)irf.clone();
		i2.init();
		System.out.println(i1.conditions == i2.conditions);
		
	}

}
