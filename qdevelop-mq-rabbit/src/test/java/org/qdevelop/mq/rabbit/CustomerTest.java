package org.qdevelop.mq.rabbit;

import java.io.Serializable;

import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class CustomerTest {
	public static void main(String[] args) {
		MQCustomer.getInstance().register(new ICustomer(){
			@Override
			public String getQueueName() {
				return "Janson";
			}

			@Override
			public boolean handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, Serializable body) {
//				Object[] v = (Object[])body;
				System.out.println(properties.getContentType()+" == "+body);
				return true;
			}
		});
	}
}
