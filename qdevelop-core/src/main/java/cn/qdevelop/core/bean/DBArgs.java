package cn.qdevelop.core.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 快速构建请求参数
 * @author janson
 *
 */
public class DBArgs extends HashMap<String, Object>{

	public DBArgs(){}

	public DBArgs(String sqlIndex){
		this.put("index", sqlIndex);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3241525323559669377L;

	/**
	 * 增加请求参数
	 */
	public DBArgs put(String key,Object value){
		super.put(key, value);
		return this;
	}
	
	/**
	 * 根据http请求参数url直接初始化成map数据
	 * @param httpParametters
	 * @return
	 */
	public DBArgs putHttpParametters(String httpParametters){
		String[] tmp = httpParametters.split("\\&");
		for(int i=0;i<tmp.length;i++){
			if(tmp[i].indexOf("=")>-1){
				String key = tmp[i].substring(0, tmp[i].indexOf("="));
				String value = tmp[i].substring(tmp[i].indexOf("=")+1);
				super.put(key, value);
			}
		}
		return this;
	}

	/**
	 * 增加一组请求参数
	 * @param args
	 * @return
	 */
	public DBArgs putMap(Map<String, Object> args){
		super.putAll(args);
		return this;
	}
	
	/**
	 * 指定参数的设置
	 * @param args
	 * @param fileds
	 * @return
	 */
	public DBArgs putMap(Map<String, Object> args,String[] fileds){
		for(String key : fileds){
			super.put(key, args.get(key));
		}
		return this;
	}

	/**
	 * 从JAVA Bean中获取属性值
	 * @param clazz
	 * @return
	 */
	public DBArgs putJavaBean(Object clazz){
		Field[] fields = clazz.getClass().getDeclaredFields();
		for(Field filed : fields){
			try {
				filed.setAccessible(true);
				String key  = filed.getName();
				Object value = filed.get(clazz);
				if(value!=null){
					super.put(key, value);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	/**
	 * 指定需要的属性值
	 * @param clazz
	 * @param fileds
	 * @return
	 */
	public DBArgs putJavaBean(Object clazz,String[] fileds){
		for(String fieldName : fileds){
			try {
				Field target = clazz.getClass().getDeclaredField(fieldName);
				target.setAccessible(true);
				Object value = target.get(clazz);
				if(value!=null){
					super.put(fieldName, value);
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return this;
	} 
	
//	public static void main(String[] args) {
//		DBArgs a = new DBArgs().putHttpParametters("username=admin&passwd=f88bdb3ce001485116bff776be5b8687&age=");
//		System.out.println(a);
//	}
}
