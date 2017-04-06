package cn.qdevelop.core.db.bean;

public class DBStrutsLeaf {
	String columnName,columnTypeName;
	int size,type=0;
	boolean isNullAble,isAutoIncrement;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnTypeName() {
		return columnTypeName;
	}
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
		
		if(columnTypeName.equalsIgnoreCase("int") || columnTypeName.equalsIgnoreCase("tinyint")){
			type = 1;
		}else if(columnTypeName.equalsIgnoreCase("varchar") || columnTypeName.equalsIgnoreCase("char")  || columnTypeName.equalsIgnoreCase("text")){
			type = 2;
		}else if(columnTypeName.equalsIgnoreCase("decimal")  || columnTypeName.equalsIgnoreCase("double")){
			type = 3;
		}else if( columnTypeName.equalsIgnoreCase("date")){
			type = 4;
		}else if(columnTypeName.equalsIgnoreCase("datetime")){
			type = 5;
		}else if(columnTypeName.equalsIgnoreCase("bigint") || columnTypeName.equalsIgnoreCase("float")){
			type = 6;
		}
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isNullAble() {
		return isNullAble;
	}
	public void setNullAble(boolean isNullAble) {
		this.isNullAble = isNullAble;
	}
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}
	
	public int getColumnType(){
		return type;
	}
	
}
