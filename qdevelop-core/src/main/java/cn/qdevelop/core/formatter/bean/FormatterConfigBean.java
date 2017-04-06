package cn.qdevelop.core.formatter.bean;

public class FormatterConfigBean {
	String _formatter;
	String[] params;
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public String getFormatterClass() {
		return _formatter;
	}
	public void setFormatterClass(String _formatter) {
		this._formatter = _formatter;
	}
	
}
