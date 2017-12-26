/**   
 * @Title: BaseConstants.java 
 * @Package com.square.common.constants 
 * @Description: TODO
 * @date 2013年11月6日 下午3:56:02 
 * @version V1.0  
 * Copyright www.youmei.com 版权所有 
 */
package cn.qdevelop.common.utils;

import cn.qdevelop.common.files.QProperties;

/**
 * @ClassName: BaseConstants
 * @Description: TODO
 * @author squarezjz
 * @date 2013年11月6日 下午3:56:02
 */
public class BaseConstants {
	public static final String SYSNAME = QProperties.getInstance().getProperty("SYSNAME");

}
