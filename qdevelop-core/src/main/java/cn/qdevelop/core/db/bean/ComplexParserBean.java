package cn.qdevelop.core.db.bean;

public class ComplexParserBean{
	String[] values;
	String[] column;
	String key;
	String parseVale;
	String parseFullValue;
	public String getParseFullValue() {
		return parseFullValue;
	}
	public void setParseFullValue(String parseFullValue) {
		this.parseFullValue = parseFullValue;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	public String[] getColumn() {
		return column;
	}
	public void setColumn(String[] column) {
		this.column = column;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getParseVale() {
		return parseVale;
	}
	public void setParseVale(String parseVale) {
		this.parseVale = parseVale;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(key).append(":").append(parseVale);
		if(column!=null){
			sb.append(" {");
			for(int i=0;i<column.length;i++){
				if(i>0)sb.append(",");
				sb.append(column[i]).append(":").append(values[i]);
			}
			sb.append("}");
		}
		return sb.append(" full:").append(parseFullValue).toString();
	}
	
}
