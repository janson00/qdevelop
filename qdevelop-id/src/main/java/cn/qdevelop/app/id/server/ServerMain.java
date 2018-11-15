package cn.qdevelop.app.id.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.qdevelop.app.id.server.impl.IDGenerate;

public class ServerMain{

//	public void test(){
//		try {
//			new QFileLoader(){
//				@Override
//				public void despose(InputStream is) {
//					Properties prop = new Properties();
//					try {
//						prop.load(is);
//						System.out.println(prop);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//
//			}.loadFile("qdevelop-id-client.properties");
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
//
//	}

	public static void main(String[] args) {
		ScheduledExecutorService scheduleExe = Executors.newScheduledThreadPool(1); 
		scheduleExe.scheduleWithFixedDelay(new Runnable(){
			public void run() {
				IDGenerate.getInstance().reloadProperties();
			}
		}, 10, 10, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				String path = IDGenerate.getInstance().shutdown(); 
				System.out.println("[SHUTDOWN] "+(new Date()).toString()+" store:"+path);

			}
		});



		//    IDGenerate.getInstance().addRule("random", new RandomRule(6));
		//    IDGenerate.getInstance().addRule("random-6-1", new RandomRule(6));
		//    IDGenerate.getInstance().addRule("random-6-2", new RandomRule(6));
		//    IDGenerate.getInstance().addRule("random-6-3", new RandomRule(6));
		//    IDGenerate.getInstance().addRule("random-5-1", new RandomRule(5));
		//    IDGenerate.getInstance().addRule("random-5-2", new RandomRule(5));
		//    IDGenerate.getInstance().addRule("random-5-3", new RandomRule(5));
		//    IDGenerate.getInstance().addRule("random-4-1", new RandomRule(4));
		//    IDGenerate.getInstance().addRule("random-4-2", new RandomRule(4));
		//    IDGenerate.getInstance().addRule("random-4-3", new RandomRule(4));
		final  Pattern getSequenseReg = Pattern.compile("[a-z]+:[0-9]+:[0-9]+");
		final  Pattern rollSequenseReg = Pattern.compile("[a-z]+@[0-9|,]+");
		ServerSocket server;
		Socket socket = null;
		try {
			server = new ServerSocket(65500);
			System.out.println("IDGenarate Server start at: "+new Date().toString());
			while (true) {
				socket = server.accept();
				new LongSocketThread(socket,getSequenseReg,rollSequenseReg).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		

	}

}
