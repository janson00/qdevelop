package cn.qdevelop.core.standard;

import java.util.Map;

public interface IDBResult {
	
	public void addResultSet(Map<String,Object> val);
	
	public Map<String,Object> getResult(int index);
	
	public int getSize();
	
	public Object getObject(int idx,String columnName);
	
	public String getString(int idx,String columnName);
	
	public Integer getInteger(int idx,String columnName);
	
	public Long getLong(int idx,String columnName);
	
	public Double getDouble(int idx,String columnName);
	
	/**
	 * 转成需要的JavaBean,支持数据库下划线直接转驼峰变量
	 * @param idx
	 * @param clazz
	 */
	public Object toJavaBean(int idx,Object clazz);
	
	/**
	 * 判定是否存在数据
	 * @return
	 */
	public boolean hasData();
	
	
}
