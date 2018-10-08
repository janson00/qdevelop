package cn.qdevelop.auth.utils;

import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public abstract class QCache {

	public static  String EXP_Default = "EXP_Default";
	public static  String EXP_5Min = "EXP_5Min";
	public static  String EXP_10Min = "EXP_10Min";
	public static  String EXP_30Min = "EXP_30Min";
	public static  String EXP_1Hour = "EXP_1Hour";
	public static  String EXP_6Hour = "EXP_6Hour";
	public static  String EXP_1Day = "EXP_1Day";
	public static String EXP_1Month = "EXP_1Month";

	protected boolean isCacheDebug,isCacheAble,isCachePressKey;
	protected String currentConfigCacheClient,ips;

	protected void initCache(Properties props){
		isCacheDebug = props.get("is_cache_debug") == null ? true :  Boolean.parseBoolean(props.get("is_cache_debug").toString());
		isCacheAble = props.get("is_cache_able") == null ? true :  Boolean.parseBoolean(props.get("is_cache_able").toString());
		isCachePressKey = props.get("is_cache_press_key") == null ? true :  Boolean.parseBoolean(props.get("is_cache_press_key").toString());
		currentConfigCacheClient = props.get("cache_client") == null ? "xmemcached" : props.get("cache_client").toString();
		ips = props.get("cache_service_url") == null ? "127.0.0.1:11211" : props.get("cache_service_url").toString();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				System.out.println("shutdown cache connects!");
				shutdown();
			}
		});
	}


	public static  Map<String, Integer> expires = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1033820870309385884L;
		{
			put("EXP_5Min",300);
			put("EXP_10Min",600);
			put("EXP_30Min",1800);
			put("EXP_1Hour",3600);
			put("EXP_6Hour",21600);
			put("EXP_1Day",86400);
			put("EXP_1Month",2592000);
		}

	};

	/**
	 * 根据配置获取过期时长
	 * @param config
	 * @return
	 */
	public int getExp(String config){
		return expires.get(config)==null ? 3600 : expires.get(config);
	}

	/**
	 * 根据过期时长获取配置名称
	 * @param exp
	 * @return
	 */
	public String getConfig(int exp){
		Iterator<Entry<String, Integer>> iter = expires.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Integer> entry = (Map.Entry<String,Integer>) iter.next();
			if(entry.getValue() == exp){
				return entry.getKey();
			}
		}
		return EXP_Default;
	}
	public abstract boolean add(String key, Serializable value, int expire);
	public abstract boolean add(String key, Serializable value, String config);
	public abstract boolean set(String key, Serializable value, int expire);
	public abstract boolean set(String key, Serializable value, String config);
	public abstract Serializable get(String key);
	public abstract Serializable get(String key,String config);
	public abstract boolean delete(String key);
	public abstract boolean delete(String key,String config);
	public abstract boolean flush();
	public abstract void shutdown();
	public abstract void printStats(OutputStream stream,IStatusFormatter isf);

	/**
	 * 根据配置判断是否需要统一压缩key的长度
	 * @param key
	 * @return
	 */
	public String toKey(String key){
		return isCachePressKey ? toMD5Key(key) : key;
	}

	private String toMD5Key(String key)  {
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		try { 
			byte[] strTemp = key.getBytes(); 
			MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
			mdTemp.update(strTemp); 
			byte[] md = mdTemp.digest(); 
			int j = md.length; 
			char str[] = new char[j * 2]; 
			int k = 0; 
			for (int i = 0; i < j; i++) { 
				byte byte0 = md[i]; 
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; 
				str[k++] = hexDigits[byte0 & 0xf]; 
			} 
			return new String(str); 
		} 
		catch (Exception e){ 
			return key; 
		} 
	}

}
