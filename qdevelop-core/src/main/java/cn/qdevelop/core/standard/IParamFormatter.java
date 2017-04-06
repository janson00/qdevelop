package cn.qdevelop.core.standard;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;

public interface IParamFormatter  extends Cloneable {
	
	public IParamFormatter clone();
	
	public void initFormatter(Element conf) throws QDevelopException;
	public void init();
	public void setConfigAttrs(Map<String,String> attrs);
	
	public Map<String,Object> formatter( Map<String,?> query);
}
