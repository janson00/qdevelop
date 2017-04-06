package cn.qdevelop.service;

public interface IService {
	/**输出json或者js文件**/
	public String RETURN_OUT_JSON = "application/json";
	/**输出HTML文件格式**/
	public String RETURN_OUT_HTML = "text/html";
	/**数据xml文件数据**/
	public String RETURN_OUT_XML = "text/xml";
	/**输出css文件数据**/
	public String RETURN_OUT_CSS = "text/css";
	
	/**验证必须参数**/
	public String INIT_VALID_REQUIRED = "valid_required";
	/**验证忽略参数**/
	public String INIT_VALID_IGNORE = "valid_ignore";
}
