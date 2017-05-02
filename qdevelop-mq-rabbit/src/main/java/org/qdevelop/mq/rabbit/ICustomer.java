package org.qdevelop.mq.rabbit;

import java.io.Serializable;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public interface ICustomer {
  
  /**
   * 获取所要监听的队列的名称
   * @return
   */
  public String getQueueName();
  
  /**
   * 当监听到有消息时，自定义处理方案
   * @param consumerTag     消息队列标识
   * @param envelope        消息相关的属性
   * @param properties      发送相关的配置
   * @param body            消息内容体
   * @return                true：反馈回执，消息处理完毕；false：不反馈回执，可以继续再次处理。
   */
  public boolean handleDelivery(String consumerTag,Envelope envelope,AMQP.BasicProperties properties,Serializable body);
  
}
