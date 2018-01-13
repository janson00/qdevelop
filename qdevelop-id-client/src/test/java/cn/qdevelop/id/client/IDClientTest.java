package cn.qdevelop.id.client;

import cn.qdevelop.plugin.id.client.IDClient;

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
//		
//		ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
//				.newFixedThreadPool(50);
//		for(int i=0;i<1000;i++){
//			threadPool.execute(new IDClientTest());
//		}
//		threadPool.shutdown();
		long s = System.currentTimeMillis();
		for(int i=0;i<1000;i++){
			try {
				System.out.println(IDClient.getInstance().getRandomID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println((System.currentTimeMillis()-s));
		
//		IDGenerate.getInstance().shutdown();
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
//	static Random r = new Random();
//	static String[] names = new String[]{"asdasf","sdfasdf","fsad","egaw","cewasd","fewcaec","asd1ds"};
//	@Override
//	public void run() {
//		try {
//			String id = IDGenerate.getInstance().getIDStr(names[r.nextInt(7)], 6, 5);
//			System.out.println(id);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
