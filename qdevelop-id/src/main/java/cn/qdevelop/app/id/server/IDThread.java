package cn.qdevelop.app.id.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

import cn.qdevelop.app.id.server.impl.IDGenerate;

public class IDThread extends Thread{
	Socket socket;Pattern reg;
	public IDThread(Socket socket,Pattern reg){
		this.socket = socket;
		this.reg = reg;
	}
	public void run(){
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			String str = in.readLine(); 
			if(reg.matcher(str).find()){
				String[] tmp = str.split(":");
				int num = Integer.parseInt(tmp[2]);
				int digit = Integer.parseInt(tmp[1]);
				Long[] val = new Long[num];
				for(int i=0;i<num;i++){
					val[i] = IDGenerate.getInstance().getID(tmp[0],digit);
				}
				out.writeObject(val);
			}else if(str.equals("shutdown")){
				IDGenerate.getInstance().shutdown();
				System.exit(0);
			}else if(str.equals("status")){
				out.writeChars("\r\n");
				out.writeChars(IDGenerate.getInstance().watch());
				out.writeChars("\r\n");
			}
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(socket!=null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
