package cn.qdevelop.core.formatter.impl;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class ClosestRootFormatter extends AbstractResultFormatter implements IResultFormatter{

	String currentIndex,parentKey,resultKey;
	String[] formatterColumns;
	
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			currentIndex = conf.attributeValue("index");
			
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
