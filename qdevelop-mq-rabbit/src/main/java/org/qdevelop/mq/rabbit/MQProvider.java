package org.qdevelop.mq.rabbit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.qdevelop.mq.rabbit.utils.MQBean;
import org.qdevelop.mq.rabbit.utils.MQConfig;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class MQProvider  extends ConcurrentLinkedQueue<MQBean>{
  /**
   * 
   */
  private static final long serialVersionUID = -2312239022403219259L;
  private  AtomicBoolean isRunning = new AtomicBoolean(false);
  private static MQProvider _MQPublisher = new MQProvider();
  public static MQProvider getInstance(){return _MQPublisher;}
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
          MQProvider.getInstance().aync();
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
//    factory.isAutomaticRecoveryEnabled()
    if(factory==null){
      initFactory();
    }
    Connection connection = null;
    Channel channel = null;
    try {
      connection = factory.newConnection();  
      channel = connection.createChannel(); 
      while(!this.isEmpty()){
        MQBean mq = super.poll();
        channel.basicPublish("", mq.getQueueName(), MessageProperties.PERSISTENT_BASIC, mq.getMsg());
      }

    } catch (IOException e) {
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
  
  private void initFactory() {
    Properties props = new Properties();
    try {
      props.load(new MQConfig().getSourceAsStream("plugin-config/rabbit-mq.properties"));
      factory = new ConnectionFactory();  
      factory.setHost(props.getProperty("mq_server_host"));
      factory.setPort(Integer.parseInt(props.getProperty("mq_server_port")));
      factory.setUsername(props.getProperty("mq_server_username"));
      factory.setPassword(props.getProperty("mq_server_password"));
      factory.setVirtualHost(props.getProperty("mq_server_virtualhost"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
   
  }

}
