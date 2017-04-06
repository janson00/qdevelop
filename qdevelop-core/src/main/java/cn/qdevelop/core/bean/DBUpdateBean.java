package cn.qdevelop.core.bean;

import java.util.ArrayList;
import java.util.List;

import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.standard.IDBUpdate;

public class DBUpdateBean extends ArrayList<UpdateBean> implements IDBUpdate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7570888768103420909L;

	@Override
	public List<UpdateBean> getUpdateBeans() {
		return this;
	}

	@Override
	public void addUpdateBean(UpdateBean ub) {
		this.add(ub);
	}

	@Override
	public void clear() {
		super.clear();
	}
	String index;

	@Override
	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public String getIndex() {
		return index;
	}

}
