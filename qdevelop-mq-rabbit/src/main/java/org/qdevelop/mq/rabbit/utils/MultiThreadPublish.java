package org.qdevelop.mq.rabbit.utils;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

public class MultiThreadPublish implements Runnable{
  String queueName;
  byte[] msg;
  Channel channel;
  public MultiThreadPublish(Channel channel,String queueName,byte[] msg){
    this.queueName = queueName;
    this.msg = msg;
    this.channel = channel;
  }
  
  @Override
  public void run() {
    try {
      channel.basicPublish("", this.queueName, MessageProperties.PERSISTENT_BASIC,this.msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
