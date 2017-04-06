package cn.qdevelop.core.db.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DBStrutsBean extends HashMap<String,DBStrutsLeaf>{
	private static final long serialVersionUID = -7390592305262986054L;
	
	public void addStruts(DBStrutsLeaf sl){
		this.put(sl.getColumnName(), sl);
	}
	
	public DBStrutsLeaf getStruts(String columnName){
		return this.get(columnName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String toString(){
		StringBuilder sb  = new StringBuilder();
		sb.append("{");
		boolean isFirst = true;
		Iterator iter = this.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,DBStrutsLeaf> entry = (Map.Entry<String,DBStrutsLeaf>) iter.next();
			String key = entry.getKey();
			DBStrutsLeaf sl = entry.getValue();
			sb.append(isFirst?"":",").append("\"").append(key).append("\":")
			.append("{\"Field\":\"").append(sl.getColumnName()).append("\",")
			.append("\"Type\":\"").append(sl.getColumnTypeName()).append("\",")
			.append("\"Size\":").append(sl.getSize()).append(",")
			.append("\"Null\":").append(sl.isNullAble).append("}");
			isFirst = false;
		}
		return sb.append("}").toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void print(){
		StringBuilder sb  = new StringBuilder();
		sb.append("{\n");
		boolean isFirst = true;
		Iterator iter = this.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,DBStrutsLeaf> entry = (Map.Entry<String,DBStrutsLeaf>) iter.next();
			String key = entry.getKey();
			DBStrutsLeaf sl = entry.getValue();
			sb.append(isFirst?"	":"	,").append("\"").append(key).append("\":")
			.append("{\"Field\":\"").append(sl.getColumnName()).append("\",")
			.append("\"Type\":\"").append(sl.getColumnTypeName()).append("\",")
			.append("\"Size\":").append(sl.getSize()).append(",")
			.append("\"Null\":").append(sl.isNullAble).append("}\n");
			isFirst = false;
		}
		sb.append("}");
		System.out.println(sb.toString());
	}

}
