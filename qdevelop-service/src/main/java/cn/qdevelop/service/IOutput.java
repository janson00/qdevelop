package cn.qdevelop.service;

public interface IOutput {
	
	public void setData(Object data);
	
	/**设置结果返回状态码**/
	public void setTag(int tag);
	
	public void setErrMsg(String errMsg);
	
	public void addAttr(String key,Object val);
	
	public String toString();
	
}
