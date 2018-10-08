package cn.qdevelop.auth.common;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet(urlPatterns={"/sys/auth/loginout","/sys/auth/loginout.jsonp"})
public class AuthLoginOut  extends APIControl{

	private static final long serialVersionUID = 1L;

	
	@Override
	public void init(Map<String, String> args) {
		
	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
			//存入memcached做验证使用
			String sid = QServiceUitls.getCookie("sid", this.getRequest());
			if(sid!=null){
				XMemcached.getInstance().delete(sid);
			}
			output.setData("退出成功");
		return null;
	}
}
