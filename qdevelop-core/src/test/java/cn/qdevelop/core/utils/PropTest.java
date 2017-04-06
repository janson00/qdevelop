package cn.qdevelop.core.utils;

import cn.qdevelop.common.utils.QProperties;
import junit.framework.TestCase;

public class PropTest  extends TestCase{
	public void testGetProp(){
		System.out.println(QProperties.getInstance().getJsonValue("test", "0"));
	}
}
