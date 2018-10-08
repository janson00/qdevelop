package cn.qdevelop.core.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import cn.qdevelop.core.standard.IDBResult;

public class DBResultBean extends ArrayList<Map<String, Object>> implements IDBResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7794331941337303308L;

	@Override
	public void addResultSet(Map<String, Object> val) {
		this.add(val);
	}

	@Override
	public int getSize() {
		return super.size();
	}

	@Override
	public Map<String, Object> getResult(int index) {
		return this.get(index);
	}
	
	@Override
	public Object getObject(int idx, String columnName) {
		if(idx>=getSize())return null;
		return super.get(idx).get(columnName);
	}

	@Override
	public String getString(int idx, String columnName) {
		if(idx>=getSize())return null;
		Object val = this.get(idx).get(columnName);
		if(val == null )return null;
		if(val.getClass().getName().equals("java.lang.String")){
			return (String)val;
		}else{
			return String.valueOf(val); 
		}
	}

	@Override
	public Integer getInteger(int idx, String columnName) {
		if(idx>=getSize())return null;
		Object val = this.get(idx).get(columnName);
		if(val == null )return null;
		if(val.getClass().getName().equals("java.lang.Integer")){
			return (Integer)val;
		}else{
			return Integer.parseInt(String.valueOf(val)); 
		}
	}

	@Override
	public Long getLong(int idx, String columnName) {
		if(idx>=getSize())return null;
		Object val = this.get(idx).get(columnName);
		if(val == null )return null;
		if(val.getClass().getName().equals("java.lang.Long")){
			return (Long)val;
		}else{
			return Long.parseLong(String.valueOf(val)); 
		}
	}

	@Override
	public Double getDouble(int idx, String columnName) {
		if(idx>=getSize())return null;
		Object val = this.get(idx).get(columnName);
		if(val == null )return null;
		if(val.getClass().getName().equals("java.lang.Double")){
			return (Double)val;
		}else{
			return Double.parseDouble(String.valueOf(val)); 
		}
	}

	
	@Override
	public Object toJavaBean(int idx,Object clazz) {
		if(idx>=getSize())return clazz;
		try {
			HashMap<String,Object> val = new HashMap<String,Object>(this.get(idx));
			Iterator<String> keys = val.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				/**兼容下划线转驼峰写法的参数赋值**/
				if(key.indexOf("_")>-1&&!key.startsWith("__")){
					StringBuffer kb = new StringBuffer();
					char[] tmp = key.toCharArray();
					for(int i=0;i<tmp.length;i++){
						if(tmp[i] == '_'){
							i = i+1;
							if(i<tmp.length){
								kb.append(String.valueOf(tmp[i]).toUpperCase());
							}
						}else{
							kb.append(tmp[i]);
						}
					}
					val.put(kb.toString(), val.get(key));
				}
			}
			BeanUtils.populate(clazz, val);
			val.clear();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}  
		return clazz;
	}

	@Override
	public boolean hasData() {
		return super.size() > 0;
	}

	

}
