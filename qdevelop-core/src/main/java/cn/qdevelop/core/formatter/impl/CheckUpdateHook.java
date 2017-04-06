package cn.qdevelop.core.formatter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.formatter.AbstractUpdateHook;
import cn.qdevelop.core.standard.IDBUpdate;
import cn.qdevelop.core.standard.IUpdateHook;

public class CheckUpdateHook extends AbstractUpdateHook implements IUpdateHook{
	public String sql;
	public String argName;
	public boolean isExsit;
	@Override
	public void initHook(Element conf) throws QDevelopException {
		if(attrs!=null){
			Set<String> keys = attrs.keySet();
			for(String attr:keys){
				if(conf.attributeValue(attr)==null){
					throw new QDevelopException(1001,"formatter配置不全错误："+attrs.toString());
				}
			}
			sql = conf.attributeValue("check-sql");
			argName = conf.attributeValue("check-arg");
			isExsit = Boolean.parseBoolean(conf.attributeValue("check-exsit"));
		}
	}

	@Override
	public void init(Connection conn,Map<String, ?> query) throws QDevelopException {
		Object val = query.get(argName);
		if(val!=null){
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setObject(1, val);
				ResultSet rs = pstmt.executeQuery();
				boolean hasData = rs.next();
				if((hasData&!isExsit)||(!hasData&&isExsit)){
					throw new QDevelopException(1002,"数据检查未通过，跳出执行更新语句");
				}
			} catch (SQLException e) {
				throw new QDevelopException(1000,e.getMessage(),e);
			}finally{
				try {
					if(pstmt!=null)pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void execHook(Connection conn, UpdateBean ub, int fetchSize, int lastInsertId) throws SQLException {

	}

	@Override
	public void flush(Connection conn, Map<String, ?> query, IDBUpdate dbUpdate) throws QDevelopException {

	}

	
	



}
