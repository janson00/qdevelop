package cn.qdevelop.auth.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.AuthUtils;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet(urlPatterns={"/sys/auth/menu","/sys/auth/menu.jsonp"})
public class MenuService extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4931335789813099582L;

	@Override
	public void init(Map<String, String> args) {

	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		String sid = QServiceUitls.getCookie("sid", this.getRequest());
		if(sid!=null){
			LoginInfo li = (LoginInfo)XMemcached.getInstance().get(sid);
			if(li!=null){
				Connection conn = null;
				try {
					conn = DatabaseFactory.getInstance().getConnectByQuery("query_menu_permit_menu_id");
					IDBResult rb = DatabaseFactory.getInstance().queryDatabase(
							new DBArgs("query_menu_permit_menu_id").put("uid", li.getUid()).put("permit_id", li.getPermitId())
							,conn);

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
					li = null;
					DatabaseFactory.closeConnection(conn);
				}

			}
		}
		output.setErrMsg("当前用户不存在，或未登陆！");
		return null;
	}

}
