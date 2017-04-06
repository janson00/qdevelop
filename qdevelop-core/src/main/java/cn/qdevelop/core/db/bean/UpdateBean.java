package cn.qdevelop.core.db.bean;

public class UpdateBean{
	
	String connName,index;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	String tableName;
	String preparedSql;
	String fullSql;
	DBStrutsBean dbsb;
	String[] columns;
	Object[] values;
	boolean isInsert;
	boolean isFetchZeroError;
	
	public String getConnName() {
		return connName;
	}
	public void setConnName(String connName) {
		this.connName = connName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPreparedSql() {
		return preparedSql;
	}
	public void setPreparedSql(String preparedSql) {
		this.preparedSql = preparedSql;
	}
	
	public DBStrutsBean getDbsb() {
		return dbsb;
	}
	public void setDbsb(DBStrutsBean dbsb) {
		this.dbsb = dbsb;
	}
	public String[] getColumns() {
		return columns;
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
	public boolean isInsert() {
		return isInsert;
	}
	public void setInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}
	
	
	public boolean isFetchZeroError() {
		return isFetchZeroError;
	}
	public void setFetchZeroError(boolean isFetchZeroError) {
		this.isFetchZeroError = isFetchZeroError;
	}
//	public String getUpdateSql(){
//		String s = new String(preparedSql);
//		for(Object val : values){
//			s = s.replace("?", String.valueOf(val));
//		}
//		return s;
//	}
	public String getFullSql() {
		return fullSql;
	}
	public void setFullSql(String fullSql) {
		this.fullSql = fullSql;
	}
	

}
