package cn.qdevelop.core.formatter.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class ExtraJsonFormatter extends AbstractResultFormatter implements IResultFormatter{

	String resultKey;

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		resultKey = conf.attributeValue("result-key");
	}

	@Override
	public boolean isQBQuery() {
		return false;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		if(data.get(resultKey)!=null){
			String jsonStr = String.valueOf(data.get(resultKey));
			if(jsonStr.length()>2 && jsonStr.startsWith("{") && jsonStr.endsWith("}")){
				JSONObject json = JSON.parseObject(jsonStr);
				Iterator<Entry<String, Object>> itors = json.entrySet().iterator();
				while(itors.hasNext()){
					Entry<String, Object> itor = itors.next();
					data.put(itor.getKey(), itor.getValue());
				}
				json = null;
				data.remove(resultKey);
			}
		}
	}

	@Override
	public void flush(IDBResult result) throws QDevelopException {

	}

}
