package cn.qdevelop.id.client;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IDClientTest implements Runnable {
	public static double BMI(double weight,double hight){
		return weight/(hight*hight);
	}

	public static void test(int cryptNum,int digit){
		int head = 0;
		char[] val = String.valueOf(cryptNum).toCharArray();
		if(val.length > digit){
			StringBuffer sb = new StringBuffer();
			int idx = val.length - digit;
			for(int i=0;i<val.length;i++){
				if(i<idx){
					sb.append(val[i]);
				}else{
					sb.append(0);
				}
			}
			head = Integer.parseInt(sb.toString());
		}
		System.out.println(head); 
	}

	public static void main(String[] args) {
		
		ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(20);
		for(int i=0;i<100;i++){
			threadPool.execute(new IDClientTest());
		}
		threadPool.shutdown();
//		try {
//			threadPool.awaitTermination(30000, TimeUnit.MILLISECONDS);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			long id = IDClient.getInstance().getID("users", 6, 5);
//			System.out.println(id+" "+IDClient.getInstance().decodeNumber(id, 6));
////			System.out.println(IDClient.getInstance().getIDStr("asdasd", 6, 5));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for(int i=0;i<9999;i++){
//			System.out.println(IDClient.getInstance().decodeNumber(i, 4));
//		}
//		
	}
	static Random r = new Random();
	static String[] names = new String[]{"asdasf","sdfasdf","fsad","egaw","cewasd","fewcaec","asd1ds"};
	@Override
	public void run() {
		try {
			String id = IDClient.getInstance().getIDStr(names[r.nextInt(7)], 6, 5);
//			System.out.println(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
