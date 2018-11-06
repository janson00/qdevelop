package cn.qdevelop.auth.utils;

import java.util.Properties;
import java.util.regex.Pattern;

import cn.qdevelop.auth.common.AuthLogin;
import cn.qdevelop.common.files.QSource;

public class AuthUtils {
	
	public static final Pattern formatUri = Pattern.compile("^\\/+|^(http|https):\\/\\/|(\\?|#).+$",Pattern.CASE_INSENSITIVE);
	public static final Pattern formatUriClean = Pattern.compile("\\/+");

	private static Properties qdevelop_menu_dict = QSource.getInstance().loadProperties("plugin-config/qdevelop_auth.properties", AuthLogin.class) ;
	private static Pattern isDynamicValue = Pattern.compile("\\{[a-zA-Z0-9_]+\\}"); 
	
	/**
	 * 从动态配置中获取域名，为了提供不同的系统环境 
	 * @param url
	 * @return
	 */
	public static String relaceDynamicValue(String url){
		if(qdevelop_menu_dict == null || !isDynamicValue.matcher(url).find())return url;
		while(isDynamicValue.matcher(url).find()){
			String header = url.substring(0,url.indexOf("{"));
			String key  = url.substring(header.length()+1,url.indexOf("}"));
			String ender = url.substring(url.indexOf("}")+1);
			url = new StringBuilder().append(header)
					.append(qdevelop_menu_dict.getProperty(key)==null?"":qdevelop_menu_dict.getProperty(key))
					.append(ender).toString();
		}
		return url;
	}
	
	
	private static Boolean isDevEnv;
	/**
	 * 是否是开发环境
	 * @return  
	 * @Description:
	 */
	public static boolean isDevEnv(){
		if(isDevEnv!=null)return isDevEnv;
		if(qdevelop_menu_dict.getProperty("is_dev_env")==null)return false;
		isDevEnv =  Boolean.parseBoolean(qdevelop_menu_dict.getProperty("is_dev_env"));
		return isDevEnv;
	}
	
	public static void main(String[] args) {
		System.out.println(relaceDynamicValue("{SVR_HOSTS}/html/main.html"));
	}
}
