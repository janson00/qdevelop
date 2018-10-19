package cn.qdevelop.auth.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.google.code.yanf4j.core.Session;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.files.QSource;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.MemcachedTCPSession;
import net.rubyeye.xmemcached.networking.Connector;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class XMemcached  extends QCache {


	private static  XMemcached local;

	public static XMemcached getInstance(){
		if(local!=null)return local;
		local = new XMemcached();
		return local;
	}

	private static final Logger log  = QLog.getLogger(XMemcached.class);
	private MemcachedClientBuilder builder;
	private MemcachedClient client; 
	private boolean isCacheAble;


	public XMemcached(Properties props) throws Exception{
		initCache(props);
		int maxPool = props.get("xmemcached_max_pool") == null ? 10 : Integer.parseInt( props.get("xmemcached_max_pool").toString());
		log.info("XMemcachedImpl ip:"+this.ips+" maxPool:"+maxPool);
		builder = new XMemcachedClientBuilder(AddrUtil.getAddresses (ips));
		builder.setConnectionPoolSize(maxPool);  //设成25或更高就ok，设小了就出异常
		client=builder.build();//client是成功了
		initOptimize();
	}

	public XMemcached() {
		try {
			Properties props = QSource.getInstance().loadProperties("plugin-config/memcached.properties",XMemcached.class);
			initCache(props);
			isCacheAble = Boolean.parseBoolean(props.getProperty("is_cache_able"));
			int maxPool = props.get("xmemcached_max_pool") == null ? 20 : Integer.parseInt( props.get("xmemcached_max_pool").toString());
			log.info("XMemcachedImpl ip:"+this.ips+" maxPool:"+maxPool);
			System.out.println("XMemcachedImpl ip:"+this.ips+" maxPool:"+maxPool);
			builder = new XMemcachedClientBuilder(AddrUtil.getAddresses (ips));
			builder.setConnectionPoolSize(maxPool);
		    
			client=builder.build();//client是成功了
			initOptimize();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化xmemcached优化选项
	 *   
	 * @Description:
	 */
	private void initOptimize(){
		if(builder!=null){
			//多memcache server 时做hash一致性的分布算法
//			builder.setSessionLocator(new KetamaMemcachedSessionLocator());
//			builder.setSocketOption(StandardSocketOption.SO_RCVBUF, 32* 1024);// 设置接收缓存区为32K，默认16K
//			builder.setSocketOption(StandardSocketOption.SO_SNDBUF,16 *1024); // 设置发送缓冲区为16K，默认为8K
//			builder.setSocketOption(StandardSocketOption.TCP_NODELAY,false); // 启用nagle算法，提高吞吐量，默认关闭
			builder.getConfiguration().setSessionIdleTimeout(10000);
		}
		if(client!=null){
			client.setOptimizeMergeBuffer(false); //关闭合并buffer的优化
			client.setMergeFactor(50);  //默认是150，缩小到50
		}
	}

	public boolean set(String key, Serializable value, String config) {
		return set(key,value,this.getExp(config));
	}

	public boolean set(String key, Serializable value, int expire) {
		if(!isCacheAble || key == null || value == null)return false;
		try {
			boolean r =  client.set(toKey(key), expire, value);
			if(this.isCacheDebug){
				log.debug("set ["+key+"] => "+toKey(key));
			}
			return r;
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Serializable get(String key) {
		if(!isCacheAble || key == null)return null;
		try {
			if(this.isCacheDebug){
				log.debug("get ["+key+"] => "+toKey(key));
			}
			return client.get(toKey(key),1000);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(String key) {
		try {
			if(this.isCacheDebug){
				log.debug("delete ["+key+"] => "+toKey(key));
			}
			return client.delete(toKey(key));
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void shutdown() {
		try {
			if(client!=null && !client.isShutdown()){
				client.shutdown();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//  @Override
	public void printStats(OutputStream stream,IStatusFormatter isf){
		if (stream == null) {  
			stream = System.out;  
		} 
		try {
			Connector connector = client.getConnector();
			for (Session session : connector.getSessionSet()) {
				isf.add("command", ((MemcachedTCPSession) session).getCurrentCommand());
				isf.add("sessionIdleTimeout", session.getSessionIdleTimeout());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd MM:mm:ss");
				isf.add("lastOperationTimeStamp", formatter.format(new Date(session.getLastOperationTimeStamp())));
				isf.add("isIdle", session.isIdle());
				isf.add("beIdleIn", (System.currentTimeMillis()-session.getLastOperationTimeStamp()));
				//        Queue<WriteMessage> queue = ((MemcachedTCPSession) session).getWriteQueue();
				//        List l = new ArrayList();
				//        for (WriteMessage wm:queue){
				//          Map m = new HashMap();
				//          m.put("wm", wm);
				//          m.put("wmsg", wm.getMessage());
				//          l.add(m);
				//        }
				//        isf.add("queue", l);
				isf.flush();
			}
			stream.write(isf.toString().getBytes());  
			stream.flush(); 
		} catch (IOException e) {
			e.printStackTrace();
		}    
	}

	@Override
	public boolean flush() {
		Connector connector = client.getConnector();
		for (Session session : connector.getSessionSet()) {
			session.flush();
		}
		return true;
	}

	@Override
	public boolean add(String key, Serializable value, int expire) {
		if(!isCacheAble || key == null || value == null)return false;
		try {
			if(this.isCacheDebug){
				log.debug("add ["+key+"] => "+toKey(key));
			}
			return  client.set(toKey(key), expire, value);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean add(String key, Serializable value, String config) {
		return add(key,value,this.getExp(config));
	}

	@Override
	public Serializable get(String key, String config) {
		return get(key);
	}

	@Override
	public boolean delete(String key, String config) {
		return delete(key);
	}

}
