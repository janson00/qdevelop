package cn.qdevelop.plugin.idgenerate.bean;

import cn.qdevelop.plugin.common.IDRequest;

public class ClientBufferBean {
	long lastQuery;
	int dilatation;
	int power;
	long lastUsed;
	int originBuffer;

	public ClientBufferBean(int buffer){
		lastQuery = System.currentTimeMillis();
		lastUsed = 0;
		dilatation = 0;
		originBuffer = buffer;
	}


	public int getBuffer(){
		return dilatation;
	}

	public void setBuffer(int buffer){
		originBuffer = buffer;
		long now = System.currentTimeMillis();
		long curUsed = now - lastQuery  ;
		if(lastUsed >0){
			dilatation = curUsed - lastUsed > 1000 ? buffer : (int)((1000-curUsed+lastUsed)*buffer/10);
		}else{
			dilatation = buffer;
		}
		lastUsed = curUsed;
		if(dilatation<buffer){
			dilatation = buffer;
		}
		if(dilatation > 1000){
			dilatation = 1000;
		}
		lastQuery = now;
	}
	
	public void reSetBuffer(IDRequest req){
		setBuffer(req.getBuffer());
		req.setBuffer(getBuffer());
	}

//	public static void main(String[] args) throws InterruptedException {
//		ClientBufferBean cbb = new ClientBufferBean();
//		cbb.setBuffer(2);
//		System.out.println("1\t"+cbb.getBuffer());
//
//		Thread.sleep(100);
//		cbb.setBuffer(2);
//		System.out.println("2\t"+cbb.getBuffer());
//
//		//		Thread.sleep(100);
//		cbb.setBuffer(2);
//		System.out.println("3\t"+cbb.getBuffer());
//
//		Thread.sleep(100);
//		cbb.setBuffer(2);
//		System.out.println("4\t"+cbb.getBuffer());
//
//		Thread.sleep(100);
//		cbb.setBuffer(2);
//		System.out.println("5\t"+cbb.getBuffer());
//
//		Thread.sleep(200);
//		cbb.setBuffer(2);
//		System.out.println("6\t"+cbb.getBuffer());
//
//		Thread.sleep(5000);
//		cbb.setBuffer(2);
//		System.out.println("7\t"+cbb.getBuffer());
//
//		//		long s = System.currentTimeMillis();
//		//		System.out.println(System.currentTimeMillis()-s);
//	}

}
