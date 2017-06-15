package org.qdevelop.mq.rabbit;

import java.util.HashMap;

public class TestProvider {

	public static void main(String[] args) {
		HashMap<String,String> aa = new HashMap<String,String>();
		aa.put("zzz", "@@@@@@");
		 MQProvider.getInstance().publish("Janson", aa);
//		 MQProvider.getInstance().publish("Janson", "janson");


//		for(int i=0;i<10000;i++){
//			Object[] v = new Object[]{"test",i};
//			 MQProvider.getInstance().publish("Janson", v);
//			 System.out.print(".");
////			 try {
////				Thread.sleep(1000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
//		}
	}

}
