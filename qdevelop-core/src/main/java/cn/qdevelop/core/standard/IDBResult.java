package cn.qdevelop.core.standard;

import java.util.Map;

public interface IDBResult {
	
	public void addResultSet(Map<String,Object> val);
	
	public Map<String,Object> getResult(int index);
	
	public int getSize();
	
}
