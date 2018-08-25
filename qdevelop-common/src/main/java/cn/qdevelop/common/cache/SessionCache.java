/**
 * 
 */
package cn.qdevelop.common.cache;

import java.util.HashMap;

/**
 * @author square
 *
 */
public class SessionCache{
	private final ThreadLocal<HashMap<String, Object>> sessionCache = new ThreadLocal<HashMap<String, Object>>();

	public  void init() {
		if (sessionCache.get() == null) {
			HashMap<String, Object> cache = new HashMap<String, Object>();
			sessionCache.set(cache);
		}
	}

	public void put(String key, Object value,long expires) {
		sessionCache.get().put(key, value);
	};

	public Object get(String key) {
		return sessionCache.get().get(key);
	}

	public void remove(String key) {
	}

	public void reload() {
		
	}


	
}
