package cn.qdevelop.core.standard;

import java.util.Map;

import cn.qdevelop.core.db.bean.DBStrutsLeaf;

public interface IDBQuery {
	public String getIndex();
	
	public void setSql(String sql);
	public String getSql();
	
	/**
	 * 获取预编译sql
	 * @return
	 */
	public String getPreparedSql();
	
	public void setPreparedSql(String sql);
	
	public String[] getPreparedColumns();
	
	public Object[] getPreparedValues();
	
	public Map<String,DBStrutsLeaf> getTableStruts();
	
	public void setPage(int page,int pageNum);
	
	public void clear();
	
	/**
	 * 是否转换空值
	 * @return
	 */
	public boolean isConvertNull();
	
	public boolean isNeedTotle();
	
	/**
	 * 是否需要混合编译
	 * @return
	 */
	public boolean isComplexBuild(); 
	
	public String toString();
	
	
}
