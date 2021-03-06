package cn.qdevelop.core;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.files.QProperties;
import cn.qdevelop.core.db.config.SQLConfigLoader;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.formatter.FormatterLoader;

public class QDevelopUtils {
	
	/**
	 * 初始化所有的资源文件
	 */
	public static void initAll(){
		QLog.init();
		QProperties.getInstance();
		SQLConfigLoader.getInstance();
		ConnectFactory.initAllConnect();
		FormatterLoader.getInstance();
	}
	
	public static void destoryAll(){
		ConnectFactory.shutdown();
	}
	
	/**
	 * 系统热加载
	 */
	public static void hotReload(){
		QProperties.getInstance().init();
		SQLConfigLoader.getInstance().hotLoadConfig();
	}
}
