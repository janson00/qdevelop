package org.qdevelop.mq.rabbit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

public class TestBlock {
	public static String now(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	public static void main(String[] args) {
		MQCustomer myCustomer = new MQCustomer("plugin-config/rabbit-mq.properties");
		myCustomer.register(new ICustomer(){

			@Override
			public String getQueueName() {
				return "Janson";
			}

			@Override
			public boolean handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
					Serializable body) {
				System.out.print(".");
				return true;
			}
			
		});
		
		
//		MQCustomer.getInstance().register(new ICustomer(){
//			@Override
//			public String getQueueName() {
//				return "Janson";
//			}
//
//			@Override
//			public boolean handleDelivery(String consumerTag, Envelope envelope,
//					BasicProperties properties, Serializable body) {
////				Object[] v = (Object[])body;
////				System.out.println(body);
////				Integer idx = (Integer)v[1];
////				boolean r = idx%2 == 0;
////				try {
////					Thread.sleep(500);
////				} catch (InterruptedException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				System.out.println(now()+"\t:["+r+"] "+v[1]+" - "+v[0]);
////				return r;
//				return true;
//			}
//		});
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		MQCustomer.getInstance().register(new ICustomer(){
//			@Override
//			public String getQueueName() {
//				return "Janson";
//			}
//
//			@Override
//			public boolean handleDelivery(String consumerTag, Envelope envelope,
//					BasicProperties properties, Serializable body) {
//				Object[] v = (Object[])body;
//				Integer idx = (Integer)v[1];
//				boolean r = idx%2 == 0;
//				System.out.println(now()+"\t:["+r+"] "+v[1]+" - "+v[0]);
//				return true;
//			}
//		});
	}

}
