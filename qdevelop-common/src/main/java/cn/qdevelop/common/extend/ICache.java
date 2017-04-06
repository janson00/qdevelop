/**
 * 
 */
package cn.qdevelop.common.extend;

/**
 * @author square
 * 
 */
public interface ICache<T,V> {
	/**
	 * 设置缓存
	 * @param key 主键
	 * @param val 值
	 * @param expires 过期时长
	 */
	public void put(T key,V val,long expires);

	public void remove(T key);

	public V get(T key);

	public void reload();

}
