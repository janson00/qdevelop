package cn.qdevelop.plugin.idgenerate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.qdevelop.plugin.idgenerate.cores.GenerateCoreImpl;

public class Main {
	public static void main(String[] args) {
		ScheduledExecutorService scheduleExe = Executors.newScheduledThreadPool(1); 
		scheduleExe.scheduleWithFixedDelay(new Runnable(){
			public void run() {
				GenerateCoreImpl.getInstance().reloadProperties();
			}
		}, 10, 10, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				String path = GenerateCoreImpl.getInstance().shutdown(); 
				IDLogger.getInstance().log("[SHUTDOWN] store:",path);
			}
		});
		
		GenerateCoreImpl.getInstance();
		
		ServerSocket server;
		Socket socket = null;
		try {
			server = new ServerSocket(65500);
			IDLogger.getInstance().log("server start!");
			while (true) {
				socket = server.accept();
				new GenerateIDThread(socket).start();
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
