package cn.qdevelop.common.asynchronous;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLogFactory;

public class AsynExcutor extends ConcurrentLinkedQueue<Runnable>{

	
	private static Logger log = QLogFactory.getLogger(AsynExcutor.class);
	
	private static final long serialVersionUID = -6561523792411246397L;
	private static AsynExcutor _AsynExcutor = new AsynExcutor();
	public static AsynExcutor getInstance(){return _AsynExcutor;}
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	public AsynExcutor(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
	        public void run() {
	        	isRunning.set(false);
	        	aync();
	        	log.info("shutdown AsynExcutor execute!");
	        }
	    });
	}
	
	/**
	 * 增加执行元素入口
	 * @param executor
	 */
	public void asynExec(Runnable executor){
		this.offer(executor);
		if(!isRunning.get()){
			new Thread(){
				public void run(){
					aync();
				}
			}.start();
		}
	}
	
	public boolean aync(){
		if(isRunning.get() || this.isEmpty())return false;
		isRunning.set(true);
		long s = System.currentTimeMillis();
		int idx = 0;
		while(!this.isEmpty()){
			Runnable query = this.poll();
			if(query==null)continue;
			query.run();
			++idx;
		}
		isRunning.set(false);
		log.info(new StringBuffer().append("running ").append(idx).append(" mission; use ").append(System.currentTimeMillis()-s).append(" ms").toString());
		return true;
	}
}
