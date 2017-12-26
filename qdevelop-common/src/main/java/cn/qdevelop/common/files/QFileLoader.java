package cn.qdevelop.common.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import cn.qdevelop.common.utils.QLog;

public abstract class QFileLoader {
	protected static Logger log = QLog.getLogger(QFileLoader.class);
	
	
	/**
	 * 加载项目指定配置文件
	 * @param configName
	 */
	public void loadFile(String configName){
		this.loadFile(configName,this.getClass());
	}
	
	
	/**
	 * 加载项目指定配置文件
	 * @param configName
	 * @param callClass
	 */
	public void loadFile(String configName,Class<?> callClass){
		try {
			InputStream res = getSourceAsStream(configName);
			if(res!=null){
				despose(res);
				return;
			}
			String runPath = callClass.getProtectionDomain().getCodeSource().getLocation().getPath();
			log.info("load from : "+runPath);
			if(runPath.endsWith(".jar")){
				File jarPath = new File(runPath);
				if (jarPath.exists() && jarPath.isFile()) {
					JarFile file = null;
					try {
						file = new JarFile(jarPath);
						Enumeration<JarEntry> entrys = file.entries();
						while (entrys.hasMoreElements()) {
							JarEntry jarEntry = entrys.nextElement();
							if (jarEntry.getName().endsWith(configName)) {
								log.info("load jar resource:\t" + jarEntry.getName());
								despose(file.getInputStream(jarEntry));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						log.error(e);
					} finally {
						try {
							if (file != null)
								file.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} 
	}
	
	public abstract void despose(InputStream is);
	
	
	private InputStream getSourceAsStream(String resource) throws Exception {
		File tmp = new File(resource);
		if (tmp.exists())
			return new FileInputStream(tmp);
		boolean hasLeadingSlash = resource.startsWith("/");
		String stripped = hasLeadingSlash ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(resource);
			if (stream == null && hasLeadingSlash) {
				stream = classLoader.getResourceAsStream(stripped);
			}
		}
		if (stream == null) {
			stream = ClassLoader.getSystemResourceAsStream(resource);
		}
		if (stream == null && hasLeadingSlash) {
			stream = ClassLoader.getSystemResourceAsStream(stripped);
		}
		if (stream == null) {
			log.error("load properties : "+resource + " not found!");
		}
		return stream;
	}
}
