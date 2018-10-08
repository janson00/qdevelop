package cn.qdevelop.core.formatter.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class ClosestRootFormatter extends AbstractResultFormatter implements IResultFormatter{

	String currentIndex,parentKey,onKey,split;
	String formatterColumns;
	int deepMax;

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			currentIndex = conf.attributeValue("index");
			parentKey = conf.attributeValue("parent-key");
			onKey =  conf.attributeValue("on-key");
			formatterColumns =  conf.attributeValue("column-key");
			split =  conf.attributeValue("split") == null ? " > " : conf.attributeValue("split");
			deepMax =  Integer.parseInt(conf.attributeValue("deep-max") == null ? "3" : conf.attributeValue("deep-max"));
		}
	}
	public HashSet<String> conditions;
	@Override
	public void init() {
		conditions = new HashSet<String>();
	}

	@Override
	public boolean isQBQuery() {
		return true;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		String val = data.get(parentKey) == null ? "0" : String.valueOf(data.get(parentKey));
		if(!val.equals("0")){
			conditions.add(String.valueOf(val));
		}
	}


	@Override
	public void flush(IDBResult result) throws QDevelopException {
		if(conditions.size() == 0 )return;
		String key = "parent_"+formatterColumns;
		String tmpVal = result.getString(0, key);
		if(tmpVal!=null && tmpVal.split(split).length>=deepMax){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> out deep max");
			return;
		}
		StringBuilder sb = new StringBuilder();
		for(String v : conditions){
			sb.append("|").append(v);
		}
		String parentValue = sb.substring(1).toString();
		IDBResult rb = DatabaseFactory.getInstance().queryDatabase(new DBArgs(currentIndex).put(onKey, parentValue));
		if(rb.hasData()){
			Map<String,Map<String,Object>> tmp = new HashMap<String,Map<String,Object>>();
			for(int i=0;i<rb.getSize();i++){
				tmp.put(rb.getString(i, onKey), rb.getResult(i));
			}
			for(int i=0;i<result.getSize();i++){
				String parentId = result.getString(i, parentKey);
				Map<String,Object> tDat = tmp.get(parentId);
				if(tDat != null){
					if(tDat.get(key)!=null){
						result.getResult(i).put(key, new StringBuffer().append(tDat.get(key)).append(split).append(tDat.get(formatterColumns)).toString());
					}else{
						result.getResult(i).put(key, String.valueOf(tDat.get(formatterColumns)));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(String.valueOf(null));
	}

}
