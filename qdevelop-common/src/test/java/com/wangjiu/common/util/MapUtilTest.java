/**
 * 
 */
package com.wangjiu.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.MapUtil;

/**
 * @author square
 *
 */
public class MapUtilTest {
	Date testDate;
	String abc;

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public static void main(String[] args) {
		MapUtilTest mut = new MapUtilTest();
		mut.setTestDate(new Date());
		mut.setAbc("dnadng");
		try {
			Map ma  = MapUtil.beanToMap(mut);
			ma.put("sysDate", new Date());
			System.out.println(ma);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
}
