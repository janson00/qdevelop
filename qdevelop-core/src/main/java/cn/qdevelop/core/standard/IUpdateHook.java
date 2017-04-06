package cn.qdevelop.core.standard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.UpdateBean;

public interface IUpdateHook extends Cloneable{
	public IUpdateHook clone();
	
	public void setConfigAttrs(Map<String,String> conf);
	
	public void initHook(Element conf) throws QDevelopException;
	
	public void init(Connection conn,Map<String,?> query) throws QDevelopException;
	/**
	 * hook执行，每执行一条sql时就执行一次,
	 * throw SQLException时中断本次更新请求
	 * 使用connection时不可直接关闭！！！
	 * @param conn
	 * @param ub
	 * @param fetchSize
	 * @param lastInsertId
	 */
	public void execHook(Connection conn,UpdateBean ub,int fetchSize,int lastInsertId) throws SQLException;
	
	/**
	 * 统一提交执行方法，执行一次
	 * 使用connection时不可直接关闭！！！
	 * @param conn
	 * @param query
	 * @param dbUpdate
	 */
	public void flush(Connection conn,Map<String,?> query,IDBUpdate dbUpdate) throws QDevelopException;
}
