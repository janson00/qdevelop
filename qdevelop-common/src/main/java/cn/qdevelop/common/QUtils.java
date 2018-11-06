package cn.qdevelop.common;

import java.util.Properties;

import org.apache.log4j.Logger;

import cn.qdevelop.common.asynchronous.AsynExcutor;
import cn.qdevelop.common.files.QSource;

/**
 * 通用工具调用入口
 * @author janson
 *
 */
public class QUtils {

	/**
	 * 动态获取项目路径地址
	 * @return
	 */
	public static String getProjectPath(){
		return QSource.getProjectPath();
	}


	/**
	 * 获取日志输出
	 * @param claZZ
	 * @return
	 */
	public static Logger getLogger(Class<?> claZZ){
		return QLog.getLogger(claZZ);
	}


	/**
	 * 异步执行通用接口
	 * @param executor
	 */
	public static void asynExec(Runnable executor){
		AsynExcutor.getInstance().add(executor);
	}

	/**
	 * 直接从项目中获取配置文件数据
	 * @param configName
	 * @param callClass
	 * @return
	 */
	public static Properties loadProperties(String configName,Class<?> callClass){
		return QSource.getInstance().loadProperties(configName, callClass);
	}


}
