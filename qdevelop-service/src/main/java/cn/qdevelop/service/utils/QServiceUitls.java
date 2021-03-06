package cn.qdevelop.service.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.core.db.config.SQLConfigLoader;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.TableColumnType;
import cn.qdevelop.core.standard.IParamFormatter;
import cn.qdevelop.service.bean.OutputJson;
import cn.qdevelop.service.interfacer.IOutput;

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
	
	private static Pattern isDomain = Pattern.compile("^.+\\..+\\..+$"); 

	public static void setCookie(HttpServletRequest request,HttpServletResponse response,String key,String value,int maxAge){
		Cookie cookie = new Cookie(key,value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		String domain = request.getServerName();
		if(isDomain.matcher(domain).find()){
			domain = domain.substring(domain.indexOf(".")+1);
		}
//		System.out.println("cookie domain >>> "+request.getServerName()+" ---> "+domain);
		cookie.setDomain(domain);
		
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
				paramMap.put(key, value[0].trim());
			}else {
				StringBuffer tmp = new StringBuffer();
				int len = value.length;
				for(int i=0;i<len;i++){
					if(i>0)tmp.append("&");
					tmp.append(value[i]);
				}
				paramMap.put(key, tmp.toString().trim());
			}
		}
		return paramMap;
	}

	public static IOutput getOutput(HttpServletRequest request,HttpServletResponse response){
		String uri = request.getRequestURI();
		String type = uri.lastIndexOf(".") == -1 ? "" : uri.substring(uri.lastIndexOf("."));
		if(type.equals(".json")){
			OutputJson oj = new OutputJson();
			oj.setOutType("application/json");
			return oj;
		}else if(type.equals(".jsonp")){
			OutputJson oj = new OutputJson();
			String callback =  request.getParameter("callback");
			if(callback==null || callback.length()==0){
				oj.setErrMsg("请求参数[callback]不能为空");
			}
			oj.setJsonpCallback(callback);
			return oj;
		}
		return new OutputJson();
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


	private static Pattern isInteger = Pattern.compile("^(([><=&\\^!\\|]+)?[0-9]+?)+?$");
	private static Pattern isDouble = Pattern.compile("^(([><=&\\^!\\|]+)?[0-9]+?\\.[0-9]+?)+?$");
	private static Pattern isTime = Pattern.compile("^(([><=&\\^!\\|]+)?[0-9]{4}-[0-9]{2}-[0-9]{2}( [0-9]{2}:[0-9]{2}:[0-9]{2})?)+?$");

	private static Pattern isAttackValue =
			Pattern.compile(
					"(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|delete|insert|trancate|char|substr|ascii|declare|exec|master|into|drop|execute)\\b)",
					Pattern.CASE_INSENSITIVE);

	/**
	 * 进行参数进行注入检查和数值类型校验；仅当args中含有index索引值时，才会根据数据库中，数据表中字段类型和当前参数进行类型校验
	 * @param args
	 * @return
	 */
	public  boolean validParameters(Map<String,String> args,IOutput out,String[] checkColumns,String[] ignoreColumns ){
		if(checkColumns!=null){
			for(String s : checkColumns){
				String v = args.get(s);
				if(v==null||v.length()==0){
					out.setErrMsg("请求参数[",s,"]不能为空");
					return false;
				}
			}
		}
		String index = args.get("index");
		if(index!=null){
			try {
				String[] nullArgsKey = getCheckArgsByIndex(index);
				if(nullArgsKey != null){
					for(int i=0;i<nullArgsKey.length;i++){
						String v = args.get(nullArgsKey[i]);
						if(v==null||v.length()==0){
							out.setErrMsg("请求参数[",nullArgsKey[i],"]不能为空");
							return false;
						}
					}
				}
				Map<String,DBStrutsLeaf> struts = getDBStrutsLeafByIndex(index);
				Iterator<Entry<String,String>> iter = args.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String,String> itor = iter.next();
					if(ignoreColumns!=null && ArrayUtils.contains(ignoreColumns, itor.getKey())){
						continue;
					}
					DBStrutsLeaf dsf = struts.get(itor.getKey());
					String val = itor.getValue();
					if(dsf!=null){
						boolean isRightValue = true;
						if(dsf!=null){
							switch(dsf.getColumnType()){
							case 1:
							case 6:
								isRightValue  = isInteger.matcher(val).find();
								break;
							case 3:
								isRightValue = isDouble.matcher(val).find();
								break;
							case 4:
							case 5:
								isRightValue = isTime.matcher(val).find();
								break;
							default:
								isRightValue = !isAttackValue.matcher(val).find();	
							}
						}
						if(!isRightValue){
							out.setErrMsg("请求参数[",itor.getKey(),"=",val,"]数据类型不合法");
							return false;
						}else if(dsf.getSize() > 0 && val.length() > dsf.getSize()){
							out.setErrMsg("请求参数[",itor.getKey(),"=",val,"]数据超长");
							return false;
						}
					}else if(isAttackValue.matcher(val).find()){
						out.setErrMsg("请求参数[",itor.getKey(),"=",val,"]可能含有恶意字符");
						return false;
					}
				}
			} catch (QDevelopException e) {
				e.printStackTrace();
			}
		}else{
			Iterator<Entry<String,String>> iter = args.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,String> itor = iter.next();
				if(ignoreColumns!=null && ArrayUtils.contains(ignoreColumns, itor.getKey())){
					continue;
				}
				if(isAttackValue.matcher(itor.getValue()).find()){
					out.setErrMsg("请求参数[",itor.getKey(),"=",itor.getValue(),"]可能含有恶意字符");
					return false;
				}
			}
		}
		return true;
	}

	static Pattern isIP = Pattern.compile("^[0-9]+?\\.[0-9]+?\\.[0-9]+?\\.[0-9]+?$");
	public static String getUserIP(HttpServletRequest request){
		String ip = request.getHeader("X-Real-IP"); 
		if(ip != null && isIP.matcher(ip).find() & !"127.0.0.1".equals(ip)){
			return ip;
		}
		ip =  request.getHeader("X-Forwarded-For");
		if(ip != null && isIP.matcher(ip).find() & !"127.0.0.1".equals(ip)){
			return ip;
		}
		ip =  request.getHeader("Proxy-Client-IP");
		if(ip != null && isIP.matcher(ip).find() & !"127.0.0.1".equals(ip)){
			return ip;
		}
		ip =   request.getHeader("WL-Proxy-Client-IP");
		if(ip != null && isIP.matcher(ip).find() & !"127.0.0.1".equals(ip)){
			return ip;
		}
		ip =   request.getRemoteAddr();
		return ip;
	}

	private final static Map<String,Map<String,DBStrutsLeaf>> dbStrutsLeafByIndex = new ConcurrentHashMap<String,Map<String,DBStrutsLeaf>>();
	private final static Map<String,String[]> getCheckArgsByIndexCache = new ConcurrentHashMap<String,String[]>();


	public Map<String,DBStrutsLeaf> getDBStrutsLeafByIndex(String index) throws QDevelopException {
		Element config = SQLConfigLoader.getInstance().getSQLConfig(index);
		if(config == null){
			throw new QDevelopException(1002,"请求index【"+index+"】配置不存在");
		}
		Map<String,DBStrutsLeaf> temp = dbStrutsLeafByIndex.get(index);
		if(temp!=null){
			return temp;
		}
		temp = new HashMap<String,DBStrutsLeaf>();
		Connection conn = null;
		try {
			conn = ConnectFactory.getInstance(config.attributeValue("connect")).getConnection();
			@SuppressWarnings("unchecked")
			List<Element> sqls = config.elements("sql");
			for(Element sql : sqls){
				String[] tableName = sql.attributeValue("tables").split("\\|");
				temp.putAll(TableColumnType.getInstance().getTablesStrutsBean(conn, tableName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ConnectFactory.close(conn);
		}
		dbStrutsLeafByIndex.put(index, temp);
		return temp;
	}

	public String[] getCheckArgsByIndex(String index) throws QDevelopException {
		String[] r = getCheckArgsByIndexCache.get(index);
		if(r!=null){
			return r;
		}

		Element config = SQLConfigLoader.getInstance().getSQLConfig(index);
		if(config==null)throw new QDevelopException(1002,"请求index【"+index+"】配置不存在");
		@SuppressWarnings("unchecked")
		List<Element> sqls = config.elements("sql");
		HashSet<String> args = new HashSet<String>();
		for(int i=0;i<sqls.size();i++){
			Element ele = sqls.get(i);
			if(ele.attributeValue("is-full-param").equals("true")){
				String params = ele.attributeValue("params");
				String repeat = ele.attributeValue("repeat");
				if(repeat!=null&&repeat.length()>0){
					args.add(repeat);
					if(params!=null&&params.length()>0){
						String repeatSplit = ele.attributeValue("repeat-split");
						Pattern clear = Pattern.compile("\\b("+repeat.replaceAll(repeatSplit, "|")+")\\b");
						params = clear.matcher(params).replaceAll("");
					}
				}
				if(params!=null&&params.length()>0){
					String[] tmp = params.split("\\|");
					for(int j=0;j<tmp.length;j++){
						if(tmp[j].length()>0){
							args.add(tmp[j]);
						}
					}
				}
			}
		}
		List<IParamFormatter> paramFormatter = SQLConfigParser.getInstance().getParamFormatter(index);
		if(paramFormatter!=null){
			for(IParamFormatter pf : paramFormatter){
				String[] ignore = pf.getncreaseKeys();
				if(ignore!=null){
					for(String key : ignore){
						args.remove(key);
					}
				}
			}
		}
		r = args.toArray(new String[]{});
		getCheckArgsByIndexCache.put(index, r);
		return r;
	}
}
