package cn.qdevelop.service.common.api;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.IService;

@WebServlet(urlPatterns="/svr/sys/common/*",
loadOnStartup=1,initParams={  
@WebInitParam(name=IService.INIT_VALID_REQUIRED , value="index"),
@WebInitParam(name=IService.INIT_VALID_IGNORE , value="")
})
public class CommonIndexService  extends APIControl{

	private static final long serialVersionUID = -4735365187166909846L;

	private static Pattern getIndex = Pattern.compile("^.+\\/|\\.(json|jsonp)$");
	@Override
	public void init(Map<String, String> args) {
		String index = getIndex.matcher(getRequest().getRequestURI()).replaceAll("");
		args.put("index", index);
	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		try {
			boolean isSelect = SQLConfigParser.getInstance().isSelectByIndex(args.get("index"));
			if(isSelect){
				if(args.get("page")==null){
					args.put("page", "1");
				}
				if(args.get("limit")==null){
					args.put("limit", "10");
				}
				IDBResult rb = DatabaseFactory.getInstance().queryDatabase(args);
				output.setData(rb);
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
