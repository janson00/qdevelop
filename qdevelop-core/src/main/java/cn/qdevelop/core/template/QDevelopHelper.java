package cn.qdevelop.core.template;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.DBStrutsBean;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.TableColumnType;

public class QDevelopHelper {
	
	
	/**
	 * 生成sql配置模版
	 * @param connName qdevelop-database.xml 中的数据库配置名
	 * @param tableName	数据库内的表名称
	 */
	public static void createSQLConfig(String connName,String tableName){
//		StringBuffer xml = new StringBuffer();
		StringBuffer selectSQL = new StringBuffer();
		StringBuffer insertSQL = new StringBuffer();
		StringBuffer updateSQL = new StringBuffer();
		StringBuffer deleteSQL = new StringBuffer();
		ArrayList<String> formatter = new ArrayList<String>();
		String cn="";
		Connection conn = null ;
		try {
			selectSQL.append("select ");
			insertSQL.append("insert into ").append(tableName).append("(");
			updateSQL.append("update ").append(tableName).append(" set ");

			conn = ConnectFactory.getInstance(connName).getConnection();
			DBStrutsBean dbb = TableColumnType.getInstance().getTableStrutsBean(conn, tableName);
			Iterator<DBStrutsLeaf> iter = dbb.values().iterator();
			int idx = 0;
			StringBuffer c = new StringBuffer();
			StringBuffer v = new StringBuffer();
			StringBuffer i = new StringBuffer();
			StringBuffer s = new StringBuffer();
			while(iter.hasNext()){
				DBStrutsLeaf sl = iter.next();
				//				sl.isAutoIncrement();
				selectSQL.append(idx>0?",":"").append(sl.getColumnName());
				if(!sl.isAutoIncrement()){
					c.append(c.length()>0?",":"").append(sl.getColumnName());
					v.append(v.length()>0?",":"").append(sl.getColumnType()==2?"'":"").append("$[").append(sl.getColumnName()).append("]").append(sl.getColumnType()==2?"'":"");
					s.append(s.length()>0?",":"").append(sl.getColumnName()).append("=").append(sl.getColumnType()==2?"'":"").append("$[").append(sl.getColumnName()).append("]").append(sl.getColumnType()==2?"'":"");
				}else{
					i.append(sl.getColumnName()).append("=$[").append(sl.getColumnName()).append("]");
				}
				if(sl.getColumnTypeName().equalsIgnoreCase("tinyint")){
					formatter.add("<prop-formatter result-key=\""+sl.getColumnName()+"\" prop-key=\""+tableName+"_"+sl.getColumnName()+"_dict\"/>");
				}else if(sl.getColumnTypeName().equalsIgnoreCase("date") || sl.getColumnTypeName().equalsIgnoreCase("datetime")){
					formatter.add("<date-formatter result-key=\""+sl.getColumnName()+"\" date-style=\"yyyy-MM-dd HH:mm:ss\"/>");
				}
				idx++;
			}
			selectSQL.append(" from ").append(tableName).append(" where {DYNAMIC}");
			updateSQL.append(s).append(" where ").append(i);
			insertSQL.append(c).append(") value (").append(v).append(")");
			deleteSQL.append("delete from ").append(tableName).append(" where ").append(i);
			cn = conn.getCatalog();
		} catch (QDevelopException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(selectSQL);
		System.out.println(insertSQL);
		System.out.println(updateSQL);
		System.out.println(deleteSQL);

		OutputStreamWriter fw=null;
		try {
			fw=new OutputStreamWriter(new  FileOutputStream(cn+"."+tableName+".sql.xml"),"utf-8");
			fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");fw.write("\r\n");
			fw.write("\r\n");fw.write("<SQLConfig>");
			fw.write("\r\n");fw.write("	<property index=\""+tableName+"-select-auto\" connect=\""+connName+"\" explain=\""+tableName+"查询\">");
			if(formatter.size()>0){
				fw.write("\r\n");fw.write("	<!--<formatter>");
				for(String fs : formatter){
					fw.write("\r\n");fw.write("			"+fs);
				}
				fw.write("\r\n");fw.write("		</formatter>-->");
			}
			fw.write("\r\n");fw.write("		<sql>"+selectSQL.toString()+"</sql>");
			fw.write("\r\n");fw.write("	</property>");
			fw.write("\r\n");fw.write("	<property index=\""+tableName+"-insert-auto\" connect=\""+connName+"\" explain=\""+tableName+"插入\">");
			fw.write("\r\n");fw.write("		<sql>"+insertSQL.toString()+"</sql>");
			fw.write("\r\n");fw.write("		<!--<sql>insert other_table("+tableName+"_id) value({"+tableName+".LAST_INSERT_ID})</sql>-->");
			fw.write("\r\n");fw.write("	</property>");
			fw.write("\r\n");fw.write("	<property index=\""+tableName+"-update-auto\" connect=\""+connName+"\" explain=\""+tableName+"修改\">");
			fw.write("\r\n");fw.write("		<sql>"+updateSQL.toString()+"</sql>");
			fw.write("\r\n");fw.write("	</property>");
			fw.write("\r\n");fw.write("	<property index=\""+tableName+"-delete-auto\" connect=\""+connName+"\" explain=\""+tableName+"删除\">");
			fw.write("\r\n");fw.write("		<sql>"+deleteSQL.toString()+"</sql>");
			fw.write("\r\n");fw.write("	</property>");
			fw.write("\r\n");fw.write("</SQLConfig>");
			fw.flush();
		} catch (Exception e) {
		}finally{
			try {
				if(fw!=null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
