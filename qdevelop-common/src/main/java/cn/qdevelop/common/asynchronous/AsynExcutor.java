package cn.qdevelop.common.asynchronous;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;

public class AsynExcutor extends ConcurrentLinkedQueue<Runnable>{


	private static Logger log = QLog.getLogger(AsynExcutor.class);

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
	
//	private Integer threadNum  = 1;

	/**
	 * 增加执行元素入口
	 * @param executor
	 * @return 
	 */
	@Override
	public boolean add(Runnable executor){
		this.offer(executor);
		if(!isRunning.get()){
			new Thread(){
				public void run(){
					aync();
				}
			}.start();
		}
		return true;
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
	
//	public static void main(String[] args) {
//		for(int i=0;i<1000;i++){
//			AsynExcutor.getInstance().add(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println(Thread.currentThread().getId());
//				}
//			});
//		}
//	}
}
