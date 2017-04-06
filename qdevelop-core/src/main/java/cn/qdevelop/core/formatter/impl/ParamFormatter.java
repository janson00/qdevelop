package cn.qdevelop.core.formatter.impl;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.standard.IParamFormatter;

public class ParamFormatter implements IParamFormatter{

	@Override
	public IParamFormatter clone() {
		try {
			return (IParamFormatter)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setConfigAttrs(Map<String, String> attrs) {
		
	}

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		
	}

	@Override
	public Map<String, Object> formatter(Map<String, ?> query) {
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	

}
