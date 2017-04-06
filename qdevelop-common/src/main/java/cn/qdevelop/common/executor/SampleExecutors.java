/**
 * 
 */
package cn.qdevelop.common.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author square
 * 
 */
public class SampleExecutors {
	private static ThreadPoolExecutor cachedThread = (ThreadPoolExecutor) Executors
			.newCachedThreadPool();
	private static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(10);

	public static ThreadPoolExecutor getCachedThread() {
		return cachedThread;
	}

	public static ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}
}
