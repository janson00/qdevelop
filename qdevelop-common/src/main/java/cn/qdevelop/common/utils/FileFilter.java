package cn.qdevelop.common.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter{
	String[] fileRule;
	boolean isContain = true;
	public FileFilter(String s){
		if(s.indexOf("!")>-1)
			isContain = false;
		fileRule = s.replaceAll("\\*|!", "").split("\\|");
	}

	public boolean accept(File dir,String name)      //name被实例化目录中的一个文件名，dir为调用List的当前目录对象
	{	
		if(new File(dir,name).isDirectory()){
			if(name.replaceAll("\\..+?$", "").length()==0){
				return false;
			}
			return true;
		}
		if(isContain){
			for(String s:fileRule){
				if(name.endsWith(s))return true;
			}
			return false;
		}else{
			for(String s:fileRule){
				if(name.indexOf(s)>-1)return false;
			}
			return true;
		}
	}

}
