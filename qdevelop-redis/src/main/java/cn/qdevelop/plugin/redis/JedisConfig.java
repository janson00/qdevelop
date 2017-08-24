package cn.qdevelop.plugin.redis;

import java.util.Properties;

import org.apache.log4j.Logger;

import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.common.utils.QSource;

public class JedisConfig {
	
	protected static Logger log = QLog.getLogger(JedisConfig.class);
	
	public static String REDIS_IP;
	public static int REDIS_PORT;
	public static String PASSWORD;
	public static int MAX_ACTIVE;
	public static int MAX_IDLE;
	public static long MAX_WAIT;
	public static boolean TEST_ON_BORROW;
	public static boolean TEST_ON_RETURN;
	public static boolean SHARDED;
	public static int DBINDEX;
	public static boolean JEDIS_SWITCH;
	
	static {
		init();
	}

	public static void init() {
			Properties pro = QSource.getInstance().loadProperties("plugin-config/qdevelop-redis.properties",JedisConfig.class);
			log.info("loading redis config from redis.properties.......");
			JEDIS_SWITCH = Boolean.parseBoolean(pro.getProperty("jedis.cache.switch"));
			REDIS_IP = pro.getProperty("redis.ip");
			REDIS_PORT = Integer.parseInt(pro.getProperty("redis.port"));
			PASSWORD = pro.getProperty("redis.password");
			MAX_ACTIVE = Integer.parseInt(pro.getProperty("redis.pool.maxActive"));
			MAX_IDLE = Integer.parseInt(pro.getProperty("redis.pool.maxIdle"));
			MAX_WAIT = Integer.parseInt(pro.getProperty("redis.pool.maxWait"));
			TEST_ON_BORROW = Boolean.parseBoolean(pro.getProperty("redis.pool.testOnBorrow"));
			TEST_ON_RETURN = Boolean.parseBoolean(pro.getProperty("redis.pool.testOnReturn"));
			SHARDED = Boolean.parseBoolean(pro.getProperty("redis.sharded"));
			DBINDEX = Integer.parseInt(pro.getProperty("redis.dbindex"));
			log.info("redis config load Completed :"+REDIS_IP);
	}
}