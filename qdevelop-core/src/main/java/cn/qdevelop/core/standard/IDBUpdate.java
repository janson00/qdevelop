package cn.qdevelop.core.standard;

import java.util.List;

import cn.qdevelop.core.db.bean.UpdateBean;

public interface IDBUpdate {
	
//	public List<String> getSqls();
	
	public void setIndex(String index);
	public String getIndex();
	
	public List<UpdateBean> getUpdateBeans();
	
	public void addUpdateBean(UpdateBean ub);
	
	public void clear();
	
}
