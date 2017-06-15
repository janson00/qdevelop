/**
 * 
 */
package cn.qdevelop.common.cache;

import java.util.HashMap;

import cn.qdevelop.common.extend.ICache;

/**
 * @author square
 *
 */
public class SessionCache implements ICache<String,Object>{
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

	@Override
	public void remove(String key) {
	}

	@Override
	public void reload() {
		
	}


	
}
