package cn.qdevelop.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractService extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7445553533896016332L;
	private HttpServletResponse response;
	private HttpServletRequest request;
	protected String OUT_TYPE_JSON = "application/json";
	protected String OUT_TYPE_HTML = "text/html";
	protected String OUT_TYPE_XML = "text/xml";
	protected String OUT_TYPE_CSS = "text/css";
	
	protected abstract String execute(Map<String,String> args,StringBuffer out);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.response = response;
		this.request = request;
		StringBuffer out = new StringBuffer();
		String type = execute(getParameters(request),out);
		output(out.toString(),type);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.response = response;
		this.request = request;
		StringBuffer out = new StringBuffer();
		String type = execute(getParameters(request),out);
		output(out.toString(),type);
	}
	
	/**
	 * 输出内容
	 * @param out
	 */
	protected void output(String out,String outType){
		HttpServletResponse response = getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType(outType==null?"application/json":outType);
		OutputStream stream = null ;
		try {
			String encoding = getRequest().getHeader("Accept-Encoding");
			if (encoding != null && encoding.indexOf("gzip") != -1){  
				response.setHeader("Content-Encoding" , "gzip");  
				stream = new GZIPOutputStream(response.getOutputStream());  
			}else if (encoding != null && encoding.indexOf("compress") != -1){  
				response.setHeader("Content-Encoding" , "compress");  
				stream = new ZipOutputStream(response.getOutputStream());  
			}else {  
				stream = response.getOutputStream();  
			}
			stream.write(out.getBytes("utf-8"));
			stream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(stream!=null){
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	public Map<String,String> getParameters(HttpServletRequest request){
		Map<String,String> paramMap = new HashMap<String,String>();
		Enumeration<?> paramNames = request.getParameterNames();
		String key;
		String[] value;
		while (paramNames.hasMoreElements()) {
			key = (String) paramNames.nextElement();
			value = request.getParameterValues(key);
			if(value!=null&&value.length==1){
				paramMap.put(key, value[0]);
			}else {
				StringBuffer tmp = new StringBuffer();
				int len = value.length;
				for(int i=0;i<len;i++){
					if(i>0)tmp.append("^");
					tmp.append(value[i]);
				}
				paramMap.put(key, tmp.toString());
			}
		}
		return paramMap;
	}
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	public HttpServletRequest getRequest(){
		return request;
	}
}
