package cn.qdevelop.service.common;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;

//@WebServlet(urlPatterns={"/svr/sys/ajax/query.json","/svr/sys/ajax/query.jsonp"},
//		loadOnStartup=1,initParams={  
//        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index"),
//        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
//})
@WebServlet("/status")
public class StatusQuery extends APIControl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	public void init(Map<String, String> args) {
	}

	@Override
	protected String execute(Map<String, String> query,IOutput out) {
		out.setData("");
		return null;
	}

}
