package cn.qdevelop.common.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 项目内文件加载，先从本地加载资源文件，文件不存在时，从调用所在jar包内加载指定配置文件资源
 * @author janson
 *
 */
public abstract class QFileLoader {
//	protected static Logger log = QLog.getLogger(QFileLoader.class);
	
	
	/**
	 * 加载项目指定配置文件
	 * @param configName
	 */
	public void loadFile(String configName)  throws FileNotFoundException{
		this.loadFile(configName,super.getClass());
	}
	
	
	/**
	 * 加载项目指定配置文件
	 * @param configName
	 * @param callClass
	 */
	public void loadFile(String configName,Class<?> callClass) throws FileNotFoundException{
		try {
			InputStream res = getSourceAsStream(configName);
			if(res!=null){
				despose(res);
				return;
			}
			CodeSource pd =  callClass.getProtectionDomain().getCodeSource();
			if(pd==null){
				throw new FileNotFoundException(configName);
			}
			String runPath = pd.getLocation().getPath();
			System.out.println("load from : "+runPath);
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
								System.out.println("load jar resource:\t" + jarEntry.getName());
								despose(file.getInputStream(jarEntry));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (file != null)
								file.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				throw new FileNotFoundException(configName);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			System.err.println("load properties : "+resource + " not found!");
		}
		return stream;
	}
}
