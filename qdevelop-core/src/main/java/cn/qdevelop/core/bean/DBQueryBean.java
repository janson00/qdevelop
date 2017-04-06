package cn.qdevelop.core.bean;


import java.util.Map;

import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.bean.PagenationBean;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IPagenation;

public class DBQueryBean implements IDBQuery{
	
	private IPagenation pagenation;
	private String index,connName,order;
	private String sql,preparedSql;
	private String[] columns;
	private Object[] values;
	private Map<String, DBStrutsLeaf> tableStruts ;
	


	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public String getSql() {
		return sql + this.getOrder() + (pagenation==null?"":pagenation.getPagenationInfo());
	}

	@Override
	public void setPage(int page, int pageNum) {
		pagenation = new PagenationBean();
		pagenation.setPage(page);
		pagenation.setPageNum(pageNum);
	}
	
//	private static Pattern hasPaginationCondition = Pattern.compile(" limit ", Pattern.CASE_INSENSITIVE);

	@Override
	public String getPreparedSql() {
		return preparedSql + this.getOrder() + (pagenation==null?"":pagenation.getPagenationInfo());
	}

	@Override
	public String[] getPreparedColumns() {
		return columns;
	}

	@Override
	public Object[] getPreparedValues() {
		return values;
	}

	@Override
	public Map<String, DBStrutsLeaf> getTableStruts() {
		return tableStruts;
	}


	public void setIndex(String index) {
		this.index = index;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public void setPreparedSql(String preparedSql) {
		this.preparedSql = preparedSql;
	}
	
	public void setTableStruts(Map<String, DBStrutsLeaf> tableStruts) {
		this.tableStruts = tableStruts;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}
	
//	private static Pattern hasOrderCondition = Pattern.compile(" order by ", Pattern.CASE_INSENSITIVE);
	public String getOrder() {
		return order == null  ? "" : " order by "+order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(index).append(":").append(connName).append("\n 预编译：").append(this.getPreparedSql()).append("\n");
		if(columns!=null){
			sb.append(" {");
			for(int i=0;i<columns.length;i++){
				if(i>0)sb.append(",");
				sb.append(columns[i]).append(":").append(values[i]);
			}
			sb.append("}");
		}
		return sb.append("\n 明文sql:").append(this.getSql()).toString();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
