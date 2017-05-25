package cn.qdevelop.service.common.api;

import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.IService;

@WebServlet(urlPatterns={"/svr/sys/ajax/query.json","/svr/sys/ajax/query.jsonp"},
		loadOnStartup=1,initParams={  
        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index"),
        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
})
//@WebServlet("/svr/ajax/query")
public class AjaxQuery extends APIControl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	protected String execute(Map<String, String> query,IOutput out) {
		/** default pagination **/
		if(query.get("page")==null){
			query.put("page", "1");
		}
		if(query.get("limit")==null){
			query.put("limit", "10");
		}
		try {
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
			out.setData(rb);
		} catch (QDevelopException e) {
			out.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		return IService.RETURN_OUT_JSON;
	}

	@Override
	public void init(Map<String, String> args) {
		
	}

	

}
