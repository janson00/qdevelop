package cn.qdevelop.auth.common;

import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet(urlPatterns={"/sys/auth/thirdValid","/sys/auth/thirdValid.jsonp"})
public class ThirdValidService extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3728246926084149730L;

	@Override
	public void init(Map<String, String> args) {
		
	}
	
	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		String url = args.get("url");
		if(url == null){
			url = this.getRequest().getHeader("Referer");
		}
		if(url == null){
			output.setErrMsg("来源Url分析错误，请带入需要验证的url");
			output.setTag(500);
			return null;
		}
//		if(url.startsWith("http://")){
//			url = url.substring(7);
//			url = url.substring(url.indexOf("/"));
//		}
		HttpServletRequest request = getRequest();
		String sid = QServiceUitls.getCookie("sid", request);
		if(sid == null ){
			sid = java.util.UUID.randomUUID().toString();
			QServiceUitls.setCookie(this.getResponse(), "sid", sid , 60*60*24*365);
			output.setErrMsg("请先登陆后再次访问。");
			output.setTag(503);
			return null;
		}
		
		LoginInfo li = (LoginInfo)XMemcached.getInstance().get(sid);
		boolean hasPermit = li != null && li.hasUriPermit(url) ;
		if(li!=null&&li.isNeedResetSession()){
			XMemcached.getInstance().set(sid, li, XMemcached.EXP_30Min);
		}
		if(hasPermit){
			output.setData("success");
			output.setErrMsg("success");
			output.setTag(200);
		}else{
			output.setData(url);
			output.setErrMsg("当前用户没有该页面访问权限");
			output.setTag(403);
		}
		return null;
	}

}
