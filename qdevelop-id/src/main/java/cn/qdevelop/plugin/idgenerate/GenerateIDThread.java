package cn.qdevelop.plugin.idgenerate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.qdevelop.app.id.server.impl.IDTemplate;
import cn.qdevelop.plugin.common.IDRequest;
import cn.qdevelop.plugin.common.IDResponse;
import cn.qdevelop.plugin.idgenerate.bean.IDResponseBean;
import cn.qdevelop.plugin.idgenerate.cores.GenerateCoreImpl;

public class GenerateIDThread extends Thread{
	Socket socket;
	public GenerateIDThread(Socket socket){
		this.socket = socket;
	}

	public void run(){
		try{
			socket.setKeepAlive(true);
			//			while(true){
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			IDRequest req = (IDRequest)in.readObject();
			IDResponse  response = new IDResponseBean(req.getBuffer());
			String clientIP = socket.getRemoteSocketAddress().toString();
			switch(req.getOper()){
			case 0:// 获取ID操作
				int idx = IDTemplate.getInstance().get(req.getName(), response.getValues());
				if(idx>0){
					IDLogger.getInstance().log(clientIP,req.getName(),"get from temp ",idx);
				}
				if(req.getBuffer() - idx > 0){
					for(int i=idx;i<req.getBuffer();i++){
						response.setValues(i, GenerateCoreImpl.getInstance().getID(req));
					}
					IDLogger.getInstance().log(clientIP,req.getName(),"get from core ",(req.getBuffer()-idx));
				}
				break;
			case 1:// 还回ID操作
				IDTemplate.getInstance().put(req.getName(), req.getRollVals());
				response.setMsg(String.valueOf(req.getRollVals().length));
				IDLogger.getInstance().log(clientIP,req.getName(),"roll back ",response.getMsg());
				break;
			case 2:// 查看服务端数据监控情况
				response.setMsg(GenerateCoreImpl.getInstance().watch());
				socket.getInetAddress().getHostAddress();
				IDLogger.getInstance().log(clientIP,"watch status!");
				break;
			}
			out.writeObject(response);
			out.flush();
//			out.close();
//			in.close();
			//			}
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