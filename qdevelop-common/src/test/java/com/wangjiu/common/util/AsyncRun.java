package com.wangjiu.common.util;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLogFactory;
import cn.qdevelop.common.QLogger;

public class AsyncRun {
	private static QLogger logger = QLogFactory.getQLogger(QLogFactory.class);
	private static Logger log = QLogFactory.getLogger(QLogFactory.class);
	public static void main(String[] args) {
		for(int i=0;i<100000;i++){
			logger.info(System.getProperties().toString());
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0;i<100000;i++){
			log.info(System.getProperties().toString());
		}
	}

}
