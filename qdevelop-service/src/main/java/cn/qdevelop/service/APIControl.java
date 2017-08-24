package cn.qdevelop.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.service.utils.QServiceUitls;

/**
 * @WebServlet(
		//自定义接口URI，前端可直接访问；注意系统内不可重复，否则会启动报错
		urlPatterns="/myapp/requestUri",
		loadOnStartup=1,initParams={
				//value内填写需要验证必须存在的参数，其他参数将自动按数据库内的格式自动校验,可将绝大部分数据验证自动处理了
				@WebInitParam(name=IService.INIT_VALID_REQUIRED,value="page,limit"),
				//value内填写忽略验证的参数，有些特殊参数可能会被误拦截为可疑hack字符
				@WebInitParam(name=IService.INIT_VALID_IGNORE,value="")
		})
 */
/**
 * 抽象API服务接口类
 * @author janson
 *
 */
public abstract class APIControl extends HttpServlet implements IService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7445553533896016332L;
	private ThreadLocal<HttpServletResponse> httpServletResponse = new ThreadLocal<HttpServletResponse>();
	private ThreadLocal<HttpServletRequest> httpServletRequest = new ThreadLocal<HttpServletRequest>();
	
	private volatile String[] checkColumns,ignoreColumns;

	public void init(ServletConfig config)throws ServletException{  
		super.init(config);  
		String columns = config.getInitParameter(IService.INIT_VALID_REQUIRED);
		if(columns!=null){
			checkColumns = columns.split(",");
		}
		String ignore = config.getInitParameter(IService.INIT_VALID_IGNORE);
		if(ignore!=null){
			ignoreColumns = ignore.split(",");
		}else{
			ignoreColumns = new String[]{};
		}
	}

	/**
	 * 自定义初始化请求参数，在执行execute方法和验证参数之前，做参数的自定义处理
	 * @param files
	 */
	public abstract void init(Map<String,String> args);

	/**
	 * 统一执行入口
	 * @param args	当前请求的所有参数
	 * @param output	需要输出内容收集器
	 * @return 输出内容的格式 暂时没作用，后期增加其他
	 */
	protected abstract String execute(Map<String,String> args,IOutput output);

	/**
	 * 关闭参数校验
	 */
	protected void closeValidate(){
		this.jumpValidate = true;
	}
	
	private volatile boolean jumpValidate = false;
	private IOutput out ;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		httpServletResponse.set(response);
		httpServletRequest.set(request);
		out = QServiceUitls.getOutput(request, response);
		if(!out.isError()){
			Map<String,String> args = QServiceUitls.getParameters(request);
			init(args);
			if(jumpValidate || new QServiceUitls().validParameters(args,out,checkColumns,ignoreColumns)){
				execute(args,out);
			}
		}
		QServiceUitls.output(out.toString(), out.getOutType(), request, response);
	}
	
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request,response);
	}


	public HttpServletResponse getResponse(){
		return httpServletResponse.get();
	}

	public HttpServletRequest getRequest(){
		return httpServletRequest.get();
	}

	/**
	 * 获取全局输出内容收集器
	 * @return
	 */
	public IOutput getOutPut(){
		return out;
	}



}
