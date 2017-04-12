package cn.qdevelop.service;

public interface IOutput {
	
	public void setData(Object data);
	
	/**设置结果返回状态码**/
	public void setTag(int tag);
	
	public void setErrMsg(String errMsg);
	
	public void addAttr(String key,Object val);
	
	public String toString();
	
	/**
	 * toString之后，前后包裹特定字符
	 * @param startString
	 * @param endString
	 * @return
	 */
	public String wrapper(String startString,String endString);
	
}
