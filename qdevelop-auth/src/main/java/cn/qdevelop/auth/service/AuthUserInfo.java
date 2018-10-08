package cn.qdevelop.auth.service;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet(urlPatterns={"/sys/auth/userInfo","/sys/auth/userInfo.jsonp"})
public class AuthUserInfo  extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 991588087992553783L;

	@Override
	public void init(Map<String, String> args) {

	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		String sid = QServiceUitls.getCookie("sid", this.getRequest());
		if(sid!=null){
			LoginInfo li = (LoginInfo)XMemcached.getInstance().get(sid);
			if(li!=null){
				try {
					IDBResult result = DatabaseFactory.getInstance().queryDatabase(
							new DBArgs("userInfos").put("uid", li.getUid())
							);
					if(result.getSize()>0){
						output.setData(result.getResult(0));
						return null;
					}
				} catch (QDevelopException e) {
					e.printStackTrace();
					output.setErrMsg(e.getMessage());
					return null;
				}finally{
					li = null;
				}

			}
		}
		output.setErrMsg("当前用户不存在，或未登陆！");
		return null;
	}

}
