package cn.qdevelop.plugin.redis;

import java.util.Properties;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLogFactory;
import cn.qdevelop.common.files.QSource;

public class JedisConfig {

	protected static Logger log = QLogFactory.getLogger(JedisConfig.class);

	public  String REDIS_IP;
	public  int REDIS_PORT;
	public  String PASSWORD;
	public  int MAX_ACTIVE;
	public  int MAX_IDLE;
	public  long MAX_WAIT;
	public  boolean TEST_ON_BORROW;
	public  boolean TEST_ON_RETURN;
	public  boolean SHARDED;
	public  int DBINDEX;
	public  boolean JEDIS_SWITCH;



	public  JedisConfig() {
		init("plugin-config/qdevelop-redis.properties");
	}

	public JedisConfig(String configPath) {
		init(configPath);
	}

	public void init(String configPath){
		Properties pro = QSource.getInstance().loadProperties(configPath,JedisConfig.class);
		if(pro!=null){
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
		}else{
			log.error("redis config not defound : "+configPath);
		}
	}
}