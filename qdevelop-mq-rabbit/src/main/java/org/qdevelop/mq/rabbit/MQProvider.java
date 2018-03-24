package org.qdevelop.mq.rabbit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.qdevelop.mq.rabbit.utils.MQBean;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.clazz.ClazzUtils;
import cn.qdevelop.common.files.QSource;

public class MQProvider  extends ConcurrentLinkedQueue<MQBean>{
	protected static Logger log = QLog.getLogger(MQProvider.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312239022403219259L;
	private  AtomicBoolean isRunning = new AtomicBoolean(false);
	private static MQProvider _MQPublisher ;
	
	public static MQProvider getInstance(){
		if(_MQPublisher == null){
			_MQPublisher = new MQProvider();
			_MQPublisher.initFactory("plugin-config/rabbit-mq.properties", ClazzUtils.getCallClass(MQProvider.class));
		}
		return _MQPublisher;
	}
	
	public static MQProvider getInstance(String config){
		if(_MQPublisher == null){
			_MQPublisher = new MQProvider();
			_MQPublisher.initFactory(config, ClazzUtils.getCallClass(MQProvider.class));
		}
		return _MQPublisher;
	}

	public MQProvider(){
	}

	public MQProvider(String config){
		initFactory(config,ClazzUtils.getCallClass(MQProvider.class));
	}


	ConnectionFactory factory;

	/**
	 * 生成者发布消息入口
	 * @param queueName
	 * @param msg
	 */
	public void publish(String queueName,Serializable msg){
		super.offer(new MQBean(queueName,toByte(msg)));
		if(!isRunning.get()){
			new Thread(){
				public void run(){
					aync();
				}
			}.start();
		}
	}

	private byte[] toByte(Serializable obj){
		byte[] bytes = null; 
		ByteArrayOutputStream bo=null;
		ObjectOutputStream oo=null;
		try {  
			bo = new ByteArrayOutputStream();  
			oo = new ObjectOutputStream(bo);  
			oo.writeObject(obj);  
			bytes = bo.toByteArray();  
		} catch (Exception e) {  
			e.printStackTrace();  
		} finally{
			if(oo!=null){
				try {
					oo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bo!=null){
				try {
					bo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;  
	}

	private void aync(){
		if(isRunning.get() || this.isEmpty())return;
		isRunning.set(true);
		Connection connection = null;
		Channel channel = null;
		MQBean mq = null;
		try {
			connection = factory.newConnection();  
			channel = connection.createChannel(); 
			while(!this.isEmpty()){
				mq = super.poll();
				channel.basicPublish("", mq.getQueueName(), MessageProperties.PERSISTENT_BASIC, mq.getMsg());
				mq=null;
			}
		} catch (IOException e) {
			if(mq!=null){
				this.offer(mq);
			}
			e.printStackTrace();
		}finally{
			if(connection!=null || channel!=null){
				try {
					channel.close();  
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		isRunning.set(false);
	}

	private void initFactory(String config,Class<?> callClass) {
		Properties props = QSource.getInstance().loadProperties(config,callClass);
		try {
			log.info("load MQ config "+config+" from "+callClass.getName());
//			props.load(new MQConfig().getSourceAsStream(config));
			factory = new ConnectionFactory();  
			factory.setHost(props.getProperty("mq_server_host"));
			factory.setPort(Integer.parseInt(props.getProperty("mq_server_port")));
			factory.setUsername(props.getProperty("mq_server_username"));
			factory.setPassword(props.getProperty("mq_server_password"));
			factory.setVirtualHost(props.getProperty("mq_server_virtualhost"));
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

}
