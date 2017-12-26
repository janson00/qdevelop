package cn.qdevelop.core.utils;

import java.util.ArrayList;

import cn.qdevelop.common.files.QProperties;
import junit.framework.TestCase;

public class PropTest  extends TestCase{
	public void testGetProp(){
		System.out.println(QProperties.getInstance().getJsonValue("test", "0"));
		
		ArrayList t = new ArrayList(10);
		t.add("aaaa");
		for(Object s : t){
			System.out.println("===>"+s);
		}
	}
}
