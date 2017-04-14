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

@WebServlet(urlPatterns={"/svr/ajax/dataTables.json","/svr/ajax/dataTables.jsonp"},
	loadOnStartup=1,initParams={  
        @WebInitParam(name=IService.INIT_VALID_REQUIRED,value="index,page"),
        @WebInitParam(name=IService.INIT_VALID_IGNORE,value="index")
})
public class DataTables extends APIControl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	protected String execute(Map<String, String> query,IOutput result) {
		try {
			String draw = query.get("draw") == null ? "1" : String.valueOf(query.get("draw"));
			int page = query.get("page") == null ? 1 : Integer.parseInt(String.valueOf(query.get("page")));
			String limit = query.get("page_size") == null ? query.get("limit") : query.get("page_size");
			int pageSize = limit == null ? 10 : Integer.parseInt(limit);

			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
			int recordsFiltered = rb.getSize() == pageSize ? page*pageSize+1 : (page-1)*pageSize + rb.getSize();
			result.setData(rb);
			result.addAttr("draw", draw);
			result.addAttr("recordsTotal", rb.getSize());
			result.addAttr("recordsFiltered", recordsFiltered);
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
