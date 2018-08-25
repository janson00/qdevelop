package cn.qdevelop.core.formatter;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.files.QSource;
import cn.qdevelop.common.files.SearchFileFromJars;
import cn.qdevelop.common.files.SearchFileFromProject;
import cn.qdevelop.common.utils.QXMLUtils;
import cn.qdevelop.core.Contant;
import cn.qdevelop.core.standard.IParamFormatter;
import cn.qdevelop.core.standard.IResultFormatter;
import cn.qdevelop.core.standard.IUpdateHook;

public class FormatterLoader{
	/**
	 * 
	 */
	private static FormatterLoader _FormatterLoader = new FormatterLoader();
	public static FormatterLoader getInstance(){
		
		return _FormatterLoader;
	}
	
	private  HashMap<String,IResultFormatter> resultFormatterCollections;
	private  HashMap<String,IParamFormatter> paramFormatterCollections;
	private  HashMap<String,IUpdateHook> updateHookCollections;
	
	private static Logger log;
	public FormatterLoader(){
		log  = QLog.getLogger(FormatterLoader.class);
		init();
	}
	
	private void init(){
		resultFormatterCollections = new HashMap<String,IResultFormatter>();
		paramFormatterCollections = new HashMap<String,IParamFormatter>();
		updateHookCollections = new HashMap<String,IUpdateHook>();
		
		final int projectIndex = QSource.getProjectPath().length();
		new SearchFileFromJars(){
			@SuppressWarnings("unchecked")
			@Override
			public void desposeFile(String jarName,String fileName, InputStream is) {
				try {
					Element root = new QXMLUtils().getDocument(is).getRootElement();
					if(root.getName().equals(Contant.FORMATTER_CONFIG_ROOT)){
						initConfig(root.elementIterator(),jarName+"!"+fileName);
					}
				} catch (Exception e) {
					log.error("err:"+jarName+"!"+fileName);
					e.printStackTrace();
				}
			}
		}.searchAllJarsFiles("qdevelop-formatter.xml$");
		new SearchFileFromProject(){
			@SuppressWarnings("unchecked")
			@Override
			protected void disposeFile(File f) {
				try {
					Element root = new QXMLUtils().getDocument(f).getRootElement();
					if(root.getName().equals(Contant.FORMATTER_CONFIG_ROOT)){
						initConfig(root.elementIterator(),f.getAbsolutePath().substring(projectIndex));
					}
				} catch (Exception e) {
					log.error("err:"+f.getAbsolutePath());
					e.printStackTrace();
				}
			}

			@Override
			protected void disposeFileDirectory(File f) {
			}
		}.searchProjectFiles("qdevelop-formatter.xml$");
	}
	
	private void initConfig(Iterator<Element> iter,String fileName) throws QDevelopException{
		log.info("load formatter: "+fileName);
		while (iter.hasNext()) {
			Element property = iter.next();
			String name = property.attributeValue("name");
			String clazz = property.attributeValue("class");
//			System.out.println(name+"   >>>> "+clazz);
			@SuppressWarnings("unchecked")
			List<Element> attrs = property.elements();
			Map<String,String> base = new HashMap<String,String>();
			for(Element attr : attrs){
				base.put(attr.getName(), attr.getText());
			}
			if(property.getName().equals("param-formatter")){
				IParamFormatter paramFormatter = (IParamFormatter)getInstanceClass(clazz);
				paramFormatter.setConfigAttrs(base);
				paramFormatterCollections.put(name, paramFormatter);
			}else if(property.getName().equals("result-formatter")){
				IResultFormatter resultFormatter = (IResultFormatter)getInstanceClass(clazz);
				resultFormatter.setConfigAttrs(base);
				resultFormatterCollections.put(name, resultFormatter);
			}else if(property.getName().equals("update-hook")){
				IUpdateHook iuh = (IUpdateHook)getInstanceClass(clazz);
				iuh.setConfigAttrs(base);
				updateHookCollections.put(name, iuh);
			}
		}
	}
	
	private Object getInstanceClass(String className) throws QDevelopException{
		if(className==null||className.trim().length()==0)return null;
		try {
			return  Class.forName(className).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new QDevelopException(1001,"formatter: ["+className+"] 类不存在",e);
		}
	}
	
	
	public IResultFormatter getResultFormatter(String key){
		return resultFormatterCollections.get(key) == null ? null : resultFormatterCollections.get(key).clone();
	}
	
	public IParamFormatter getParamFormatter(String key){
		return paramFormatterCollections.get(key) == null ? null : paramFormatterCollections.get(key).clone();
	}
	
	public IUpdateHook getUpdateHook(String key){
		return updateHookCollections.get(key) == null ? null : updateHookCollections.get(key).clone();
	}
	
	
	public static void main(String[] args) {
		int s = FormatterLoader.getInstance().resultFormatterCollections.size();
		FormatterLoader.getInstance().getResultFormatter("prop-formatter");
		System.out.println(s);
	}

}
