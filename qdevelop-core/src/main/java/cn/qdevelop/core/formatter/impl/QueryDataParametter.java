package cn.qdevelop.core.formatter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.formatter.AbstractParamFormatter;
import cn.qdevelop.core.standard.IDBResult;

/**
 * 根据条件，查询数据表中一条记录中的某些字段数据值，作为参数去请求其他操作。
 * @author janson
 *
 */
public class QueryDataParametter extends AbstractParamFormatter{

	String index;
	String[] whereArgs;
	String[] columns;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		 index = conf.attributeValue("index");
		 whereArgs = conf.attributeValue("where-key").split(",");
		 columns = conf.attributeValue("columns").split(",");
	}

	@Override
	public void init() {
		
	}

	@Override
	public Map<String, Object> formatter(Map<String,  Object> query) {
		HashMap<String,Object> args = new HashMap<String,Object>();
		args.put("index", index);
		for(String key : whereArgs){
			args.put(key, query.get(key));
		}
		args.put("page", 1);
		args.put("limit", 1);
		try {
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(args);
			if(rb.getSize()>0){
				Map<String,Object> data = rb.getResult(0);
				for(String key : columns){
					String argKey ;
					if(key.indexOf(" as ")>-1){
						argKey = key.substring(key.indexOf(" as ")+4);
						key = key.substring(0,key.indexOf(" as "));
					}else{
						argKey = key;
					}
					if(data.get(key)!=null){
						query.put(argKey, data.get(key));
					}
				}
			}
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
		return query;
	}
	
	private static Pattern clean = Pattern.compile("^.+ as ");
	
	public String[] getncreaseKeys(){
		String[] arg = new String[columns.length];
		for(int i=0;i<arg.length;i++){
			arg[i] = clean.matcher(columns[i]).replaceAll("");
		}
		return arg;
	}

	

}
