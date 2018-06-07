package cn.qdevelop.service.interfacer;

public interface IOutput {
	
	public boolean isError();
	
	public void setData(Object data);
	
	/**设置结果返回状态码**/
	public void setTag(int tag);
	
	public void setErrMsg(String ... errMsg);
	
	public void addAttr(String key,Object val);
	
	public String toString();
	
	public void setOutType(String outType);
	
	public String getOutType();
	
	public boolean isBodyOnly();
	
	public void setBodyOnly(boolean isBodyOnly);
}
