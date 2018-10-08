package cn.qdevelop.auth.service;

import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.interfacer.IService;

@WebServlet(
		//自定义接口URI，前端可直接访问；注意系统内不可重复，否则会启动报错
		urlPatterns={"/sys/menu/getRoleSetByMenuId","/sys/menu/getRoleSetByMenuId.json"},
		loadOnStartup=1,initParams={
				//value内填写需要验证必须存在的参数，其他参数将自动按数据库内的格式自动校验,可将绝大部分数据验证自动处理了
				@WebInitParam(name=IService.INIT_VALID_REQUIRED,value="menu_id"),
				//value内填写忽略验证的参数，有些特殊参数可能会被误拦截为可疑hack字符
				@WebInitParam(name=IService.INIT_VALID_IGNORE,value="")
		})
public class GetMenuPermitFromMenuID extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2286120734712593209L;

	@Override
	public void init(Map<String, String> args) {

	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		// TODO Auto-generated method stub
		//
		try {
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(new DBArgs("qd_auth_menu_permit_simple").put("menu_id", args.get("menu_id")));
			StringBuffer permitIds = new StringBuffer();
			StringBuffer userIds = new StringBuffer();
			if(rb.hasData()){
				for(int i=0;i<rb.getSize();i++){
					Integer permitId = rb.getInteger(i, "permit_id");
					Integer userId = rb.getInteger(i, "uid");
					if(permitId != null && permitId > 0){
						permitIds.append(";").append(permitId);
					}
					if(userId!=null && userId > 0){
						userIds.append(";").append(userId);
					}
				}
				rb = null;
			}
			rb = DatabaseFactory.getInstance().queryDatabase(new DBArgs("qd_auth_permit_query_action"));
			String indexs = new StringBuffer().append(permitIds).append(";").toString();
			for(int i=0;i<rb.getSize();i++){
				String permitId = rb.getString(i, "permit_id");
				if(indexs.indexOf(";"+permitId+";")>-1){
					rb.getResult(i).put("isChecked", true);
				}
			}
			//			output.setData(rb);
			output.addAttr("permitList", rb);
			output.addAttr("currentRole", new DBArgs()
					.put("permit_id", permitIds.length()>0?permitIds.substring(1).toString():"")
					.put("uid", userIds.length()>0?userIds.substring(1).toString():"")
					);
			if(userIds.length()>0){
				IDBResult users = DatabaseFactory.getInstance().queryDatabase(new DBArgs("qd_auth_users_simple").put("uid", userIds.substring(1).toString().replaceAll(";", "|")));
				output.addAttr("usersList", users);
			}
		} catch (QDevelopException e) {
			output.setErrMsg(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

}
