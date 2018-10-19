package cn.qdevelop.auth.filter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns={"/sys/*"})
public class SysPermitFilter extends PermitVerifierFilter{

	@Override
	protected String getLoginUrl() {
		return "/sys/login/login.html";
	}

	@Override
	protected String[] ignoreUris() {
//		return null;
		return new String[]{"/sys/auth/loginout$","/sys/auth/login$","/sys/auth/menu","/sys/auth/userInfo"};
	}

	@Override
	protected String getApiErrMsg() {
		return "您当前没有权限使用该接口";
	}
}
