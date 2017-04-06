package cn.qdevelop.common.schedule;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QScheduleFactory {

	private static QScheduleFactory _QScheduleFactory = new QScheduleFactory();

	public static QScheduleFactory getInstance() {
		return _QScheduleFactory;
	}

	private ScheduledExecutorService scheduleExe = Executors.newScheduledThreadPool(8);

	/**
	 * 增加任务调度接口
	 * 
	 * @param schedule
	 *            任务接口
	 * @param afterTimer
	 *            多久后开始执行 秒
	 * @param loopTimer
	 *            每个几秒执行一次
	 * @return
	 */
	public boolean addSchedule(Runnable schedule, long afterTimer, long loopTimer) {
		scheduleExe.scheduleWithFixedDelay(schedule, afterTimer, loopTimer, TimeUnit.SECONDS);
		return true;
	}

	/**
	 * 给定开始时间日期，定时执行程序
	 * 
	 * @param schedule
	 * @param begineTimer
	 * @param loopTimer
	 * @return
	 */
	public boolean addSchedule(Runnable schedule, Date begineTimer, long loopTimer) {
		long afterTimer = begineTimer.getTime() - System.currentTimeMillis();
		if (afterTimer < 0)
			return false;
		scheduleExe.scheduleWithFixedDelay(schedule, afterTimer, loopTimer * 1000,
				TimeUnit.MILLISECONDS);
		return true;
	}

	public ScheduledExecutorService getScheduleExecutor() {
		return scheduleExe;
	}

	public boolean shutdown() {
		if (!scheduleExe.isShutdown() || !scheduleExe.isTerminated()) {
			scheduleExe.shutdown();
			try {
				scheduleExe.awaitTermination(300, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

}
