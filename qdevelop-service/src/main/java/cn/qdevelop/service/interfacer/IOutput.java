package cn.qdevelop.service.interfacer;

public interface IOutput {
	public static final int  
	/**极简模式**/
	MINI_TYPE = 2 ,
	/**简单模式**/
	SIMPLE_TYPE = 1 ,
	/**正常模式**/
	NORMAL_TYPE = 0; 

	public boolean isError();

	public void setData(Object data);

	/**设置结果返回状态码**/
	public void setTag(int tag);

	public void setErrMsg(String ... errMsg);

	public void addAttr(String key,Object val);

	public String toString();

	public void setOutType(String outType);

	public String getOutType();

	public int getFormatType();

	public void setFormatType(int type);


}
