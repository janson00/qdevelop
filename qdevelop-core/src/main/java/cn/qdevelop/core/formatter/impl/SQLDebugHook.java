package cn.qdevelop.core.formatter.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.standard.IDBUpdate;
import cn.qdevelop.core.standard.IUpdateHook;

public class SQLDebugHook implements IUpdateHook{
	private static SimpleDateFormat sdf;
	@Override
	public IUpdateHook clone() {
		try {
			return (IUpdateHook)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	int idx = 1;
	@Override
	public void execHook(Connection conn, UpdateBean ub, int fetchSize, int lastInsertId) throws SQLException{
		print(idx++,"[",fetchSize,"-",lastInsertId,"]",ub.getFullSql());
	}

	@Override
	public void flush(Connection conn, Map<String, ?> query, IDBUpdate dbUpdate) throws QDevelopException{
		print("query",query.toString());
		List<UpdateBean> tmp = dbUpdate.getUpdateBeans();
		int idx = 1;
		for(UpdateBean ub : tmp){
			System.out.println("[exec-sql] "+ub.getPreparedSql());
			print(idx++,ub);
		}
		
	}
	
	private void print(Object ... vals ){
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(sdf.format(new Date())).append("] ");
		for(Object o : vals){
			sb.append(o).append(" ");
		}
		System.out.println(sb);
	}
	
	private void print(int idx ,UpdateBean ub){
		StringBuilder sb = new StringBuilder();
		sb.append("[exec-sql] ").append(idx).append(" : 预编译数据={");
		for(int i=0;i<ub.getColumns().length;i++){
			sb.append(i>0?",":"").append(ub.getColumns()[i]).append(":").append(ub.getValues()[i]);
		}
		System.out.println(sb.append("}"));
	}

	public void init(Connection conn,Map<String,?> query) throws QDevelopException{
		idx = 1;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public void setConfigAttrs(Map<String, String> conf) {
		
	}

	@Override
	public void initHook(Element conf) throws QDevelopException {
		// TODO Auto-generated method stub
		
	}
	

}
