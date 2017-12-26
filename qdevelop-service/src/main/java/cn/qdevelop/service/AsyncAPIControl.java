package cn.qdevelop.service;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.service.interfacer.IService;

//@WebServlet(urlPatterns = "/async/test", asyncSupported = true,loadOnStartup=1,initParams={
//		//value内填写需要验证必须存在的参数，其他参数将自动按数据库内的格式自动校验,可将绝大部分数据验证自动处理了
////		@WebInitParam(name=IService.INIT_VALID_REQUIRED,value=""),
//		//value内填写忽略验证的参数，有些特殊参数可能会被误拦截为可疑hack字符
//		@WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
//})
public abstract class AsyncAPIControl  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8266031266081558118L;
	private String[] checkColumns,ignoreColumns;
	
	public void init(ServletConfig config)throws ServletException{  
		super.init(config);  
		String columns = config.getInitParameter(IService.INIT_VALID_REQUIRED);
		if(columns!=null){
			checkColumns=columns.split(",");
		}
		String ignore = config.getInitParameter(IService.INIT_VALID_IGNORE);
		if(ignore!=null){
			ignoreColumns=ignore.split(",");
		}else{
			ignoreColumns=(new String[]{});
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
			throws IOException, ServletException {
		
//        System.out.println("AsyncLongRunningServlet Start::Name="  
//                + Thread.currentThread().getName() + "::ID="  
//                + Thread.currentThread().getId());  
        
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        AsyncContext asyncCtx = request.startAsync();  
        addListener(asyncCtx);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) request  
                .getServletContext().getAttribute("executor");  
        AsyncAPIModel asyncProcess = getExecutor(asyncCtx);
        asyncProcess.setCheck(checkColumns, ignoreColumns);
        executor.execute(asyncProcess);  
    } 
	
	public void doPost(HttpServletRequest request, HttpServletResponse resp)
			throws IOException, ServletException {
		doGet(request,resp);
	}
	
	public abstract AsyncAPIModel getExecutor(AsyncContext asyncContext);
	
	public abstract void addListener(AsyncContext asyncContext);

	public String[] getCheckColumns() {
		return checkColumns;
	}

	public void setCheckColumns(String[] checkColumns) {
		this.checkColumns = checkColumns;
	}

	public String[] getIgnoreColumns() {
		return ignoreColumns;
	}

	public void setIgnoreColumns(String[] ignoreColumns) {
		this.ignoreColumns = ignoreColumns;
	}
	
}
