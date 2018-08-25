package cn.qdevelop.core.formatter.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.core.standard.IResultFormatter;

public class LeftJoinFormatter extends AbstractResultFormatter implements IResultFormatter{
	String formatterIndex,formatterKey,resultKey;
	String[] formatterColumns;

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			formatterIndex = conf.attributeValue("left-join");
			formatterKey = conf.attributeValue("on-key");
			formatterColumns = conf.attributeValue("columns").split(",");
			resultKey =  conf.attributeValue("result-key");
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
		if(val!=null){
			conditions.add(String.valueOf(val));
		}
	}

	@Override
	public void flush(IDBResult result)  throws QDevelopException{
		if(result.getSize()==0)return;
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("index", formatterIndex);
		StringBuilder sb = new StringBuilder();
		for(String v : conditions){
			sb.append("|").append(v);
		}
		if(sb.length()==0)return;
		query.put(formatterKey, sb.substring(1));
		query.put("page", 1);
		query.put("limit", conditions.size());
		Connection conn = null;
		try {
			conn = DatabaseFactory.getInstance().getConnectByQuery(query);
			IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(query, conn);

			IDBResult formatterResult = new DBResultBean();
			new DatabaseImpl().queryDB(conn, dbQuery, formatterResult);
			int size;
			HashMap<String,Map<String,Object>> tmp = new HashMap<String,Map<String,Object>>();
			if(formatterResult.getSize()>0){
				DatabaseFactory.getInstance().formatterResult(formatterIndex, formatterResult);
				size  = formatterResult.getSize();
				Map<String,Object> dataTemple = result.getResult(0);
				for(int i=0;i<size;i++){
					Map<String,Object> data = formatterResult.getResult(i);
					String key = String.valueOf(data.get(formatterKey));
					Map<String,Object> fr = new HashMap<String,Object>();
					for(String k : formatterColumns){
						String[] _t = k.split(" as ");//XXX 兼容配置中加“|”和“>”来自定义格式化之后的别名
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
						if(dataTemple.get(rname)!=null){
							rname = "__"+rname;
						}
						fr.put(rname, data.get(column));

						Object tranVal = data.get("__"+column);
						if(tranVal!=null){
							fr.put("__"+rname, tranVal);
						}
					}
					tmp.put(key, fr);
				}
			}
			size = result.getSize();
			for(int i = 0; i < size;i++){
				Map<String,Object> data = result.getResult(i);
				String targetValue = String.valueOf(data.get(resultKey));
				Map<String,Object> fr = tmp.get(targetValue);
				if(fr == null && dbQuery.isConvertNull()){
					fr = new HashMap<String,Object>();
					for(String k : formatterColumns){
						String[] _t = k.split(" as ");//XXX 兼容配置中加“|”和“>”来自定义格式化之后的别名
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
						DBStrutsLeaf  sl = dbQuery.getTableStruts().get(column);
						Object val=null;
						switch(sl.getColumnType()){
						case 1:
						case 3:
						case 6:
						case 7:
							val = -1;
							break;
						case 4:
						case 5:
							//break;
						default:
							val = "";
						}
						fr.put(rname, val);
					}
				}
				if(fr!=null){
					data.putAll(fr);
				}
			}
			dbQuery = null;
			tmp.clear();
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

	}

	public void testAdd(String val){
		conditions.add(val);
	}


	@Override
	public void init() {
		conditions = new HashSet<String>();
	}
}
