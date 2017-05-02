package org.qdevelop.mq.rabbit.utils;


public class MQBean {
  
  public MQBean(){
    
  }
  
  public MQBean(String queueName,byte[] msg){
    this.queueName = queueName;
    this.msg = msg;
  }
  String queueName;
  byte[] msg;
  public String getQueueName() {
    return queueName;
  }
  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }
  public byte[] getMsg() {
    return msg;
  }
  public void setMsg(byte[] msg) {
    this.msg = msg;
  }
  
}
