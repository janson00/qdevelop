package cn.qdevelop.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QString;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.config.SQLConfigLoader;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IDBUpdate;
import cn.qdevelop.core.standard.IParamFormatter;
import cn.qdevelop.core.standard.IResultFormatter;
import cn.qdevelop.core.standard.IUpdateHook;

public class DatabaseFactory {
	public static DatabaseFactory getInstance(){return new DatabaseFactory();}
	private final static Logger log  = QLog.getLogger(DatabaseFactory.class);


	/**
	 * 获取数据库链接,直接给定index的string或者请求来的hashmap
	 * @param query
	 * @return
	 * @throws QDevelopException
	 */
	@SuppressWarnings("unchecked")
	public Connection getConnectByQuery(Object query) throws QDevelopException{
		String index=null;
		if(query.getClass().equals(String.class)){
			index = (String)query;
		}else if(query.getClass().equals(Map.class)||query.getClass().equals(HashMap.class)){
			index = String.valueOf(((Map<String,?>)query).get("index"));
		}
		if(index==null)throw new QDevelopException(1002,"请求没有index");
		Element config = SQLConfigLoader.getInstance().getSQLConfig(index);
		if(config==null)throw new QDevelopException(1003,"["+index+"] SQL配置【"+index+"】不存在");
		return ConnectFactory.getInstance(config.attributeValue("connect")).getConnection();
	}
	private static Pattern cleanPrevSql = Pattern.compile("^select .+ from ", Pattern.CASE_INSENSITIVE);
	private static Pattern from = Pattern.compile(" from ", Pattern.CASE_INSENSITIVE);
	
	/**清除统计中无效参数**/
	private static String[] countClearArgs = new String[]{"order","page","limit","page_size"};
	
