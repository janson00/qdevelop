package cn.qdevelop.plugin.idgenerate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class IDLogger  extends ConcurrentLinkedQueue<String>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8877571977255953005L;
	private static IDLogger _IDLogger = new IDLogger();
	public static IDLogger getInstance(){return _IDLogger;}
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	

	public void log(Object ... infos){
		StringBuilder sb = new StringBuilder();
		sb.append(new Date()).append(" :");
		for(Object v : infos){
			sb.append(" ").append(v);
		}
		this.offer(sb.append("\r\n").toString());
		if(!isRunning.get()){
			new Thread(){
				public void run(){
					aync();
				}
			}.start();
		}
	}
	
	private boolean aync(){
		if(isRunning.get() || this.isEmpty())return false;
		isRunning.set(true);
		String parentPath = "./logs";
		File path = new File(parentPath);
		if(!path.exists() || !path.isDirectory()){
			path.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder sb = new StringBuilder().append(parentPath).append("/running.").append(sdf.format(new Date())).append(".log");
		FileWriter fw = null ;
		try {
			fw = new FileWriter(sb.toString(),true);
			while(!this.isEmpty()){
				String query = this.poll();
				if(query==null)continue;
				//XXX 
				fw.write(query);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		isRunning.set(false);
		return true;
	}
}
