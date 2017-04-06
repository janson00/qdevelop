package cn.qdevelop.common.schedule;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ClassName: MainServer <br/>
 * Description: TODO . <br/>
 * date: 4 Jun 2014 10:48:12 <br/>
 *
 * @author chenjianjun
 * @version
 */
public class ScheduleServer{
	
	private static final transient Logger LOG = LoggerFactory
			.getLogger(ScheduleServer.class);
	/**
	 * 加载服务集合.
	 */
	private static Map<String, ScheduleTask> serviceMaps = null;

	
	public static final int getScheduleCount(){
		if (serviceMaps==null) {
			return 0;
		}else {
			return serviceMaps.size();
		}
	}
	@PostConstruct
	public void startServer() {
		LOG.info("=============== 启动 shecdule  1.0 服务 ===============");
		if(null == getServiceMaps() || getServiceMaps().isEmpty()) {
			LOG.info("无启动服务.请配置...");
			return;
		}
		startServiceMaps();
		LOG.info("=============== 启动 shecdule 1.0 服务 完成===============");
	}
	
	private boolean startServiceMaps() {
		Set<Entry<String, ScheduleTask>> serviceSet = getServiceMaps().entrySet();
		for (Entry<String, ScheduleTask> entryService : serviceSet) {
			ScheduleTask service = entryService.getValue();
			if (service.getIsEnabled() && !service.isRunning()) {
				service.initTask();
				LOG.info("=============== 初始化  " + entryService.getKey() + " 服务完成===============");
				service.startService();
				service.setRunning(true);
				LOG.info("=============== 启动  " + entryService.getKey() + " 服务完成===============");
				
			}
		}
		return true;
	}

	public void stopServer() {
		String log = "=============== 停止order shecdule 1.0 服务 ===============";
		LOG.info(log);
		Set<Entry<String, ScheduleTask>> serviceSet = getServiceMaps().entrySet();
		for (Entry<String, ScheduleTask> entryService : serviceSet) {
			ScheduleTask service = entryService.getValue();
			if (service.getIsEnabled()) {
				service.stopService();
				LOG.info( "=============== 停止 " + entryService.getKey() + " 服务完成===============");
				service.setRunning(false);
			}
			
		}

		log = "=============== 停止order shecdule 1.0 服务完成 ===============";
		LOG.info(log);
	}

	/**
	 * @return the serviceMaps
	 */
	public Map<String, ScheduleTask> getServiceMaps() {
		return serviceMaps;
	}

	/**
	 * @param serviceMaps the serviceMaps to set
	 */
	public void setServiceMaps(Map<String, ScheduleTask> serviceMaps) {
		ScheduleServer.serviceMaps = serviceMaps;
	}

}
