package cn.qdevelop.core.standard;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;

public interface IResultFormatter  extends Cloneable{
	public IResultFormatter clone();
	public void initFormatter(Element conf) throws QDevelopException;
	public void setConfigAttrs(Map<String,String> attrs);
	public boolean isQBQuery();
	
	public void formatter(Map<String,Object> data);
	
	public void flush(IDBResult result)  throws QDevelopException;
	
	public void init();
	
}
