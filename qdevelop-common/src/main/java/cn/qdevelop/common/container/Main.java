/**
 * 
 */
package cn.qdevelop.common.container;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.qdevelop.common.utils.QProperties;

/**
 * @author square
 *
 */
public class Main {
	public static final String CONTAINER_KEY = "wangjiu.container";

	public static final String SHUTDOWN_HOOK_KEY = "wangjiu.shutdown.hook";

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static volatile boolean running = true;

	public static void main(String[] args) {
		try {
			final List<Container> containers = new ArrayList<Container>();
			//暂时先只支持一个容器
			containers.add((Container)Class.forName(QProperties.getInstance().getProperty(CONTAINER_KEY)).newInstance());
			if ("true".equals(QProperties.getInstance().getProperty(SHUTDOWN_HOOK_KEY))) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						for (Container container : containers) {
							try {
								container.stop();
								logger.info("wangjiu container " + container.getClass().getSimpleName() + " stopped!");
							} catch (Throwable t) {
								logger.error(t.getMessage(), t);
							}
							synchronized (Main.class) {
								running = false;
								Main.class.notify();
							}
						}
					}
				});
			}

			for (Container container : containers) {
				container.start();
				logger.info("wangjiu container " + container.getClass().getSimpleName() + " started!");
			}
			System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date())
					+ " wangjiu service server started!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
		synchronized (Main.class) {
			while (running) {
				try {
					Main.class.wait();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
}
