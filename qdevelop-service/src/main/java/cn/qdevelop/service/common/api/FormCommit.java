package cn.qdevelop.service.common.api;

import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.IService;

@WebServlet(urlPatterns={"/svr/sys/ajax/formCommit.json","/svr/sys/ajax/formCommit.jsonp"},loadOnStartup=1,initParams={  
        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index"),
        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
})
public class FormCommit  extends APIControl {
	private static final long serialVersionUID = 5578926696733805453L;

	@Override
	protected String execute(Map<String, String> args, IOutput out) {
		boolean r;
		try {
			r = DatabaseFactory.getInstance().updateDatabase(args);
			out.setData(r);
		} catch (QDevelopException e) {
			out.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		return IService.RETURN_OUT_JSON;
	}

	@Override
	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub
		
	}

}
