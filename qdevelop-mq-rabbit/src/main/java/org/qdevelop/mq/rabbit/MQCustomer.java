package org.qdevelop.mq.rabbit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.clazz.ClazzUtils;
import cn.qdevelop.common.files.QSource;

public class MQCustomer {
	protected static Logger log = QLog.getLogger(MQCustomer.class);

	private static MQCustomer _MQCustomer;
	
	public static MQCustomer getInstance(){
		if(_MQCustomer == null){
			_MQCustomer = new MQCustomer();
			_MQCustomer.init("plugin-config/rabbit-mq.properties",ClazzUtils.getCallClass(MQCustomer.class));
		}
		return _MQCustomer;
	}
	
	public static MQCustomer getInstance(String config){
		if(_MQCustomer == null){
			_MQCustomer = new MQCustomer();
			_MQCustomer.init(config,ClazzUtils.getCallClass(MQCustomer.class));
		}
		return _MQCustomer;
	}
	
	private ArrayList<Connection> collection = new ArrayList<Connection>();

	ConnectionFactory factory;
	
	public MQCustomer(){
	}

	/**
	 * 自定义配置文件路径地址
	 * @param config
	 */
	public MQCustomer(String config){
		init(config,ClazzUtils.getCallClass(MQCustomer.class));
	}

	private void init(String config,Class<?> callClass ){
		log.info("load MQ config "+config+" from "+callClass.getName());
		Properties props = QSource.getInstance().loadProperties(config,callClass);
		try {
			factory = new ConnectionFactory();  
			factory.setHost(props.getProperty("mq_server_host"));
			factory.setPort(Integer.parseInt(props.getProperty("mq_server_port")));
			factory.setUsername(props.getProperty("mq_server_username"));
			factory.setPassword(props.getProperty("mq_server_password"));
			factory.setVirtualHost(props.getProperty("mq_server_virtualhost"));
			System.out.println("connect MQ : "+props.getProperty("mq_server_host"));
		}  catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		factory.setAutomaticRecoveryEnabled(true);
	}



	public void register(final ICustomer customer){
		try {
			Connection connection = factory.newConnection();
			final Channel channel = connection.createChannel();
			channel.queueDeclare(customer.getQueueName(), true, false, false, null);
			channel.basicConsume(customer.getQueueName(),false,new QueueingConsumer(channel){
				public void handleDelivery(String consumerTag,Envelope envelope,
						AMQP.BasicProperties properties,byte[] body){
					Serializable vals = null;
					if(properties.getContentType().equals("application/json")){
						vals = new String(body);
					}else{
						vals = toSerializable(body);
					}
					
					if(customer.handleDelivery(consumerTag, envelope, properties, vals)){
						try {
							channel.basicAck(envelope.getDeliveryTag(), false);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						try {
							channel.basicReject(envelope.getDeliveryTag(), true);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			});
			collection.add(connection);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
	}

	public void shutdown(){
		for(int i=0;i<collection.size();i++){
			Connection connection =  collection.get(i);
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection=null;
		}
		collection.clear();
	}



	private Serializable toSerializable(byte[] bytes){
		Serializable obj= null;  
		try {  
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
			ObjectInputStream oi = new ObjectInputStream(bi);  
			obj = (Serializable)oi.readObject();  
			bi.close();  
			oi.close();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return obj;
	}

}
