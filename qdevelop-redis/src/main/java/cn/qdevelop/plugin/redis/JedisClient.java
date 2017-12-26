/**
 * Project Name:webapi-1.0
 * File Name:JedisClient.java
 * Package Name:com.wangjiu.webapi.cache
 * Date:2014年10月23日 下午4:34:31
 * Copyright (c) 2014, chenjianjun@wangjiu.com All Rights Reserved.
 *
 */
package cn.qdevelop.plugin.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.qdevelop.common.utils.XYUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@SuppressWarnings({"rawtypes"})
public class JedisClient {

	private static JedisClient jedisClient;
	
	private ShardedJedisPool shardedJedisPool;
	
	private JedisPool jedisPool;
	
	private JedisConfig jedisConfig;
	
	public static JedisClient getInstance(){
		if (jedisClient == null) {
			jedisClient = new JedisClient();
		}
		return jedisClient;
	}
	
	public JedisClient() {	
		jedisConfig = new JedisConfig();
		if(jedisConfig.SHARDED){
			initialShardedPool();
		}else{
			initialPool();
		}
	}
	
	public JedisClient(String config) {	
		jedisConfig = new JedisConfig(config);
		if(jedisConfig.SHARDED){
			initialShardedPool();
		}else{
			initialPool();
		}
	}
	
