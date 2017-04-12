package cn.qdevelop.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.SQLConfigParser;
import cn.qdevelop.core.db.bean.DBStrutsLeaf;
import cn.qdevelop.service.bean.OutputJson;
import cn.qdevelop.service.utils.QServiceUitls;

/**
 * 抽象API服务接口类
 * @author janson
 *
 */
public abstract class APIControl extends HttpServlet implements IService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7445553533896016332L;
	private HttpServletResponse response;
	private HttpServletRequest request;
	private String[] checkColumns,ignoreColumns;

	public void init(ServletConfig config)throws ServletException{  
		super.init(config);  
		String columns = config.getInitParameter(IService.INIT_VALID_REQUIRED);
		if(columns!=null){
			checkColumns = columns.split(",");
		}
		String ignore = config.getInitParameter(IService.INIT_VALID_IGNORE);
		if(ignore!=null){
			ignoreColumns = ignore.split(",");
		}else{
			ignoreColumns = new String[]{};
		}
	}


	/**
	 * 统一执行入口
	 * @param args	当前请求的所有参数
	 * @param output	需要输出内容收集器
	 * @return 输出内容的格式 暂时没作用，后期增加其他
	 */
	protected abstract String execute(Map<String,String> args,IOutput output);

	private IOutput out ;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.response = response;
		this.request = request;
		out = new OutputJson();
		init(out);
		boolean isJsonp = request.getRequestURI().endsWith(".jsonp");
		if(isJsonp){
			String callback =  request.getParameter("callback");
			if(callback==null || callback.length()==0){
				QServiceUitls.output(new StringBuffer()
						.append("{\"tag\":1,\"errMsg\":\"请求参数[callback]不能为空\"}")
						.toString(),RETURN_OUT_JSON,request,response);
				return;
			}
		}
		Map<String,String> args = QServiceUitls.getParameters(request);
		if(validParameters(args)){
			execute(args,out);
			QServiceUitls.output(isJsonp?out.wrapper(request.getParameter("callback")+"(", ");"):out.toString(),RETURN_OUT_JSON,request,response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request,response);
	}

	private static Pattern isInteger = Pattern.compile("^[><=&\\^\\|0-9]+?$");
	private static Pattern isDouble = Pattern.compile("^[><=&\\^\\|\\.0-9]+?$");
	private static Pattern isTime = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}( [0-9]{2}:[0-9]{2}:[0-9]{2})?$");
	private static Pattern isAttackValue =
			Pattern.compile(
					"(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|delete|insert|trancate|char|substr|ascii|declare|exec|master|into|drop|execute)\\b)",
					Pattern.CASE_INSENSITIVE);

	/**
	 * 进行参数进行注入检查和数值类型校验；仅当args中含有index索引值时，才会根据数据库中，数据表中字段类型和当前参数进行类型校验
	 * @param args
	 * @return
	 */
	protected boolean validParameters(Map<String,String> args){
		if(checkColumns!=null){
			for(String s : checkColumns){
				if(args.get(s)==null){
					QServiceUitls.output(new StringBuffer()
							.append("{\"tag\":1,\"errMsg\":\"请求参数[").append(s).append("]不能为空\"}")
							.toString(),RETURN_OUT_JSON,request,response);
					return false;
				}
			}
		}
		if(args.get("index")!=null){
			try {
				Map<String,DBStrutsLeaf> struts = SQLConfigParser.getInstance().getDBStrutsLeafByIndex(args.get("index"));
				Iterator<Entry<String,String>> iter = args.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String,String> itor = iter.next();
					if(ArrayUtils.contains(ignoreColumns, itor.getKey())){
						continue;
					}
					DBStrutsLeaf dsf = struts.get(itor.getKey());
					String val = itor.getValue();
					if(dsf!=null){
						boolean isRightValue = true;
						if(dsf!=null){
							switch(dsf.getColumnType()){
							case 1:
							case 6:
								isRightValue  = isInteger.matcher(val).find();
								break;
							case 3:
								isRightValue = isDouble.matcher(val).find();
								break;
							case 4:
							case 5:
								isRightValue = isTime.matcher(val).find();
								break;
							default:
								isRightValue = !isAttackValue.matcher(val).find();	
							}
						}
						if(!isRightValue){
							QServiceUitls.output(new StringBuffer()
									.append("{\"tag\":1,\"errMsg\":\"请求参数[").append(itor.getKey()).append("=").append(val).append("]数据不合法或可能含有恶意字符\"}")
									.toString(),RETURN_OUT_JSON,request,response);
							return false;
						}else if(dsf.getSize() > 0 && val.length() > dsf.getSize()){
							QServiceUitls.output(new StringBuffer()
									.append("{\"tag\":1,\"errMsg\":\"请求参数[").append(itor.getKey()).append("=").append(val).append("]超长\"}")
									.toString(),RETURN_OUT_JSON,request,response);
							return false;
						}
					}else if(isAttackValue.matcher(val).find()){
						QServiceUitls.output(new StringBuffer()
								.append("{\"tag\":1,\"errMsg\":\"请求参数[").append(itor.getKey()).append("=").append(val).append("]可能含有恶意字符\"}")
								.toString(),RETURN_OUT_JSON,request,response);
					}
				}
			} catch (QDevelopException e) {
				e.printStackTrace();
			}
		}else{
			Iterator<Entry<String,String>> iter = args.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,String> itor = iter.next();
				if(ArrayUtils.contains(ignoreColumns, itor.getKey())){
					continue;
				}
				if(isAttackValue.matcher(itor.getValue()).find()){
					QServiceUitls.output(new StringBuffer()
							.append("{\"tag\":1,\"errMsg\":\"请求参数[").append(itor.getKey()).append("=").append(itor.getValue()).append("]可能含有恶意字符\"}")
							.toString(),RETURN_OUT_JSON,request,response);
				}
			}
		}
		return true;
	}

	public HttpServletResponse getResponse(){
		return response;
	}

	public HttpServletRequest getRequest(){
		return request;
	}

	/**
	 * 获取全局输出内容收集器
	 * @return
	 */
	public IOutput getOutPut(){
		return out;
	}

	/**
	 * 初始化
	 * @param out
	 */
	public void init(IOutput output){

	}

}
