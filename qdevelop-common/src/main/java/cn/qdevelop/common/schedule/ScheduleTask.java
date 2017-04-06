package cn.qdevelop.common.schedule;

import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ScheduleTask {
	
	private static final transient Logger LOG = LoggerFactory
			.getLogger(ScheduleTask.class);
	
	/**
	 * 调度池
	 */
	protected ScheduledExecutorService schExe = QScheduleFactory.getInstance().getScheduleExecutor();
	
	/**
	 * 是否启用
	 */
	private boolean isEnabled = false;
	
	/**
	 * 是否启用
	 */
	private boolean isRunning = false;
	
	/**
	 * 任务
	 */
	protected Runnable task;
	/**
	 * 任务定义
	 */
	 void initTask(){
		task = new Runnable() {
			@Override
			public void run() {
				try {
					initService();
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("ScheduleTask调度出错",e);
					// TODO: handle exception
				}
				
			}
		};
	}
	/**
	 * 初始化服务.
	 * @return
	 */
	public abstract void initService();
	
	/**
	 * 启动服务.
	 * @return
	 */
	public abstract void startService();
	
	/**
	 * 停止服务.
	 * @return
	 */
	public abstract boolean stopService();
	
	/**
	 * 服务是否激活.true - 激活; false - 不激活.
	 * @param isEnabled
	 * @return
	 */
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public boolean getIsEnabled(){
		return this.isEnabled;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}

	