	/**
	 * 普通连接池
	 */
	private void initialPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(jedisConfig.MAX_ACTIVE);
		config.setMaxIdle(jedisConfig.MAX_IDLE);
		config.setMaxWaitMillis(jedisConfig.MAX_WAIT);
		config.setTestOnBorrow(jedisConfig.TEST_ON_BORROW);
		jedisPool = new JedisPool(config,
								  jedisConfig.REDIS_IP,
								  jedisConfig.REDIS_PORT,
								  Protocol.DEFAULT_TIMEOUT,
								  jedisConfig.PASSWORD == null || jedisConfig.PASSWORD.equals("") ? null : jedisConfig.PASSWORD,
								  jedisConfig.DBINDEX);
	}

	/**
	 * 初始化切片池
	 */
	private void initialShardedPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(jedisConfig.MAX_ACTIVE);
		config.setMaxIdle(jedisConfig.MAX_IDLE);
		config.setMaxWaitMillis(jedisConfig.MAX_WAIT);
		config.setTestOnBorrow(jedisConfig.TEST_ON_BORROW);
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(jedisConfig.REDIS_IP, jedisConfig.REDIS_PORT, "master"));
		shardedJedisPool = new ShardedJedisPool(config, shards);
	}
	
	public void returnResource(Jedis jedis){
		jedis.close();
	}
	
	public void returnResource(ShardedJedis shardedJedis){
		shardedJedis.close();
	}
	
	public Jedis getJedis() {
		if (jedisPool == null) throw new RuntimeException("jedisPool not init");
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}

	public ShardedJedis getShardedJedis() {
		if (shardedJedisPool == null) throw new RuntimeException("shardedJedisPool not init");
		return shardedJedisPool.getResource();
	}
	
	public long setString(String key, String value) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.set(key, value);
    		return ret!=null&&ret.equals("OK")?1:0;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public long setStringNoExists(String key, String value, long milliseconds) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.set(key, value, "NX", "PX", milliseconds);
    		return ret!=null&&ret.equals("OK")?1:0;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public long setString(String key, String value, int seconds) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.set(key, value);
			if(ret!=null&&ret.equals("OK")) {
				return jedis.expire(key, seconds);
			} else {
				return 0;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public String getString(String key) {
		Jedis jedis = getJedis();
		try {
			String o = jedis.get(key);
			if (o == null || o.length() == 0 )  return null;
			else return o;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	/*public long setMap(String key, Map value) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.set(key, JSON.toJSONString(value, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
    		return ret!=null&&ret.equals("OK")?1:0;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}*/
	
	/*public Map getMap(String key) {
		Jedis jedis = getJedis();
		try {
			Map o = (Map)JSON.parse(jedis.get(key));
			if (o == null || o.size() == 0) return null;
			else return o;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}*/

	/***
	 * 设置map
	 * @param key
	 * @param dataMap
	 * @return
	 */
	public int setHM(String key, Map<String,String> dataMap){
		Jedis jedis = getJedis();
		try {
			jedis.hmset(key, dataMap);
    		return 1;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}

	/***
	 * 设置map
	 * @param key
	 * @param dataMap
	 * @return
	 */
	public List<String> getHM(String key, String... fields){
		Jedis jedis = getJedis();
		try {
			return jedis.hmget(key, fields);
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public long setList(String key, List<String> value) {
		Jedis jedis = getJedis();
		try {
			for(int i = 0, len = value.size(); i < len; i++) {
				jedis.lpush(key, value.get(i));
			}
    		return 1;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}

	/**
	 * 替换list内容
	 * @param key
	 * @param valueList
	 * @return
	 */
	public long replaceList(String key, List<? extends Object> valueList){
		Jedis jedis = getJedis();
		try {
			long lengthR = jedis.llen(key);//redis中的list长度
			long lengthN = valueList==null?0:valueList.size();//新数据长度
			if(lengthN==0){
				jedis.del(key);//清空redislist
			}else{
				for(long i=0;i<lengthN;i++){
					if(i<lengthR){//redis中存在
						jedis.lset(key, i, XYUtil.transformString(valueList.get((int)i)));
					}else{
						jedis.rpush(key, XYUtil.transformString(valueList.get((int)i)));
					}
				}
				if(lengthR>lengthN){
					jedis.ltrim(key, 0, lengthN-1);
				}
			}
    		return 1;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	/**
	 * 替换list内容
	 * @param key
	 * @param valueList
	 * @return
	 */
	public long replaceList(String key, List<? extends Object> valueList, int seconds){
		Jedis jedis = getJedis();
		try {
			long lengthR = jedis.llen(key);//redis中的list长度
			long lengthN = valueList==null?0:valueList.size();//新数据长度
			if(lengthN==0){
				jedis.del(key);//清空redislist
			}else{
				for(long i=0;i<lengthN;i++){
					if(i<lengthR){//redis中存在
						jedis.lset(key, i, XYUtil.transformString(valueList.get((int)i)));
					}else{
						jedis.rpush(key, XYUtil.transformString(valueList.get((int)i)));
					}
				}
				if(lengthR>lengthN){
					jedis.ltrim(key, 0, lengthN-1);
				}
				jedis.expire(key, seconds);
			}
    		return 1;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	/**
	 * 创建新list内容，原有值将会被清除
	 * @param key
	 * @param valueList
	 * @return
	 */
	public long createNewList(String key, List<? extends Object> valueList, int seconds){
		Jedis jedis = getJedis();
		try {
			long length = jedis.llen(key);//redis中的list长度
			if(length > 0) jedis.del(key);
			if(valueList != null && valueList.size() > 0) {
				for(int i = 0, len = valueList.size(); i < len; i++) {
					jedis.rpush(key, XYUtil.transformString(valueList.get(i)));
				}
				jedis.expire(key, seconds);
			}
    		return 1;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public long getListSize(String key) {
		Jedis jedis = getJedis();
		try {
			long len = jedis.llen(key);
			return len;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public List<String> getAllList(String key) {
		Jedis jedis = getJedis();
		try {
			long end = jedis.llen(key);
			if(end <= 0) return null;
			List<String> list = jedis.lrange(key, 0, end - 1);
			return list;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public List<Map> getMapList(String key, List<String> keys) {
		Jedis jedis = getJedis();
		List<Map> list = new ArrayList<Map>();
		Map data = null;
		try {
			for(String id : keys) {
				data = (Map)JSON.parse(jedis.get(key + id));
				list.add(data);
			}
			return list;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public List<String> getListRange(String key, long start, long end) {
		Jedis jedis = getJedis();
		try {
			long _end = jedis.llen(key);
			if(start > _end || start < 0 || end < 0 || start > end) return null;
			if(end > _end){
				end = _end;
			}
			List<String> list = jedis.lrange(key, start, end - 1);
			return list;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public long delKey(String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.del(key);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public void flushDB() {
		Jedis jedis = getJedis();
		try {
			jedis.flushDB();
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
	public void flushAll() {
		Jedis jedis = getJedis();
		try {
			jedis.flushAll();
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(jedis != null) jedis.close();
		}
	}
	
//	@SuppressWarnings({"serial"})
	public static void main(String[] args) {
//		getInstance().getJedis().flushDB();
		Jedis jedis = getInstance().getJedis();
		
		
		jedis.set("product", "商品");
//		jedis.hset("product:single:123", "123", "321");
		/*final HashMap<String, String> data = new HashMap<String, String>(){
			{
				put("aaa", "aaa");
				put("bbb", "bbb");
			}
		};
		final HashMap<String, String> data1 = new HashMap<String, String>(){
			{
				put("ccc", "ccc");
				put("ddd", "ddd");
			}
		};
		ArrayList list = new ArrayList() {
			{
				add(data);
				add(data1);
			}
		};*/
//		String value = JSON.toJSONString(data);
//		System.out.println(data);
//		jedis.set("product:single:456", value);
//		jedis.hmset("product:single:456", data);
//		getInstance().set("single:product:456", data, 6000);
//		getInstance().set("single:product:123", list, 6000);
	
//		Map tmp = (Map)getInstance().get("single:product:456",""."");
//		List tL = (List)getInstance().get("single:product:123");
//		String tmp = jedis.get("product:single:456");
//		System.out.println(tmp);
//		System.out.println(tL);
//		Jedis jedis = getInstance().getJedis();
//		jedis.hset("product:single:789", "aaa", "111");
//		jedis.hset("product:single:789", "bbb", "222");
		/*for(int i = 0, len = list.size(); i < len; i++) {
//			jedis.append("all:product", JSON.toJSONString(list.get(i)));
			jedis.lpush("all:product", JSON.toJSONString(list.get(i)));
		}*/
//		List tmp = jedis.lrange("all:product", 1, jedis.llen("all:product") - 1);
//		List tmp = getInstance().getListAll(COLLECTION_PRODUCT + "all");
//		System.out.println(tmp);
//		jedis.append("all:product", list);
//		jedis.hmset("single:product:789", data);
//		Map ss = jedis.hgetAll("single:product:789");
//		System.out.println(ss);
	}

}
