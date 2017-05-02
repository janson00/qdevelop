package org.qdevelop.mq.rabbit;

public class TestProvider {

	public static void main(String[] args) {
		for(int i=0;i<10;i++){
			Object[] v = new Object[]{"test",i};
			 MQProductor.getInstance().publish("Janson", v);
			 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