	/**
	 * 根据条件单独查询结果集总数
	 * @param query
	 * @return
	 * @throws QDevelopException
	 */
	public int queryDatabaseCount(Map<String,?> query) throws QDevelopException{
		long s = System.currentTimeMillis();
		String index = String.valueOf(query.get("index"));
		Connection conn = getConnectByQuery(query);
		
		try {
			formatterParameters(query);
			for(int i=0;i<countClearArgs.length;i++){
				query.remove(countClearArgs[i]);
			}
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			String sql = dbQuery.getPreparedSql();
			Matcher m = from.matcher(sql);
			int i=0;
			while(m.find()){
				i++;
			}
			if(i<2){
				dbQuery.setPreparedSql(cleanPrevSql.matcher(sql).replaceAll("select count(1) as cn from "));
				dbQuery.setSql(cleanPrevSql.matcher(dbQuery.getSql()).replaceAll("select count(1) as cn from "));
			}else{
				dbQuery.setPreparedSql("select count(1) as cn from ("+dbQuery.getPreparedSql()+") qdevelop_temp");
				dbQuery.setSql("select count(1) as cn from ("+dbQuery.getSql()+") qdevelop_temp");
			}
			IDBResult result = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, result);
			dbQuery.clear();
			if(result.getSize()>0){
				return  Integer.parseInt(String.valueOf(result.getResult(0).get("cn")));
			}else{
				return 0;
			}
		} catch (QDevelopException e) {
			throw e; 
		}finally{
			query.clear();
			ConnectFactory.close(conn);
			log.info("queryDatabaseCount:"+index+" use:"+(System.currentTimeMillis()-s)+"ms");
		}
	}
	


	/**
	 * 查询数据库
	 * @param query
	 * @return IDBResult
	 * @throws QDevelopException
	 */
	public IDBResult queryDatabase(Map<String,?> query) throws QDevelopException{
		Connection conn = null;
		try {
			conn = getConnectByQuery(query);
			return queryDatabase(query,conn);
		} catch (QDevelopException e) {
			throw e;
		}finally{
			ConnectFactory.close(conn);
		}
		
	}
	
	
	
	/**
	 * 查询数据库；自动清理请求参数，关闭数据库链接
	 * @param query
	 * @param conn
	 * @return
	 * @throws QDevelopException
	 */
	public IDBResult queryDatabase(Map<String,?> query,Connection conn) throws QDevelopException{
		long s = System.currentTimeMillis();
		String index = String.valueOf(query.get("index"));
		try {
			formatterParameters(query);
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			IDBResult result = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, result);
			formatterResult(dbQuery.getIndex(),result);
			dbQuery.clear();
			dbQuery=null;
			return result;
		} catch (QDevelopException e) {
			throw e;
		}finally{
			query.clear();
			log.info("queryDatabase:"+index+" use:"+(System.currentTimeMillis()-s)+"ms");
		}
	}


	/**
	 * @deprecated
	 * 查询数据库；自助控制数据库链接，请求参数和数据库链接都不清理
	 * @param query
	 * @param conn
	 * @return
	 * @throws QDevelopException
	 */
	public IDBResult queryDatabaseSelfControl(Map<String,?> query,Connection conn) throws QDevelopException{
		try {
			formatterParameters(query);
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			IDBResult result = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, result);
			formatterResult(dbQuery.getIndex(),result);
			dbQuery.clear();
			dbQuery=null;
			return result;
		} catch (QDevelopException e) {
			throw e;
		}
	}

	/**
	 * 更新数据库；框架提供整体事务控制，提供链接关闭功能
	 * @param query
	 * @return
	 * @throws QDevelopException
	 */
	public boolean updateDatabase(Map<String,?> query) throws QDevelopException{
		Connection conn = null;
		try {
			conn = getConnectByQuery(query);
			return updateDatabase(query,conn,true);
		} catch (QDevelopException e) {
			throw e;
		}finally{
			ConnectFactory.close(conn);
		}
	}

	public boolean updateDatabase(Map<String,?> query,Connection conn) throws QDevelopException{
		try {
			return updateDatabase(query, conn,conn.getAutoCommit()) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 
	 *  更新数据库；框架提供整体事务控制，提供链接关闭功能
	 *
	 * @param query
	 * @param conn
	 * @param isAutoCommit 是否自动事务控制
	 * @return
	 * @throws QDevelopException
	 */
	public boolean updateDatabase(Map<String,?> query,Connection conn,boolean isAutoCommit) throws QDevelopException{
		long s = System.currentTimeMillis();
		String index = String.valueOf(query.get("index"));
		try {
			formatterParameters(query);
			List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks((String)query.get("index"));
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.init(conn,query);
				}
			}
			IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(query, conn);
			boolean r =  new DatabaseImpl().updateDB(conn, dbUpdate , updateHooks, isAutoCommit);
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.flush(conn,query, dbUpdate);
				}
				updateHooks.clear();
			}
			dbUpdate.clear();
			dbUpdate = null;
			return r;
		} catch (QDevelopException e) {
			throw e;
		}finally{
			query.clear();
			log.info("updateDatabase:"+index+" use:"+(System.currentTimeMillis()-s)+"ms");
		}
	}
	
	/**
	 * 插入并返回自增ID的方法
	 * @param query
	 * @return
	 * @throws QDevelopException
	 */
	public int insertDBReturnAutoID(Map<String,?> query) throws QDevelopException{
		Connection conn = null;
		try {
			conn = getConnectByQuery(query);
			return insertDBReturnAutoID(query,conn);
		} catch (QDevelopException e) {
			throw e;
		}finally{
			ConnectFactory.close(conn);
		}
	}
	
	/**
	 * 插入并返回自增ID的方法;
	 * @param query
	 * @param conn
	 * @return
	 * @throws QDevelopException
	 */
	public int insertDBReturnAutoID(Map<String,?> query,Connection conn) throws QDevelopException{
		long s = System.currentTimeMillis();
		String index = String.valueOf(query.get("index"));
		try {
			formatterParameters(query);
			List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks((String)query.get("index"));
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.init(conn,query);
				}
			}
			IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(query, conn);
			int r =  new DatabaseImpl().insertDBReturnAutoID(conn, dbUpdate.getUpdateBeans().get(0) , updateHooks, conn.getAutoCommit());
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.flush(conn,query, dbUpdate);
				}
				updateHooks.clear();
			}
			dbUpdate.clear();
			dbUpdate = null;
			return r;
		} catch (QDevelopException e) {
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QDevelopException(10001,e.getMessage(),e);
		}finally{
			query.clear();
			log.info("insertDBReturnAutoID:"+index+" use:"+(System.currentTimeMillis()-s)+"ms");

		}
	}
	
	public int updateBatch(Map<String,?> query,List<Object[]> values) throws QDevelopException{
		Connection conn = null;
		try {
			conn = this.getConnectByQuery(query);
			conn.setAutoCommit(false);
			return updateBatch(query,values,conn);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.closeConnection(conn);
		}
		return 0;
	}
	
	/**
	 * 单sql模版批量更新数据库操作
	 * @param query
	 * @param values
	 * @param conn
	 * @return
	 * @throws QDevelopException
	 */
	public int updateBatch(Map<String,?> query,List<Object[]> values,Connection conn) throws QDevelopException{
		long s = System.currentTimeMillis();
		String index = String.valueOf(query.get("index"));
		try {
			formatterParameters(query);
			List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks((String)query.get("index"));
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.init(conn,query);
				}
			}
			IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(query, conn);
			int r =  new DatabaseImpl().singleBatchUpdate(conn, dbUpdate.getUpdateBeans().get(0),values,updateHooks, conn.getAutoCommit());
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.flush(conn,query, dbUpdate);
				}
				updateHooks.clear();
			}
			dbUpdate.clear();
			dbUpdate = null;
			return r;
		} catch (QDevelopException e) {
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QDevelopException(1001,e.getMessage(),e);
		}finally{
			query.clear();
			log.info("updateBatch:"+index+" use:"+(System.currentTimeMillis()-s)+"ms");
		}
	}

	/**
	 * @deprecated
	 * 更新数据库；框架只提供模版执行的方法,数据库链接、数据库事务、请求对象等需要自己关闭或清除
	 * @param query
	 * @param conn
	 * @return
	 * @throws QDevelopException
	 */
	public boolean updateDatabaseSelfControl(Map<String,?> query,Connection conn) throws QDevelopException{
		try {
			formatterParameters(query);
			List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks((String)query.get("index"));
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.init(conn,query);
				}
			}
			IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(query, conn);
			boolean r =  new DatabaseImpl().updateDB(conn, dbUpdate , updateHooks, conn.getAutoCommit());
			if(updateHooks!=null){
				for(IUpdateHook iuh : updateHooks){
					iuh.flush(conn,query, dbUpdate);
				}
				updateHooks.clear();
			}
			dbUpdate.clear();
			dbUpdate = null;
			return r;
		} catch (QDevelopException e) {
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QDevelopException(1001,e.getMessage(),e);
		}
	}

	/**
	 * 更新数据库；多index请求，事务控制
	 * @param querys
	 * @return
	 * @throws QDevelopException
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public boolean updateDataBaseMulti(Map<String,?> ... query) throws QDevelopException{
		long start = System.currentTimeMillis();
		StringBuffer indexs = new StringBuffer();
		
		HashMap<String,Connection> cache = new HashMap<String,Connection>(query.length);
		HashMap<String,List<IUpdateHook>> hooks = new HashMap<String,List<IUpdateHook>>(query.length);
		HashMap<String,IDBUpdate> IDBUpdates = new HashMap<String,IDBUpdate>(query.length);
		try {
			for(Map<String,?> q : query){
				String index = (String)q.get("index");
				indexs.append(",").append(index);
				if(index==null)throw new QDevelopException(1002,"请求没有index");
				if(cache.get(index)==null){
					Element config = SQLConfigLoader.getInstance().getSQLConfig(index);
					if(config==null)throw new QDevelopException(1003,"["+index+"] SQL配置【"+index+"】不存在");
					Connection conn = ConnectFactory.getInstance(config.attributeValue("connect")).getConnection();
					conn.setAutoCommit(false);
					cache.put(index, conn);
				}
			}

			for(Map<String,?> q : query){
				formatterParameters(q);
				String index = (String)q.get("index");
				Connection conn = cache.get(index);
				List<IUpdateHook> updateHooks = SQLConfigParser.getInstance().getUpdateHooks(index);
				if(updateHooks!=null){
					for(IUpdateHook iuh : updateHooks){
						iuh.init(conn,q);
					}
				}
				hooks.put(index, updateHooks);
				IDBUpdate dbUpdate = SQLConfigParser.getInstance().getDBUpdateBean(q, conn);
				IDBUpdates.put(index, dbUpdate);
				if(!new DatabaseImpl().updateDB(conn, dbUpdate , updateHooks, false)){
					throw new QDevelopException(10001,QString.append(q.toString(),"更新失败！"));
				}
			}
			Collection<Connection> itors = cache.values();
			for(Connection conn : itors){
				try {
					conn.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			for(Map<String,?> q : query){
				String index = (String)q.get("index");
				List<IUpdateHook> updateHooks = hooks.get(index);
				if(updateHooks!=null && updateHooks.size() > 0){
					Connection conn = cache.get(index);
					IDBUpdate dbUpdate = IDBUpdates.get(index);
					for(IUpdateHook iuh : updateHooks){
						iuh.flush(conn,q, dbUpdate);
					}
				}
			}

		}  catch (Exception e) {
			Collection<Connection> itors = cache.values();
			for(Connection conn : itors){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new QDevelopException(1001,e.getMessage(),e);
		}finally{
			Collection<Connection> itors = cache.values();
			for(Connection conn : itors){
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			for(Map<String,?> q : query){
				q.clear();
			}
			hooks.clear();
			cache.clear();
			IDBUpdates.clear();
			log.info("updateDataBaseMulti:"+indexs.toString()+" use:"+(System.currentTimeMillis()-start)+"ms");
		}
		return true;
	}



	@SuppressWarnings("unchecked")
	public void formatterParameters(Map<String,? extends Object> query) throws QDevelopException{
		String index = (String)query.get("index");
		List<IParamFormatter> paraFormatter = SQLConfigParser.getInstance().getParamFormatter(index) ;
		if(paraFormatter!=null && paraFormatter.size()>0){
			for(IParamFormatter formatter : paraFormatter){
				formatter.init();
				formatter.formatter((Map<String,Object>)query);
			}
			paraFormatter.clear();
		}
	}

	public void formatterResult(String index,IDBResult dbResult) throws QDevelopException{
		List<IResultFormatter> resultFormatters = SQLConfigParser.getInstance().getResultFormatter(index);
		if(resultFormatters!=null && resultFormatters.size()>0){
			for(IResultFormatter resultFormatter : resultFormatters){
				resultFormatter.init();
			}
			int size = dbResult.getSize();
			for(int i=0;i<size;i++){
				Map<String,Object> data = dbResult.getResult(i);
				for(IResultFormatter resultFormatter : resultFormatters){
					resultFormatter.formatter(data);
				}
			}
			for(IResultFormatter resultFormatter : resultFormatters){
				resultFormatter.flush(dbResult);
			}
			resultFormatters.clear();
		}
	}

	/**
	 * 关闭链接
	 * @param conn
	 */
	public void closeConnection(Connection conn){
		ConnectFactory.close(conn);
	}

}
