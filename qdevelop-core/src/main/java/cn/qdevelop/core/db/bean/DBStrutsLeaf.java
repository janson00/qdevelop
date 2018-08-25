package cn.qdevelop.core.db.bean;

public class DBStrutsLeaf {
	String columnName,columnTypeName;
	int size,type=0;
	boolean isNullAble,isAutoIncrement;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = clearColumnName(columnName);
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}
	private String clearColumnName(String columnName){
		if(columnName.indexOf(" ")>-1){
			return columnName.substring(0,columnName.indexOf(" "));
		}
		return columnName;
	}
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = clearColumnName(columnTypeName);

		if(this.columnTypeName.equalsIgnoreCase("int") || this.columnTypeName.equalsIgnoreCase("tinyint")){
			type = 1;
		}else if(this.columnTypeName.equalsIgnoreCase("varchar") || this.columnTypeName.equalsIgnoreCase("char")  || this.columnTypeName.equalsIgnoreCase("text")){
			type = 2;
		}else if(this.columnTypeName.equalsIgnoreCase("decimal")  || this.columnTypeName.equalsIgnoreCase("double")){
			type = 3;
		}else if(this.columnTypeName.equalsIgnoreCase("date")){
			type = 4;
		}else if(this.columnTypeName.equalsIgnoreCase("datetime")){
			type = 5;
		}else if(this.columnTypeName.equalsIgnoreCase("bigint")){
			type = 6;
		}else if(this.columnTypeName.equalsIgnoreCase("float")){
			type = 7;
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
