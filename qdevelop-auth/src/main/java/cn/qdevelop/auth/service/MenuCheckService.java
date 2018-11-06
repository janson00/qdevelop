package cn.qdevelop.auth.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import cn.qdevelop.auth.utils.AuthUtils;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;

@WebServlet(urlPatterns={"/sys/auth/menuView"})
public class MenuCheckService extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4931335789813099582L;

	@Override
	public void init(Map<String, String> args) {

	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		String uid = args.get("uid");
		String permitId = args.get("permit_id");
		if(uid==null&&permitId==null){
			output.setErrMsg("至少含有一个参数");
			return null;
		}

		DBArgs param = new DBArgs();
		if(uid==null){
			param.put("index", "query_menu_permit_nonuser_menu_id").put("permit_id", permitId);
		}else{
			try {
				IDBResult rb = DatabaseFactory.getInstance().queryDatabase(new DBArgs("qd_auth_user_permit_relation_query_action")
						.put("uid", uid));
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<rb.getSize();i++){
					sb.append(i==0?"":",").append(rb.getObject(i, "permit_id"));
				}
				if(sb.length()==0){
					output.setErrMsg("该用户不存在，或者没设置权限。");
					return null;
				}
				param.put("index", "query_menu_permit_menu_id").put("uid", uid).put("permit_id", sb.toString());
			} catch (QDevelopException e) {
				// TODO Auto-generated catch block  
				e.printStackTrace();
			}
		}
		Connection conn = null;
		try {
			conn = DatabaseFactory.getInstance().getConnectByQuery("query_menu_permit_menu_id");
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(param,conn);
			if(rb.hasData()){
				StringBuffer sb = new StringBuffer();
				for(int i = 0 ; i < rb.getSize() ; i++){
					sb.append(i==0?"":",").append(rb.getObject(i, "menu_id"));
				}
				rb = null;
				rb = DatabaseFactory.getInstance().queryDatabase(
						new DBArgs("query_menu_permit_menu_result").put("menuIds", sb.toString())
						,conn);
				if(!rb.hasData()){
					output.setErrMsg("当前权限下用户没有获取到有效可用菜单");
					return null;
				}
				ArrayList<String> indexs = new ArrayList<String>();
				Map<String,ArrayList<Map<String,Object>>> temp = new HashMap<String,ArrayList<Map<String,Object>>>();
				for(int i=0;i<rb.getSize();i++){
					String key = rb.getString(i, "parent_menu_name")+","+rb.getString(i, "parent_menu_icon");
					ArrayList<Map<String,Object>> r = temp.get(key);
					if(r == null){
						r = new ArrayList<Map<String,Object>>();
						temp.put(key, r);
						indexs.add(key);
					}
					Map<String,Object> data = rb.getResult(i);
					data.remove("parent_menu_name");
					data.remove("parent_menu_icon");
					//替换动态域名
					data.put("menu_link", AuthUtils.relaceDynamicValue(rb.getString(i, "menu_link")));
					r.add(data);
				}

				ArrayList<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
				for(String key:indexs){
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("menu_name", key.substring(0, key.indexOf(",")));
					data.put("menu_icon", key.substring(key.indexOf(",")+1));
					data.put("childs", temp.get(key));
					result.add(data);
				}
				indexs.clear();
				temp = null;
				rb = null;
				output.setData(result);
				return null;
			}else{
				output.setErrMsg("当前用户权限下没有可用菜单");
				return null;
			}
		} catch (QDevelopException e) {
			e.printStackTrace();
			output.setErrMsg(e.getMessage());
			return null;
		}finally{
			DatabaseFactory.closeConnection(conn);
		}
	}

}
