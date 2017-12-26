package cn.qdevelop.common;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

public class QLogger extends ConcurrentLinkedQueue<QLogBean>{
	private static final long serialVersionUID = -6561523792411246397L;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	Logger logger;

	public QLogger(Logger logger){
		this.logger = logger;
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//			public void run() {
//				isRunning.set(false);
//				aync();
//			}
//		});
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
	public void asynExec(QLogBean qlb){
		this.offer(qlb);
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
		while(!this.isEmpty()){
			QLogBean query = this.poll();
			if(query==null)continue;
			switch(query.getType()){
			case 1:
				logger.info(query.getInfos());
				break;
			case 2:
				logger.warn(query.getInfos());
				break;
			case 3:
				logger.error(query.getInfos());
				break;
			}
		}
		isRunning.set(false);
		return true;
	}
}