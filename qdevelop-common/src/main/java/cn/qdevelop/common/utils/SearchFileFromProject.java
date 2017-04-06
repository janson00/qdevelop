package cn.qdevelop.common.utils;

import java.io.File;
import java.util.regex.Pattern;

public  abstract class SearchFileFromProject {
	private static Boolean isFindClassPath ;
	public void searchProjectFiles(String filter){
		Pattern search = Pattern.compile(filter.replace(".", "\\.").replace("*", ".+"));
		File root = new File(QSource.getProjectPath());
		if(isFindClassPath==null){
			isClassSupport(root);
		}
		if(isFindClassPath == null){
			System.out.println("项目路径寻找错误："+QSource.getProjectPath());
		}
		searchFiles(root, search);
		root = null;
	}

	private final void isClassSupport(File f){
		if(f.isDirectory()){
			File[] fs = f.listFiles();
			for(int i=0;i<fs.length;i++){
				if(fs[i].isDirectory()){
					if(fs[i].getName().equals("classes")){
						isFindClassPath = new Boolean(true);
						break;
					}else{
						isFindClassPath = new Boolean(false);
						isClassSupport(fs[i]);
					}
				}
			}
		}
	}

	public final void searchFiles(File f,Pattern search){
		if(f.isDirectory()){
			File[] fs ;
			fs = f.listFiles();
			for(File _f : fs){
				if(_f.isDirectory()){
					if(!isFindClassPath.booleanValue() || _f.getAbsolutePath().indexOf("classes") > -1){
						if(search.matcher(_f.getAbsolutePath()).find()){
							disposeFileDirectory(_f);
						}
					}
					searchFiles(_f,search);
				}else{
					if(!isFindClassPath.booleanValue() || _f.getAbsolutePath().indexOf("classes") > -1){
						//						System.out.println(_f.getAbsolutePath());
						if(search.matcher(_f.getAbsolutePath()).find()){
							disposeFile(_f);
						}
					}
				}
			}
		}else{
			disposeFile(f);
		}
	}

	/**
	 * 遍历文件后，处理文件
	 */
	protected abstract void disposeFile(File f);

	/**
	 * 遍历文件后，处理文件夹
	 */
	protected abstract void disposeFileDirectory(File f);

}
