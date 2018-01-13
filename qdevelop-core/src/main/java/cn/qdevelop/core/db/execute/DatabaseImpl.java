package cn.qdevelop.core.db.execute;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QString;
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
				result.addResultSet(parseRecord(rsmd,rs,recordSize,query.isConvertNull()));
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

	private static Pattern isCaseBatchInsert = Pattern.compile("^insert .+ values ",Pattern.CASE_INSENSITIVE);
	private static Pattern isCaseBatchInsertHeadClean = Pattern.compile(" values .+$",Pattern.CASE_INSENSITIVE);
	private static Pattern isCaseBatchInsertLastClean = Pattern.compile("^.+ values ",Pattern.CASE_INSENSITIVE);

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

			int size=0;
			int MAX_SPLIT = 200;
			if(isCaseBatchInsert.matcher(ub.getPreparedSql()).find() && values.size() > MAX_SPLIT){//只有 insert & values时，动态改变批次插入的方法,并且数据超过100时，开启动态改变插入方法
				StringBuffer _sql = new StringBuffer();
				_sql.append(isCaseBatchInsertHeadClean.matcher(ub.getPreparedSql()).replaceAll("")).append(" values ");
				String insertModule = isCaseBatchInsertLastClean.matcher(ub.getPreparedSql()).replaceAll("");
				for(int i=0;i<MAX_SPLIT;i++){
					_sql.append(i==0?"":",").append(insertModule);
				}
				pstmt = conn.prepareStatement(_sql.toString());
				int pages = (int)(values.size()/MAX_SPLIT);
				for(int i=0;i<pages;i++){
					for(int j=0;j<MAX_SPLIT;j++){
						setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),values.get(MAX_SPLIT*i+j),j*ub.getColumns().length+1);
						//						System.out.println(pages+" : "+(MAX_SPLIT*i+j)+" vals "+(j*ub.getColumns().length+1));
					}
					pstmt.addBatch();
					if(++idx%500==0){
						pstmt.executeBatch();
						size += pstmt.getUpdateCount();
						System.out.println("singleInsertBatchUpdate:["+size+"] +1");
					}
				}
				pstmt.executeBatch();
				size += pstmt.getUpdateCount();
				int left = values.size()%MAX_SPLIT;
				if(left > 0){
					_sql = new StringBuffer();
					_sql.append(isCaseBatchInsertHeadClean.matcher(ub.getPreparedSql()).replaceAll("")).append(" values ");
					insertModule = isCaseBatchInsertLastClean.matcher(ub.getPreparedSql()).replaceAll("");
					for(int i=0;i<left;i++){
						_sql.append(i==0?"":",").append(insertModule);
					}
					pstmt = conn.prepareStatement(_sql.toString());
					for(int j=0;j<left;j++){
						setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),values.get(pages*MAX_SPLIT+j),j*ub.getColumns().length+1);
					}
					pstmt.addBatch();
					pstmt.executeBatch();
					size += pstmt.getUpdateCount();
				}
			}else{
				pstmt = conn.prepareStatement(ub.getPreparedSql());
				for(int i=0;i<values.size();i++){
					ub.setValues(values.get(i));
					setValue(ub.getPreparedSql(),ub.getDbsb(),pstmt,ub.getColumns(),ub.getValues());
					log.info("[batch] "+ub.getFullSql());
					pstmt.addBatch();
					if(++idx%1000==0){
						pstmt.executeBatch();
						size += pstmt.getUpdateCount();
						System.out.println("singleBatchUpdate:["+size+"] +1");
					}
				}
				pstmt.executeBatch();
				size += pstmt.getUpdateCount();
			}
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
			StringBuilder sb = new StringBuilder();
			sb.append(e.getMessage()).append(",第").append(idx+1).append("行数据错误：");
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

	final static Pattern hasAutoIncrementParam = Pattern.compile("\\{[a-zA-z0-9_]+\\.LAST_INSERT_ID\\}");
	final static Pattern getAutoIncrementKey = Pattern.compile("^.*\\{|\\.LAST_INSERT_ID\\}.*$");

	public boolean updateDB(Connection conn,IDBUpdate dbUpdate,List<IUpdateHook> updateHooks,boolean isAutoCommit) throws QDevelopException{
		HashMap<String,String> autoIncrement = new HashMap<String,String>();
		PreparedStatement pstmt = null;
		try {
			if(isAutoCommit)conn.setAutoCommit(false);
			List<UpdateBean> updateBeans = dbUpdate.getUpdateBeans();
			for(UpdateBean ub : updateBeans){
				try {
					while(hasAutoIncrementParam.matcher(ub.getPreparedSql()).find()){
						String key = getAutoIncrementKey.matcher(ub.getPreparedSql()).replaceAll("");
						if(autoIncrement.get(key) == null){
							throw new QDevelopException(1001,"【获取自增ID出错】【"+key+"】"+ub.getFullSql());
						}
						ub.setPreparedSql(ub.getPreparedSql().replace(QString.append("{",key,".LAST_INSERT_ID}"), autoIncrement.get(key)));
						ub.setFullSql(ub.getFullSql().replace(QString.append("{",key,".LAST_INSERT_ID}"), autoIncrement.get(key)));
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

	private Map<String,Object> parseRecord(ResultSetMetaData rsmd,ResultSet rs,int recordSize,boolean isConvertNull) throws QDevelopException, SQLException{
		Map<String,Object> data = new HashMap<String,Object>(recordSize);
		for(int i=1;i<=recordSize;i++){
			Object val = rs.getObject(i);
			if(isConvertNull && val == null){
				String columnTypeName = TableColumnType.clear_brackets.matcher(rsmd.getColumnTypeName(i)).replaceAll("");
				if(columnTypeName.equalsIgnoreCase("int") || columnTypeName.equalsIgnoreCase("tinyint")
						||columnTypeName.equalsIgnoreCase("decimal")  || columnTypeName.equalsIgnoreCase("double")){
					val = -1;
				}else if(columnTypeName.equalsIgnoreCase("bigint")||columnTypeName.equalsIgnoreCase("float")){
					val = -1L;
				}else if(columnTypeName.equalsIgnoreCase("varchar") || columnTypeName.equalsIgnoreCase("char")  || columnTypeName.equalsIgnoreCase("text")){
					val = "";
				}else if( columnTypeName.equalsIgnoreCase("date")||columnTypeName.equalsIgnoreCase("datetime")){
					val = new Date(0);
				}else {
					val = "";
				}
			}
			data.put(rsmd.getColumnLabel(i), val);
		}
		return data;
	}

	public void setValue(String sql,Map<String,DBStrutsLeaf> dbsb,PreparedStatement pstmt,String[] columns,Object[] values) throws QDevelopException{
		setValue( sql, dbsb, pstmt,columns,values,1);
	}
	
	
	private static Pattern isNumber = Pattern.compile("^\\-?[0-9]+?\\.?([0-9]+)?$");
	private static Pattern isDateTime = Pattern.compile("(^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$)|(^[0-9]{2}:[0-9]{2}:[0-9]{2}$)|(^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$)");

	public void setValue(String sql,Map<String,DBStrutsLeaf> dbsb,PreparedStatement pstmt,String[] columns,Object[] values,int start) throws QDevelopException{
		try {
			for(int i=0;i<columns.length;i++){
				if(values[i]==null){
					pstmt.setObject(i+start, null);
					continue;
				}
				DBStrutsLeaf sl = dbsb.get(columns[i]);
				if(sl!=null){
					int type = sl.getColumnType();
					switch(type){
					case 1:
						if(values[i].getClass().equals(Integer.class)){
							pstmt.setInt(i+start, (Integer)values[i]);
						}else{
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isNumber.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							pstmt.setInt(i+start, Integer.parseInt(v));
						}
						break;
					case 2:
						pstmt.setString(i+start, String.valueOf(values[i]));
						break;
					case 3:
						if(values[i].getClass().equals(Double.class)){
							pstmt.setDouble(i+start, (Double)values[i]);
						}else{
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isNumber.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							pstmt.setDouble(i+start, Double.parseDouble(v));
						}
						break;
					case 4:
						java.sql.Date val=null;
						if(values[i].getClass().equals(Date.class)){
							val = new java.sql.Date(((Date)values[i]).getTime());
						}else if(values[i].getClass().equals(String.class)){
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isDateTime.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							if(v.length() == 19){
								val = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(v).getTime());
							}else if(v.length() == 10){
								val = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(v).getTime());
							}
						}
						pstmt.setDate(i+start, val);
						break;
					case 5:
						java.sql.Timestamp val1=null;
						if(values[i].getClass().equals(Date.class)){
							val1 = new java.sql.Timestamp(((Date)values[i]).getTime());
						}else if(values[i].getClass().equals(String.class)){
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isDateTime.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							if(v.length() == 19){
								val1 = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(v).getTime());
							}else if(v.length() == 10){
								val1 = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(v).getTime());
							}
						}
						pstmt.setTimestamp(i+start, val1);
						break;
					case 6:
						if(values[i].getClass().equals(BigDecimal.class)){
							pstmt.setBigDecimal(i+start, (BigDecimal)values[i]);
						}else{
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isNumber.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							pstmt.setBigDecimal(i+start, new BigDecimal(v));
						}
						break;
					case 7:
						if(values[i].getClass().equals(Float.class)){
							pstmt.setFloat(i+start, (Float)values[i]);
						}else{
							String v = String.valueOf(values[i]).replaceAll("'", "");
							if(!isNumber.matcher(v).find()){
								throw new QDevelopException(1001,QString.append("参数",columns[i]," = '",v,"' "," 为非",sl.getColumnTypeName(),"错误"));
							}
							pstmt.setFloat(i+start, Float.parseFloat(v));
						}
						break;
					default :
						pstmt.setObject(i+start, values[i]);
						break;
					}
				}
			}
		} /*catch (NumberFormatException e) {
			e.printStackTrace();
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"数字格式转换错误",e);
		} catch (SQLException e) {
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"SQL预编译错误",e);
		} */catch (QDevelopException e) {
			throw e;
		}catch (Exception e) {
			log.error("预编译SQL："+sql);
			log.error(toDebugInfo(dbsb,columns,values));
			throw new QDevelopException(1003,"预编译SQL错误",e);
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
