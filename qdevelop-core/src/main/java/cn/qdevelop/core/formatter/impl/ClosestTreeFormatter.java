package cn.qdevelop.core.formatter.impl;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class ClosestTreeFormatter extends AbstractResultFormatter implements IResultFormatter{

	String formatterIndex,formatterKey,resultKey;
	String[] formatterColumns;
	
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			formatterIndex = conf.attributeValue("data-index");
			formatterKey = conf.attributeValue("leaf-key");
			resultKey =  conf.attributeValue("parents-key");
			formatterColumns = conf.attributeValue("columns").split(",");
		}
	}

	@Override
	public boolean isQBQuery() {
		return false;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		
	}

	@Override
	public void flush(IDBResult result) throws QDevelopException {
		
	}

}
