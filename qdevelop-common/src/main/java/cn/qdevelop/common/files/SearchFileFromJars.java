package cn.qdevelop.common.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public abstract class SearchFileFromJars {
	public static List<File> libs;
	File _f;
	public void searchAllJarsFiles(String filter){
		if(libs == null){
			HashSet<String> lists = getEnvironmentJars();
			libs = new ArrayList<File>(lists.size());
			for(String s : lists){
				File f = new File(s);
				if(f.isFile()){
					libs.add(f);
				}
			}
		}
		Pattern search = Pattern.compile(filter.replaceAll("\\*", ".+"));
		for(File _f:libs){
			JarFile file=null;
			try {
				file = new JarFile(_f);
				Enumeration<JarEntry> entrys = file.entries();
				while(entrys.hasMoreElements()){
					JarEntry jar = entrys.nextElement();
					if(search.matcher(jar.getName()).find()){
						desposeFile(clear.matcher(file.getName()).replaceAll(""),jar.getName(),file.getInputStream(jar));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(file!=null)file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private HashSet<String> getEnvironmentJars(){
		HashSet<String> all = new HashSet<String>();
		try {
			File root = new File(QSource.getProjectPath());
			File[] fs = root.listFiles(new FileFilter(".jar"));
			if(fs!=null){
				for(int i=0;i<fs.length;i++){
					loopSearchJar(fs[i],all);
				}
			}
			String classPath = System.getProperty("java.class.path");
			String[] jars;
			if(isWindows()){
				jars = classPath.split(";");
			}else{
				jars = classPath.split(":");
			}
			for(int i=0;i<jars.length;i++){
				if(jars[i].endsWith(".jar")){
					all.add(clean.matcher(new File(jars[i]).getAbsolutePath()).replaceAll(""));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//		System.out.println("load environmentJars >>>>>>>>>>>>>>>>");
		return all;
	}

	private static Boolean isWindow  ;
	private static boolean isWindows(){
		if(isWindow!=null)return isWindow.booleanValue();
		if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
			isWindow = new Boolean(true);
		}else{
			isWindow = new Boolean(false);
		}
		return isWindow.booleanValue();
	}

	private static Pattern clean = Pattern.compile("\\.\\/");
	private static Pattern clear = Pattern.compile("^.*\\/");
	/**
	 *  递归寻找jar
	 * @param ff
	 * @param collect
	 */
	private void loopSearchJar(File ff,HashSet<String> collect){
		if(ff.isDirectory()){
			File[] fs = ff.listFiles(new FileFilter(".jar"));
			if(fs!=null){
				for(int i=0;i<fs.length;i++){
					loopSearchJar(fs[i],collect);
				}
			}
		}else{
			collect.add(clean.matcher(ff.getAbsolutePath()).replaceAll(""));
		}
	}

	public abstract void desposeFile(final String jarName,final String fileName,final InputStream is);

}
