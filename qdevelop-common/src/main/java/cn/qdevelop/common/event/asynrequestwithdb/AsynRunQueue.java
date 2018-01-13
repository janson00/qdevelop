package cn.qdevelop.common.event.asynrequestwithdb;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.event.IRequest;
import cn.qdevelop.common.schedule.QScheduleFactory;

public class AsynRunQueue extends ConcurrentLinkedQueue<AsynRunBean> {

	IRequestQueue requestQueue;

	String sysName;

	public AsynRunQueue(String sysName, IRequestQueue requestQueue) {
		this.requestQueue = requestQueue;
		this.sysName = sysName == null ? this.getClass().getName() : sysName;
		QScheduleFactory.getInstance().addSchedule(new Runnable() {
			public void run() {
				reload();
			}
		}, 10, 60);// 系统自动每分钟去获取错误未跑完的数据请求
	}

	public void reload() {
		List<AsynRunBean> nonComleteRequests = requestQueue
				.loadNonComplete(sysName);
		long currentTime = System.currentTimeMillis();
		int idx = 0;
		for (AsynRunBean rqb : nonComleteRequests) {
			if (rqb.getStatus() == 0) {
				if (rqb.getRetryTime() > 20) {// 最多retry 20次
					requestQueue.requestTimeout(rqb.getRqid());
				} else if (currentTime - rqb.getCreateTime().getTime() > rqb
						.getRetryTime() * 120000
						&& requestQueue.isCanRunning(rqb.getRqid()) == 1) {// 随着错误次数增多，间隔时间越长，每错一次，多增加2分钟等待时间
					++idx;
					this.offer(rqb);
				}
			} else {
				if (currentTime - rqb.getLoadTime().getTime() > 300000) {
					requestQueue.resetRequest(rqb.getRqid());
				}
			}
		}
		log.info("load [" + idx + "] nonComplete request record success!");
		if (!isRunning) {
			new Thread() {
				public void run() {
					sync();
				}
			}.start();
		}

	}

	private static final long serialVersionUID = -5578417746089072721L;

	private static final transient Logger log = QLog.getLogger(AsynRunQueue.class);

	private boolean isRunning = false;

	/**
	 * 
	 * @Description: 返回当期任务ID
	 * @Title: addRequest
	 * @param @param mission
	 * @param @return
	 * @param @throws Exception
	 * @return int
	 * @throws
	 * @author
	 * @date 2013-9-22 上午11:41:03
	 */
	public int addRequest(IRequest mission) throws Exception {
		try {
			AsynRunBean rqb = new AsynRunBean(mission);
			rqb.setSysName(sysName);
			requestQueue.storeRequest(rqb);
			this.offer(rqb);
			if (!isRunning) {
				new Thread() {
					public void run() {
						sync();
					}
				}.start();
			}
			return rqb.getRqid();
		} catch (Exception e) {
			log.error("add request to queue error!", e);
			throw e;
		}
	}

	public void clearRequest(int requestId) {
		requestQueue.requestComplete(requestId);
	}

	public boolean sync() {
		if (isRunning || this.isEmpty())
			return false;
		isRunning = true;
		while (!this.isEmpty()) {
			AsynRunBean mission = this.poll();
			try {
				long cur = System.currentTimeMillis();
				IRequest rp = mission.getRpcRequest();
				rp.run();
				log.info(rp.toString() + " use "
						+ (System.currentTimeMillis() - cur) + "ms");
				requestQueue.requestComplete(mission.getRqid());
			} catch (Exception e) {
				requestQueue.requestError(mission.getRqid());
				log.error("RpcRequest running error!", e);
			}
		}
		isRunning = false;
		return true;
	}
}
