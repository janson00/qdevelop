package cn.qdevelop.core.formatter.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class LeftJoinMultiFormatter extends AbstractResultFormatter implements IResultFormatter{
	String formatterIndex,formatterKey,resultKey,nodeName="child";
	String[] formatterColumns;
	Integer limit = null;

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			formatterIndex = conf.attributeValue("left-join");
			formatterKey = conf.attributeValue("on-key");
			formatterColumns = conf.attributeValue("columns").split(",");
			resultKey =  conf.attributeValue("result-key");
			if(conf.attributeValue("node-name")!=null){
				nodeName = conf.attributeValue("node-name");
			}
			if(conf.attributeValue("limit")!=null){
				limit = Integer.parseInt(conf.attributeValue("limit"));
			}
		}
	}

	public HashSet<String> conditions;

	@Override
	public boolean isQBQuery() {
		return true;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		Object val = data.get(resultKey);
		//		StringUtils.isEmpty(String.valueOf(val));
		if(val!=null&&String.valueOf(val).length()>0){
			conditions.add(String.valueOf(val));
		}
	}

	@Override
	public void flush(IDBResult result)  throws QDevelopException{
		if(result.getSize()==0)return;
		Connection conn = null;
		try {
			Map<String,Object> query = new HashMap<String,Object>();
			query.put("index", formatterIndex);
			StringBuilder sb = new StringBuilder();
			//		synchronized(conditions){
			Iterator<String> itor = conditions.iterator();
			while(itor.hasNext()){
				sb.append("|").append(itor.next());
			}
			//		System.out.println(">>>>>>> "+sb.toString());
			if(sb.length()==0)return;
			query.put(formatterKey, sb.substring(1));
			//		query.put("page", 1);
			//		query.put("limit","50000");

			conn = DatabaseFactory.getInstance().getConnectByQuery(query);
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);
			IDBResult formatterResult = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, formatterResult);
			int size  = formatterResult.getSize();
			if(size==0)return;
			DatabaseFactory.getInstance().formatterResult(formatterIndex, formatterResult);
			//			System.out.println(JSONObject.toJSONString(formatterResult));	
			HashMap<String,List<Map<String,Object>>> tmp = new HashMap<String,List<Map<String,Object>>>();
			for(int i=0;i<size;i++){
				Map<String,Object> data = formatterResult.getResult(i);
				String key = String.valueOf(data.get(formatterKey));
				List<Map<String,Object>> array = tmp.get(key);
				if(array==null){
					array = new ArrayList<Map<String,Object>>();
					tmp.put(key, array);
				}

				if(limit!=null && array.size() >= limit){
					continue;
				}

				Map<String,Object> fr = new HashMap<String,Object>();
				for(String k : formatterColumns){
					String[] _t = k.split(" as ");
					String column, rname;
					if(_t.length==1){
						column = _t[0].trim();
						rname = _t[0].trim();
					}else if(_t.length==2){
						column = _t[0].trim();
						rname = _t[1].trim();
					}else{
						throw new QDevelopException(10001, "key-value-formatter 参数设置错误："+k);
					}
					fr.put(rname, data.get(column));
					Object tranVal = data.get("__"+column);
					if(tranVal!=null){
						fr.put("__"+rname, tranVal);
					}
				}
				if(data.get(nodeName)!=null){
					fr.put(nodeName,data.get(nodeName));
				}
				array.add(fr);
			}
			size = result.getSize();
			for(int i = 0; i < size;i++){
				Map<String,Object> data = result.getResult(i);
				String targetValue = String.valueOf(data.get(resultKey));
				List<Map<String,Object>> fr = tmp.get(targetValue);
				if(fr!=null){
					data.put(nodeName, fr);
				}
			}
			tmp = null;
			formatterResult = null;
		} catch (Exception e) {
			throw e;
		}finally{
			conditions.clear();
			try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//		}
	}

	public void testAdd(String val){
		conditions.add(val);
	}


	@Override
	public void init() {
		conditions = new HashSet<String>();
	}
}
