package cn.qdevelop.core.db.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.DBStrutsBean;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.connect.ConnectFactory;

public class TableColumnType  extends ConcurrentHashMap<String, DBStrutsBean>{

	private static  TableColumnType _TableColumnType = new TableColumnType();
	public static TableColumnType getInstance(){return _TableColumnType;}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Pattern clear_brackets = Pattern.compile("\\([0-9|,]+\\)");
	protected static Pattern clear_brackets2 = Pattern.compile("^.*\\(|\\)");
	protected static Pattern isNumber = Pattern.compile("^[0-9]+$");
	protected static Pattern isTableName = Pattern.compile("^[a-zA-Z0-9|\\_]+$");
	private final static Map<String,String> connNameRelation = new ConcurrentHashMap<String,String>();


	public Map<String,DBStrutsLeaf> getTablesStrutsBean(String connName,String ... tables) {
		Map<String,DBStrutsLeaf> result = new HashMap<String,DBStrutsLeaf>();

		Connection conn = null;
		try {
			String catalog = connNameRelation.get(connName);
			if(catalog == null){
				conn = ConnectFactory.getInstance(connName).getConnection();
				catalog = conn.getCatalog();
				connNameRelation.put(connName, catalog);
			}
			for(String tableName : tables){
				if(!isTableName.matcher(tableName).find()){
					continue;
				}
				DBStrutsBean dbsb  = this.get(catalog+"_"+tableName);
				if(dbsb == null){
					if(conn==null){
						conn = ConnectFactory.getInstance(connName).getConnection();
					}
					dbsb = getTableStrutsBean(conn,tableName);
				}
				Iterator<Entry<String, DBStrutsLeaf>> iter = dbsb.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry<String,DBStrutsLeaf> entry = (Map.Entry<String,DBStrutsLeaf>) iter.next();
					String key = entry.getKey();
					DBStrutsLeaf sl = entry.getValue();
					result.put(key, sl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnectFactory.close(conn);
		}
		return result;
	}

	public DBStrutsBean getTableStrutsBean(Connection conn,String tableName) throws QDevelopException, SQLException{
		String connect = conn.getCatalog();
		DBStrutsBean _DBStrutsBean  = this.get(connect+"_"+tableName);
		if(_DBStrutsBean!=null){
			return _DBStrutsBean;
		}
		_DBStrutsBean = new DBStrutsBean();
		if(isTableName.matcher(tableName).find()){
			ResultSet rs = null ;
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("desc "+tableName,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
				rs = ps.executeQuery();
				while(rs.next()){
					DBStrutsLeaf sl = new DBStrutsLeaf();
					sl.setColumnName(rs.getString(1));
					sl.setColumnTypeName(clear_brackets.matcher(rs.getString(2)).replaceAll("").toLowerCase());
					sl.setNullAble(Boolean.parseBoolean(rs.getString(3)));
					String size = clear_brackets2.matcher(rs.getString(2)).replaceAll("");
					if(isNumber.matcher(size).find()){
						sl.setSize(size.length() > 0 ? Integer.parseInt(size) : Integer.MAX_VALUE);
					}else{
						sl.setSize(0);
					}
					String extra = rs.getString(6);
					sl.setAutoIncrement(extra!=null&&extra.equals("auto_increment"));
					_DBStrutsBean.addStruts(sl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs!=null)rs.close();
					if (ps != null)	ps.close();
				} catch (Exception e) {
				}
			}
		}
		this.put(connect+"_"+tableName, _DBStrutsBean);
		return _DBStrutsBean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,DBStrutsLeaf> getTablesStrutsBean(Connection conn,String ... tables) throws QDevelopException, SQLException{
		Map<String,DBStrutsLeaf> result = new HashMap<String,DBStrutsLeaf>();
		for(String t : tables){
			if(isTableName.matcher(t).find()){
				DBStrutsBean dbsb = getTableStrutsBean(conn,t);
				Iterator iter = dbsb.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry<String,DBStrutsLeaf> entry = (Map.Entry<String,DBStrutsLeaf>) iter.next();
					String key = entry.getKey();
					DBStrutsLeaf sl = entry.getValue();
					result.put(key, sl);
				}
			}
		}
		return result;
	}




	//	public static void main(String[] args)  {
	//		Pattern isTableName = Pattern.compile("^[a-zA-Z0-9|\\_]+$");
	//		String[] test = new String[]{"a1_1","aSd","a a","aA-s"};
	//		for(String t:test)
	//			System.out.println(isTableName.matcher(t).find());
	//	}
	//		 try {
	//			 Connection conn = ConnectFactory.getInstance("ep_cash_amount_apply_log").getConnection();
	//			DBStrutsBean sb = TableColumnType.getInstance().getTableStrutsBean(conn, "ep_account");
	//			sb.print();
	//			conn.close();
	//		} catch (QDevelopException e) {
	//			e.printStackTrace();
	//		} catch (SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		 
	//		 
	//	}
	//				try {
	//					
	//					String connectName = "ep_account";
	//					DBStrutsBean sb = TableColumnType.getInstance().getDBStrutsBean("ep_cash_amount_apply_log", connectName);
	//					Connection conn = ConnectFactory.getInstance(connectName).getConnection();
	//					PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	//					String[] keys = "ecaa_id,status,content,uid,ctime".split(",");
	//					Object[] values;
	//					values = new Object[]{
	//							3,"1",1231412,10,"2016-11-09"
	//					};
	////					
	//					TableColumnType.getInstance().setValue(sb, pstmt, keys, values);
	//					pstmt.executeUpdate();
	//					ResultSet rs=pstmt.getGeneratedKeys();
	//					if(rs.next()){
	//						System.out.println(rs.getInt(1));
	//					}
	////					pstmt.addBatch();
	////					values = new Object[]{
	////							4,100,"sdasd",10,"2016-11-09 11:23:34"
	////					}; 
	////					TableColumnType.getInstance().setValue(sb, pstmt, keys, values);
	////					pstmt.addBatch();
	//					
	//					pstmt.executeBatch();
	//					pstmt.close();
	//					conn.close();
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//	}


}
