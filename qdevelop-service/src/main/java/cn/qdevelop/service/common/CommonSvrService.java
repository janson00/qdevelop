package cn.qdevelop.service.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.interfacer.IService;

@WebServlet(urlPatterns="/svr/sys/common/*",
loadOnStartup=1,initParams={  
@WebInitParam(name=IService.INIT_VALID_REQUIRED , value="index"),
@WebInitParam(name=IService.INIT_VALID_IGNORE , value="index")
})
public class CommonSvrService  extends APIControl{

	private static final long serialVersionUID = -4735365187166909846L;

	private static Pattern getIndex = Pattern.compile("^.+\\/|\\.(json|jsonp)$");
	private static Pattern isOpenPath = Pattern.compile("\\/?common-svr-sqls\\/");
	@Override
	public void init(Map<String, String> args) {
		String index = getIndex.matcher(getRequest().getRequestURI()).replaceAll("");
		args.put("index", index);
	}
	
	
	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		try {
			String path = SQLConfigParser.getInstance().getAttrValue(args.get("index"), "file");
			if(!isOpenPath.matcher(path).find()){
				output.setErrMsg("非法访问请求，只能访问common-svr-sqls下文件");
				return null;
			}
			boolean isSelect = SQLConfigParser.getInstance().isSelectByIndex(args.get("index"));
			if(isSelect){
				if(args.get("page")==null){
					args.put("page", "1");
				}
				if(args.get("limit")==null){
					args.put("limit", "10");
				}
				int limit = Integer.parseInt(args.get("limit"));
				int page = Integer.parseInt(args.get("page"));
				int total;
				Map<String,String> totalQuery =  null ;
				if(args.get("is-need-total")!=null && args.get("is-need-total").equals("true")){
					totalQuery = new HashMap<String,String>(args);
				}
				args.put("is-convert-null", "true");
				
				
				IDBResult rb = DatabaseFactory.getInstance().queryDatabase(args);
				output.setData(rb);
				if(totalQuery != null){
					total = DatabaseFactory.getInstance().queryDatabaseCount(totalQuery);
				}else{
					total = rb.getSize() < limit ? (page-1)*limit+rb.getSize() : page*limit+1;  
				}
				output.addAttr("page", page);
				output.addAttr("limit", limit);
				output.addAttr("next", total > limit*page ? page+1 : page);
				output.addAttr("total", total);
			}else{
				boolean r = DatabaseFactory.getInstance().updateDatabase(args);
				output.setData(r);
			}
		} catch (QDevelopException e) {
			output.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
