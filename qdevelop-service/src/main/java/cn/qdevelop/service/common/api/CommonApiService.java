package cn.qdevelop.service.common.api;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.Contant;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.connect.ConnectFactory;
import cn.qdevelop.core.db.execute.DatabaseImpl;
import cn.qdevelop.core.standard.IDBQuery;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.IService;

@WebServlet(urlPatterns="/api/sys/common/*",
loadOnStartup=1,initParams={  
		@WebInitParam(name=IService.INIT_VALID_REQUIRED , value="index"),
		@WebInitParam(name=IService.INIT_VALID_IGNORE , value="index")
})
public class CommonApiService  extends APIControl{

	private static final long serialVersionUID = -4735365187166909846L;

	private static Pattern getIndex = Pattern.compile("^.+\\/|\\.(json|jsonp)$");
	private static Pattern isOpenPath = Pattern.compile("\\/?common-api-sqls\\/");
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
				output.setErrMsg("非法访问请求，只能访问common-api-sqls下文件");
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
				IDBResult result;
				DatabaseFactory dbf = new DatabaseFactory();
				Connection conn = dbf.getConnectByQuery(args);
				try {
					dbf.formatterParameters(args);
					IDBQuery dbQuery = SQLConfigParser.getInstance().getDBQueryBean(args, conn);
					
					if(dbQuery.getSql().indexOf(Contant.AUTO_SEARCH_MARK)>-1){
						output.setErrMsg("当前查询接口有风险，不能够直接使用，请先明确配置变量名称！");
						return null;
					}
					
					result = new DBResultBean();
					new DatabaseImpl().queryDB(conn, dbQuery, result);
					dbf.formatterResult(dbQuery.getIndex(),result);
					dbQuery.clear();
					dbQuery=null;
				} catch (QDevelopException e) {
					throw e;
				}finally{
					args.clear();
					ConnectFactory.close(conn);
				}

				if(result!=null){
					output.setData(result);
					if(totalQuery != null){
						total = DatabaseFactory.getInstance().queryDatabaseCount(totalQuery);
					}else{
						total = result.getSize() < limit ? (page-1)*limit+result.getSize() : page*limit+1;  
					}
					output.addAttr("page", page);
					output.addAttr("limit", limit);
					output.addAttr("next", total > limit*page ? page+1 : page);
					output.addAttr("total", total);
				}
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
