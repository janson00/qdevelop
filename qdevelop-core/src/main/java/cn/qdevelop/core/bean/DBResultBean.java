package cn.qdevelop.core.bean;

import java.util.ArrayList;
import java.util.Map;

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

}
