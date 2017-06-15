package cn.qdevelop.plugin.id.client;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IDRateCalculator {
	
	AtomicLong lastRequestTimer = new AtomicLong(0);
	AtomicInteger currentSize = new AtomicInteger(0);
	

}
