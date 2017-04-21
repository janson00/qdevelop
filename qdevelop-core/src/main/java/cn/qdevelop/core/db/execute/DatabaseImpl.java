package cn.qdevelop.core.db.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IDBUpdate;
import cn.qdevelop.core.standard.IUpdateHook;

public class DatabaseImpl {
	private final static Logger log  = QLog.getLogger(DatabaseImpl.class);

	public IDBResult queryDB(Connection conn,IDBQuery query,IDBResult result) throws QDevelopException{
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(query.getPreparedSql());
			setValue(query.getPreparedSql(),query.getTableStruts(),pstmt,query.getPreparedColumns(),query.getPreparedValues());
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			log.info("["+query.getPreparedColumns().length+"] "+query.getSql());
			int recordSize = rsmd.getColumnCount();
			while(rs.next()){
				result.addResultSet(parseRecord(rsmd,rs,recordSize));
			}
		} catch (SQLException e) {
			log.error("["+query.getIndex()+"] "+query.getPreparedSql());
			log.error(toDebugInfo(query.getTableStruts(),query.getPreparedColumns(),query.getPreparedValues()));
			throw new QDevelopException(1001,"数据库查询错误",e);
		}finally{
			try {
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	final static Pattern hasAutoIncrementParam = Pattern.compile("\\{[a-zA-z0-9_]+\\.LAST_INSERT_ID\\}");
	final static Pattern getAutoIncrementKey = Pattern.compile("^.*\\{|\\.LAST_INSERT_ID\\}.*$");

	/**
	 * 插入并返回自增ID的方法
	 * @param conn
	 * @param ub
	 * @param updateHooks
	 * @param isAutoCommit
	 * @return
	 * @throws QDevelopException
	 */
	public int insertDBReturnAutoID(Connection conn,UpdateBean ub,List<IUpdateHook> updateHooks,boolean isAutoCommit) throws QDevelopException{
		if(!ub.isInsert())throw new QDevelopException(1001,"此方法只执行insert语句"+ub.getFullSql()); 
		PreparedStatement pstmt = null;
		try {
			if(isAutoCommit)conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(ub.getPreparedSql(),Statement.RETURN_GENERATED_KEYS);
			setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),ub.getValues());

			int fetch = pstmt.executeUpdate();
			if(ub.isFetchZeroError() && fetch == 0){
				throw new QDevelopException(1001,"【影响记录数为0】"+ub.getFullSql());
			}
			int last_id=0;
			ResultSet rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				last_id = rs.getInt(1);
			}
			log.info("["+last_id+"] "+ub.getFullSql());
			if(updateHooks!=null){
				for(IUpdateHook uh : updateHooks){
					uh.execHook(conn,ub, fetch, last_id);
				}
			}
			if(isAutoCommit)conn.commit();
			return last_id;
		} catch (SQLException e) {
			log.error("["+ub.getIndex()+"] "+ub.getPreparedSql());
			log.error(toDebugInfo(ub.getDbsb(),ub.getColumns(),ub.getValues()));
			if(isAutoCommit){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new QDevelopException(999,"数据库执行错误",e);
		}finally{
			try {
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(isAutoCommit){
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 批量执行更新语句
	 * @param conn
	 * @param ub
	 * @param values
	 * @param updateHooks
	 * @param isAutoCommit
	 * @return
	 * @throws QDevelopException
	 */
	public int singleBatchUpdate(Connection conn,UpdateBean ub,List<Object[]> values,List<IUpdateHook> updateHooks,boolean isAutoCommit) throws QDevelopException{
		PreparedStatement pstmt = null;
		int idx=0;
		try {
			if(isAutoCommit)conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(ub.getPreparedSql());
			int size=0;
			for(idx=0;idx<values.size();idx++){
				ub.setValues(values.get(idx));
				setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),ub.getValues());
				log.info("[batch] "+ub.getFullSql());
				pstmt.addBatch();
				if(idx%1000==0){
					pstmt.executeBatch();
					size += pstmt.getUpdateCount();
					System.out.println("singleBatchUpdate:["+size+"] +1");
				}
			}
			pstmt.executeBatch();
			size += pstmt.getUpdateCount();
			if(isAutoCommit)conn.commit();
			return size;
		} catch (Exception e) {
			log.error("["+ub.getIndex()+"] "+ub.getPreparedSql());
			log.error(toDebugInfo(ub.getDbsb(),ub.getColumns(),ub.getValues()));
			if(isAutoCommit){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			StringBuilder sb = new StringBuilder().append("数据库批量执行错误,第").append(idx+1).append("行数据错误：");
			Object[] val = values.get(idx);
			for(Object v : val){
				sb.append(v).append(",");
			}
			QDevelopException ex =  new QDevelopException(999,sb.toString(),e);
			ex.setDetail(val);
			throw ex;
		}finally{
			try {
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(isAutoCommit){
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean updateDB(Connection conn,IDBUpdate dbUpdate,List<IUpdateHook> updateHooks,boolean isAutoCommit) throws QDevelopException{
		HashMap<String,String> autoIncrement = new HashMap<String,String>();
		PreparedStatement pstmt = null;
		try {
			if(isAutoCommit)conn.setAutoCommit(false);
			List<UpdateBean> updateBeans = dbUpdate.getUpdateBeans();
			for(UpdateBean ub : updateBeans){
				try {
					if(hasAutoIncrementParam.matcher(ub.getPreparedSql()).find()){
						String key = getAutoIncrementKey.matcher(ub.getPreparedSql()).replaceAll("");
						if(autoIncrement.get(key) == null){
							throw new QDevelopException(1001,"【获取自增ID出错】【"+key+"】"+ub.getFullSql());
						}
						ub.setPreparedSql(hasAutoIncrementParam.matcher(ub.getPreparedSql()).replaceAll(autoIncrement.get(key)));
					}
					if(ub.isInsert()){
						pstmt = conn.prepareStatement(ub.getPreparedSql(),Statement.RETURN_GENERATED_KEYS);
					}else{
						pstmt = conn.prepareStatement(ub.getPreparedSql());
					}
					setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),ub.getValues());

					int fetch = pstmt.executeUpdate();
					if(ub.isFetchZeroError() && fetch == 0){
						throw new QDevelopException(1001,"【影响记录数为0】"+ub.getFullSql());
					}
					int last_id=0;
					if(ub.isInsert()){
						ResultSet rs=pstmt.getGeneratedKeys();
						if(rs.next()){
							last_id = rs.getInt(1);
							autoIncrement.put(ub.getTableName(), String.valueOf(last_id));
						}
						log.info("["+last_id+"] "+ub.getFullSql());
					}else{
						log.info(ub.getFullSql());
					}
					if(updateHooks!=null){
						for(IUpdateHook uh : updateHooks){
							uh.execHook(conn,ub, fetch, last_id);
						}
					}
				} catch (Exception e) {
					log.error("["+ub.getIndex()+"] "+ub.getPreparedSql());
					log.error(toDebugInfo(ub.getDbsb(),ub.getColumns(),ub.getValues()));
					if(isAutoCommit){
						try {
							conn.rollback();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					throw e;
				}
			}
			if(isAutoCommit)conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			if(isAutoCommit){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new QDevelopException(999,"数据库执行错误",e);
		}finally{
			autoIncrement.clear();
			try {
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(isAutoCommit){
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Map<String,Object> parseRecord(ResultSetMetaData rsmd,ResultSet rs,int recordSize) throws QDevelopException, SQLException{
		Map<String,Object> data = new HashMap<String,Object>(recordSize);
		for(int i=1;i<=recordSize;i++){
			data.put(rsmd.getColumnLabel(i), rs.getObject(i));
		}
		return data;
	}

	public void setValue(String sql,Map<String,DBStrutsLeaf> dbsb,PreparedStatement pstmt,String[] columns,Object[] values) throws QDevelopException{
		try {
			for(int i=0;i<columns.length;i++){
				DBStrutsLeaf sl = dbsb.get(columns[i]);
				if(sl!=null){
					int type = sl.getColumnType();
					switch(type){
					case 1:
						if(values[i].getClass().equals(Integer.class)){
							pstmt.setInt(i+1, (Integer)values[i]);
						}else{
							pstmt.setInt(i+1, Integer.parseInt(String.valueOf(values[i]).replaceAll("'", "")));
						}
						break;
					case 2:
						pstmt.setString(i+1, String.valueOf(values[i]));
						break;
					case 3:
						if(values[i].getClass().equals(Double.class)){
							pstmt.setDouble(i+1, (Double)values[i]);
						}else{
							pstmt.setDouble(i+1, Double.parseDouble(String.valueOf(values[i]).replaceAll("'", "")));
						}
						break;
					case 4:
						java.sql.Date val=null;
						if(values[i].getClass().equals(Date.class)){
							val = new java.sql.Date(((Date)values[i]).getTime());

						}else if(values[i].getClass().equals(String.class)){
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(v.length() == 19){
								val = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(v).getTime());
							}else if(v.length() == 10){
								val = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(v).getTime());
							}
						}
						pstmt.setDate(i+1, val);
						break;
					case 5:
						java.sql.Timestamp val1=null;
						if(values[i].getClass().equals(Date.class)){
							val1 = new java.sql.Timestamp(((Date)values[i]).getTime());
						}else if(values[i].getClass().equals(String.class)){
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(v.length() == 19){
								val1 = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(v).getTime());
							}else if(v.length() == 10){
								val1 = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(v).getTime());
							}
						}
						pstmt.setTimestamp(i+1, val1);
						break;
					case 6:
						if(values[i].getClass().equals(Long.class)){
							pstmt.setLong(i+1, (Long)values[i]);
						}else{
							pstmt.setLong(i+1, Long.parseLong(String.valueOf(values[i]).replaceAll("'", "")));
						}
						break;
					default :
						pstmt.setObject(i+1, values[i]);
						break;
					}
				}
			}
		} catch (NumberFormatException e) {
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"数字格式转换错误",e);
		} catch (SQLException e) {
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"SQL预编译错误",e);
		} catch (ParseException e) {
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"日期格式转换错误",e);
		}

	}

	private String toDebugInfo(Map<String,DBStrutsLeaf> dbsb,String[] columns,Object[] values){
		if(columns==null||values==null||dbsb==null)return "";
		StringBuilder sb = new StringBuilder();
		sb.append("预编译详情：[");
		for(int i=0;i<columns.length;i++){
			if(i>0)sb.append(",");
			sb.append("{name:").append(columns[i])
			.append(",value:").append(values[i])
			.append(",type:").append(dbsb.get(columns[i])==null?"err-"+columns[i]:dbsb.get(columns[i]).getColumnTypeName()).append("}");
		}
		sb.append("]");
		return sb.toString();
	}

}
