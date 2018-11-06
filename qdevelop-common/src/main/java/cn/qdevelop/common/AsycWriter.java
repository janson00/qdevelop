package cn.qdevelop.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsycWriter extends ConcurrentLinkedQueue<QLogBean>{
	private static final long serialVersionUID = -6561523792411246397L;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	File target;

	public AsycWriter(String file){
		target = new File(file);
		File parent = target.getParentFile();
		if(parent!=null && !parent.exists()){
			parent.mkdirs();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				isRunning.set(false);
				aync();
			}
		});
	}


	public void info(String ... val){
		asynExec(new QLogBean(1,val));
	}
	public void warn(String ... val){
		asynExec(new QLogBean(2,val));
	}
	public void error(String ... val){
		asynExec(new QLogBean(3,val));
	}

	/**
	 * 增加执行元素入口
	 * @param infos
	 */
	private void asynExec(QLogBean qlb){
		this.offer(qlb);
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
		try{
			while(!target.canWrite()){
				Thread.sleep(1000);
				if(target.canWrite()){
					break;
				}
			}
			BufferedWriter out=new BufferedWriter(new FileWriter(target,true));
			while(!this.isEmpty()){
				QLogBean query = this.poll();
				if(query==null)continue;
				out.write(query.toString());
				out.newLine();
			}
			out.close();


		} catch (Exception e){
			e.printStackTrace();
		}
		isRunning.set(false);
		return true;
	}

	public static void main(String[] args) throws InterruptedException {
		final AsycWriter aw = new AsycWriter("/data/logs/test.log");
		final AsycWriter aw2 = new AsycWriter("/data/logs/test.log");
		new Thread(){
			public void run(){
				for(int i=0;i<100;i++){
					aw.info(java.util.UUID.randomUUID().toString());
				}
			}
		};
		new Thread(){
			public void run(){
				for(int i=0;i<100;i++){
					aw2.warn(java.util.UUID.randomUUID().toString());
				}
			}
		};
		Thread.sleep(100);

	}
}