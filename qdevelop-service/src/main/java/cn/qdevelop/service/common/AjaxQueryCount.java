package cn.qdevelop.service.common;

import java.util.Map;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.interfacer.IService;

//@WebServlet(urlPatterns={"/svr/sys/ajax/queryCount.json","/svr/sys/ajax/queryCount.jsonp"},
//	loadOnStartup=1,initParams={  
//        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index"),
//        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
//})
//@WebServlet("/svr/ajax/query")
public class AjaxQueryCount extends APIControl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	protected String execute(Map<String, String> query,IOutput result) {
		try {
			result.setFormatType(IOutput.SIMPLE_TYPE);
			int count = DatabaseFactory.getInstance().queryDatabaseCount(query);
			result.setData(count);
		} catch (QDevelopException e) {
			result.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		return IService.RETURN_OUT_JSON;
	}

	@Override
	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub
		
	}

}
