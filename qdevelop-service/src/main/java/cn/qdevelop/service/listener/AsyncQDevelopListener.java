package cn.qdevelop.service.listener;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;

public class AsyncQDevelopListener implements AsyncListener {  

	public void onComplete(AsyncEvent asyncEvent) throws IOException {  
		System.out.println("AppAsyncListener onComplete");  
	}  

	public void onError(AsyncEvent asyncEvent) throws IOException {  
		System.out.println("AppAsyncListener onError");  
	}  

	public void onStartAsync(AsyncEvent asyncEvent) throws IOException {  
		System.out.println("AppAsyncListener onStartAsync");  
	}  

	public void onTimeout(AsyncEvent asyncEvent) throws IOException {  
		System.out.println("AppAsyncListener onTimeout");  
		ServletResponse response = asyncEvent.getAsyncContext().getResponse();  
		PrintWriter out = response.getWriter();  
		out.write("TimeOut Error in Processing");  
	}  

}
