/**   
 * @Title: DateUtil.java 
 * @Package com.youmei.common.basic.utils 
 * @Description: TODO
 * @date 2013年12月20日 下午2:19:40 
 * @version V1.0  
 * Copyright www.youmei.com 版权所有 
 */
package cn.qdevelop.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: DateUtil
 * @Description: TODO
 * @author squarezjz
 * @date 2013年12月20日 下午2:19:40
 */
public class DateUtil {
	
	public static String formatDate(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static Date parse(String strDate) throws ParseException {
		if(strDate == null){
			return null;
		}
		return parse(strDate, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date parse(String strDate, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(strDate);
	}

	public static String formatDate(Date date, String format)
			throws ParseException {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getNow(){
		try {
			return formatDate(new Date());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
