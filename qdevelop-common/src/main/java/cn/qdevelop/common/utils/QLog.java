/**
 * 
 */
package cn.qdevelop.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Janson
 * 
 */
public class QLog {
	public static Pattern isArgs = Pattern.compile("(\\{)SYSNAME(\\})");
	private static String logConfig = "qdevelop-log.properties$";
	private static AtomicBoolean isInit = new AtomicBoolean(false);

	public static Logger getLogger(Class<?> claZZ){
		if(!isInit.get()){
			init();
		}
		return Logger.getLogger(claZZ);
	}


	public static void init(){
		final Properties props = new Properties();
		new SearchFileFromJars(){
			@Override
			public void desposeFile(String jarName,String fileName, InputStream is) {
				System.out.println(DateUtil.getNow()+" =====> log:"+fileName);
				try {
					props.load(is);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.searchAllJarsFiles(logConfig);
		final int projectIdx = QSource.getProjectPath().length();
		new SearchFileFromProject(){
			@Override
			protected void disposeFile(File f) {
				System.out.println(DateUtil.getNow()+" =====> log:"+f.getAbsolutePath().substring(projectIdx));
				try {
					props.load(new FileInputStream(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void disposeFileDirectory(File f) {
			}
		}.searchProjectFiles(logConfig);
		
		new SearchFileFromProject(){
			@Override
			protected void disposeFile(File f) {
				System.out.println(DateUtil.getNow()+" =====> log:"+f.getAbsolutePath().substring(projectIdx));
				try {
					props.load(new FileInputStream(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void disposeFileDirectory(File f) {
			}
		}.searchProjectFiles("log4j.properties");

		String projectName = QSource.getProjectName();
		Iterator<?> itor = props.keySet().iterator();
		while(itor.hasNext()){
			String key = (String)itor.next();
			String value = props.getProperty(key);
			if(isArgs.matcher(value).find()){
				props.setProperty(key, isArgs.matcher(value).replaceAll(projectName));
			}
		}

		/**如果是开发环境，尽量输出相信的日志信息在控制台，否则就直接在项目固定目录中输出**/
		if(isDevelopEnv()){
			if(props.getProperty("log4j.logger.cn.qdevelop.core")!=null){
				props.setProperty("log4j.logger.cn.qdevelop.core", props.getProperty("log4j.logger.cn.qdevelop.core")+",console");
			}
			if(props.getProperty("log4j.logger.cn.qdevelop.service")!=null){
				props.setProperty("log4j.logger.cn.qdevelop.service", props.getProperty("log4j.logger.cn.qdevelop.service")+",console");
			}
			if(props.getProperty("log4j.appender.sqlExcute.file")!=null){
				System.out.println("[sql log] ===> "+props.getProperty("log4j.appender.sqlExcute.file"));
			}
		}
		PropertyConfigurator.configure(props);
		isInit.set(true);
	}
	
	

	public static boolean isDevelopEnv(){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("solaris") > -1 || os.indexOf("centos") > -1 || os.indexOf("sunos") > -1 || os.indexOf("freebsd") > -1 || os.indexOf("Linux") > -1){
			return false;
		}
		return true;
	}
}
