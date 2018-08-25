package cn.qdevelop.core.formatter.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.config.SQLConfigLoader;
import cn.qdevelop.core.formatter.AbstractParamFormatter;

public class ExtraJsonParametter extends AbstractParamFormatter{
	String index,extraKey;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(conf!=null){
//			System.out.println(">>>>>"+conf.asXML());
			index = conf.attributeValue("index");
			extraKey = conf.attributeValue("extra-key");
		}
	}

	@Override
	public void init() {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> formatter(Map<String,  Object> query) {
		Element config = SQLConfigLoader.getInstance().getSQLConfig(index);
		HashSet<String> args = new HashSet<String>();
		List<Element> sqls = config.elements("sql");
		for(Element sql : sqls){	
			String[] tmp = sql.attributeValue("params").split("\\|");
			for(String t : tmp){
				if(t.length()>0){
					args.add(t);
				}
			}
		}
		HashMap<String,Object> extra = new HashMap<String,Object>();
		Iterator<String> keys = query.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			if(!ArrayUtils.contains(SQLConfigParser.specilQueryKey, key) && !args.contains(key)){
				extra.put(key, query.get(key));
				query.remove(key);
			}
		}
		query.put(extraKey, JSON.toJSONString(extra));
		extra.clear();
		args.clear();
		keys = null;
		return query;
	}

	@Override
	public String[] getncreaseKeys() {
		return null;
	}


	

}
