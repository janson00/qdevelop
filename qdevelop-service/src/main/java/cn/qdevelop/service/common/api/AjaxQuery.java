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

@WebServlet(urlPatterns="/svr/ajax/query",
		loadOnStartup=1,initParams={  
        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index"),
        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="")
})
//@WebServlet("/svr/ajax/query")
public class AjaxQuery extends APIControl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	protected String execute(Map<String, String> query,IOutput out) {
		try {
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
			out.setData(rb);
		} catch (QDevelopException e) {
			out.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		return IService.RETURN_OUT_JSON;
	}

	

}
