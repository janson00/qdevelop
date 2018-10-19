package cn.qdevelop.core.template;

public class QThreadLogger {
	private static ThreadLocal<StringBuilder> cache = new ThreadLocal<StringBuilder>();
	public static void add(String ... str){
		StringBuilder sb = cache.get();
		if(sb == null){
			sb = new StringBuilder();
		}
		for(String s : str){
			sb.append(s);
		}
		cache.set(sb);
	}
	
	public static String get(){
		StringBuilder sb = cache.get();
		cache.remove();
		return sb==null?"":sb.toString();
	}
}
