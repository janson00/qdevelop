package cn.qdevelop.plugin.common;

public interface IDResponse extends java.io.Serializable {
	public Long[] getValues();
	public String getMsg();
	public void setValues(int i,long v);
	public void setMsg(String msg);
	
}
