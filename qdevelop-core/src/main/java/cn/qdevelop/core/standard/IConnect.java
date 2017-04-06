package cn.qdevelop.core.standard;

import java.sql.Connection;

import cn.qdevelop.common.exception.QDevelopException;


public interface IConnect {
	/**
	 * 初始化数据库链接
	 */
	public void init();
	
	/**
	 * 获取数据库链接
	 * @return
	 */
	public Connection getConnection() throws QDevelopException;
	
	/**
	 * 关闭数据库链接
	 * @param conn
	 */
	public void close(Connection conn);
	
	public void shutdown();
	
	public int getCanUseNum();
	
	
	/**
	 * 打印数据库链接相关信息
	 */
	public void printInfo();
	
	public String monitor();
	
	public String getDataBase();
}
