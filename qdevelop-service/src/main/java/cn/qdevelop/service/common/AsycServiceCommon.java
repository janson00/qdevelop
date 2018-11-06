package cn.qdevelop.service.common;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import cn.qdevelop.service.interfacer.IOutput;

//@WebServlet(urlPatterns="/asyc/rest/*",
//loadOnStartup=1,initParams={  
//		@WebInitParam(name=IService.INIT_VALID_REQUIRED , value="index"),
//		@WebInitParam(name=IService.INIT_VALID_IGNORE , value="index")
//})
public class AsycServiceCommon  extends APIControl{

	private static final long serialVersionUID = -4735365187166909846L;

	private static Pattern getIndex = Pattern.compile("^.+\\/|\\.(json|jsonp)$");
	private String rightPath = "xxx";
	@Override
	public void init(Map<String, String> args) {
		String uri = getRequest().getRequestURI();
		String contextPath = getRequest().getContextPath(); 
		String index = getIndex.matcher(getRequest().getRequestURI()).replaceAll("");
		args.put("index", index);
		rightPath = uri.substring(contextPath.length()+6, uri.lastIndexOf("/"));
//		System.out.println(rightPath);
	}

	private static Pattern isWithNonCondition = Pattern.compile(" where "+Contant.AUTO_SEARCH_MARK+"$",Pattern.CASE_INSENSITIVE);
	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		try {
			//System.out.println(args);
			String path = SQLConfigParser.getInstance().getAttrValue(args.get("index"), "file");
			if(path.indexOf(rightPath) == -1){
				output.setErrMsg("非法访问请求，只能访问"+rightPath+"下文件配置");
				return null;
			}
			
			/***********处理输出结果集样式************/
			int resultFormatType =  0;
			if(args.get("result-format-type")!=null){
				resultFormatType = Integer.parseInt(args.get("result-format-type"));
				args.remove("result-format-type");
			}else{
				String rft = SQLConfigParser.getInstance().getAttrValue(args.get("index"), "result-format-type");
				if(rft!=null){
					resultFormatType = Integer.parseInt(rft);
				}
			}
			output.setFormatType(resultFormatType);
			/*************************************/
			
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
					if(isWithNonCondition.matcher(dbQuery.getSql()).find()){
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
