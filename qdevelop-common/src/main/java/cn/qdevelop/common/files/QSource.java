package cn.qdevelop.common.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 
 * 获取系统资源类
 * 
 * @author Janson.Gu
 * 
 */
public class QSource {
		private static QSource _QSource = new QSource();
	
		public static QSource getInstance() {
			return _QSource;
		}

	/**
	 * 获取资源路径，jar，和文件目录均可获取，jar包和文件目录均有的话，已目录为准
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	public static URL getResource(String resource) throws Exception {
		File tmp = new File(resource);
		if (tmp.exists())
			return tmp.toURI().toURL();
		boolean hasLeadingSlash = resource.startsWith("/");
		String stripped = hasLeadingSlash ? resource.substring(1) : resource;
		URL _url = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			_url = classLoader.getResource(resource);
			if (_url == null && hasLeadingSlash) {
				_url = classLoader.getResource(stripped);
			}
		}
		if (_url == null) {
			_url = ClassLoader.getSystemResource(resource);
		}
		if (_url == null && hasLeadingSlash) {
			_url = ClassLoader.getSystemResource(stripped);
		}
		if (_url == null) {
			throw new Exception(resource + " not found!");
		}
		return _url;
	}

	/**
	 * 
	 * TODO 获取文件列表
	 * 
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	public List<InputStream> getResources(String resource) {
		ArrayList<InputStream> fileList = new ArrayList<InputStream>();
		try {
			InputStream stream = null;
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
				stream = classLoader.getResourceAsStream(resource);
				if (stream != null)
					fileList.add(stream);
			}
			if (stream == null) {
				stream = ClassLoader.getSystemResourceAsStream(resource);
				if (stream != null)
					fileList.add(stream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}

	/**
	 * 获取资源，jar，和文件目录均可获取，jar包和文件目录均有的话，已目录为准
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	public InputStream getSourceAsStream(String resource) throws Exception {
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

	/**
	 * 
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	public static File getResourceAsFile(String resource) throws Exception {
		File tmp = new File(resource);
		if (tmp.exists())
			return tmp;
		URL url = getResource(resource);
		if (url == null)
			return null;
		return new File(url.toURI());
	}

//	public Properties loadProperties(String configName){
//		try {
//			InputStream idSvrConfig = getSourceAsStream(configName);
//			if(idSvrConfig!=null){
//				Properties prop = new Properties();
//				prop.load(idSvrConfig);
//				return prop;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		return null;
//	}
	
	/**
	 * 加载配置文件
	 * @param configName
	 * @return
	 */
	public Properties loadProperties(String configName,Class<?> callClass){
		try {
			InputStream res = getSourceAsStream(configName);
			if(res!=null){
				Properties prop = new Properties();
				prop.load(res);
				return prop;
			}
			String runPath = callClass.getProtectionDomain().getCodeSource().getLocation().getPath();
			System.out.println("load from : "+runPath);
			if(runPath.endsWith(".jar")){
				File jarPath = new File(runPath);
				Properties prop = new Properties();
				InputStream result = null;
				if (jarPath.exists() && jarPath.isFile()) {
					JarFile file = null;
					try {
						file = new JarFile(jarPath);
						Enumeration<JarEntry> entrys = file.entries();
						while (entrys.hasMoreElements()) {
							JarEntry jarEntry = entrys.nextElement();
							if (jarEntry.getName().endsWith(configName)) {
								System.out.println("load jar["+file.getName()+"] resource:\t" + jarEntry.getName());
								result = file.getInputStream(jarEntry);
								prop.load(result);
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
				return prop;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	private final static Pattern regProjectName = Pattern.compile("^.*\\/");
	private final static Pattern clearProjectName = Pattern.compile("\\/bin\\/?$|\\/WEB-INF.+?$|\\/lib\\/?$|\\/target.+?$");
	private static String projectPath = null;
	public static String getProjectName(){
		return regProjectName.matcher(getProjectPath().replace("/webapps/ROOT", "")).replaceAll("");
	}

	public static String getProjectPath(){
		if(projectPath == null ){
			try{ 
				projectPath = clearProjectName.matcher(toCommonPath(Thread.currentThread().getContextClassLoader().getResource("").getPath())).replaceAll("");
			}catch(Exception e){
				File directory = new File("");
				try {
					projectPath = clearProjectName.matcher(toCommonPath(directory.getCanonicalPath())).replaceAll("");
				} catch (IOException e1) {
				}
			} 
			/*兼容window路径问题*/
			if(projectPath.startsWith("/file:/")){
				projectPath = projectPath.substring(7);
			}
			System.out.println("root ==> "+projectPath);
		}
		return projectPath;
	}
	
	public static void setProjectPath(String path){
		projectPath = toCommonPath(path);
	}
	
	private static String toCommonPath(String path){
		String t = path.replaceAll("\\\\", "/");
		if(!t.startsWith("/")) return "/"+t;
		return t;
	}

	
//	public static void main(String[] args) {
//		Properties prop = QSource.getInstance().loadProperties("qdevelop-log.properties", Closeables.class);
//		if(prop!=null){
//			System.out.println(prop.toString());
//		}
//	}


	
	

}
