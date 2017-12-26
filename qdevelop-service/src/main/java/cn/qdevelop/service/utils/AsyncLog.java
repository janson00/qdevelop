package cn.qdevelop.service.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLogFactory;

public class AsyncLog implements Runnable{
	private static Logger log = QLogFactory.getLogger(AsyncLog.class);
	
	HttpServletRequest request;long start;
	
	public AsyncLog(HttpServletRequest request,long start){
		this.request = request;
		this.start = start;
	}
	
	@Override
	public void run() {
		log.info(new StringBuffer().append(request.getRequestURI())
				.append("?").append(request.getQueryString())
				.append(" use ").append(System.currentTimeMillis()-start).append(" ms")
				.toString());
//		request.getQueryString();
//		System.out.println(request.getQueryString());
	}

}
