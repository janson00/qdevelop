package cn.qdevelop.service.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QServiceUitls {
	
	public static String getCookie(String key,HttpServletRequest request){
		String val = request.getHeader("Cookie");
		if(val == null || val.indexOf(key+"=")==-1){
			return null;
		}
		val = val.substring(val.indexOf(key+"=")+key.length()+1);
		if(val.indexOf(";")>-1){
			val = val.substring(0, val.indexOf(";"));
		}
		
		return val;
	}
	
	public static void setCookie(HttpServletResponse response,String key,String value,int maxAge){
		Cookie cookie = new Cookie(key,value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static Map<String,String> getParameters(HttpServletRequest request){
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
					if(i>0)tmp.append("&");
					tmp.append(value[i]);
				}
				paramMap.put(key, tmp.toString());
			}
		}
		return paramMap;
	}

	/**
	 * 输出内容
	 * @param out
	 */
	public static void output(String out,String outType,HttpServletRequest request,HttpServletResponse response){
		response.setCharacterEncoding("utf-8");
		response.setContentType(outType==null?"application/json":outType);
		
		OutputStream stream = null ;
		try {
			String encoding = request.getHeader("Accept-Encoding");
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
			
			if(request.getAttribute("__startTime")!=null){
				Long s = (Long)request.getAttribute("__startTime");
				response.setHeader("ops", String.valueOf(System.currentTimeMillis()-s));
			}
			
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
}
