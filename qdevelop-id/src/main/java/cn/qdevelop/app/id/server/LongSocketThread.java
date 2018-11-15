package cn.qdevelop.app.id.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

import cn.qdevelop.app.id.server.impl.IDGenerate;
import cn.qdevelop.app.id.server.impl.IDTemplate;

public class LongSocketThread extends Thread{
	Socket socket;Pattern reg;Pattern rollReg;
	public LongSocketThread(Socket socket,Pattern reg,Pattern rollReg){
		this.socket = socket;
		this.reg = reg;
		this.rollReg = rollReg;
	}
	public void run(){
		try{
			socket.setKeepAlive(true);
			while(true){
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				String str = in.readLine(); 
				if(str!=null){
					if(reg.matcher(str).find()){
//						System.out.println("get >> "+str);
						String[] tmp = str.split(":");
						String name = tmp[0];
						int digit = Integer.parseInt(tmp[1]);
						int num = Integer.parseInt(tmp[2]);
						Long[] val = new Long[num];
						int idx = IDTemplate.getInstance().get(name, val);//从临时队列中取数据
//						if(idx>0)System.out.println(name+" >> temp get: "+idx);
						for(int i=idx;i<num;i++){
							val[i] = IDGenerate.getInstance().getID(name,digit);
						}
						out.writeObject(val);
					}else if(rollReg.matcher(str).find()){
						String name = str.substring(0,str.indexOf("@"));
						String[] vals = str.substring(str.indexOf("@")+1).split(",");
						IDTemplate.getInstance().put(name, vals);
						out.writeObject("");
					}else if(str.equals("shutdown")){
						IDGenerate.getInstance().shutdown();
						System.exit(0);
					}else if(str.equals("status")){
						out.writeObject("\r\n");
						out.writeObject(IDGenerate.getInstance().watch());
						out.writeObject("\r\n");
					}else{
						out.writeObject("");
					}
					out.flush();
				}
				if(str == null || (!reg.matcher(str).find() && !rollReg.matcher(str).find())){
					break;
				}
			}
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