package cn.qdevelop.core.db.connect;

public class ConnectShunDownThread extends Thread{
	public void run() {
		System.out.println("shutdown all connects!");
		ConnectFactory.shutdown();
	}
}
