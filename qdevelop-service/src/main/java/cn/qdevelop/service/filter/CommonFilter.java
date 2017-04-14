package cn.qdevelop.service.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.core.QDevelopUtils;
import cn.qdevelop.service.utils.QServiceUitls;

@WebFilter(urlPatterns="/*")
public class CommonFilter  implements Filter{
//	private static Logger log = QLog.getLogger(CommonFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("system load on init!");
		QDevelopUtils.initAll();  
	}

	private static Pattern isMark  = Pattern.compile("sid=[A-Za-z0-9-]{36};?");
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setAttribute("__startTime", System.currentTimeMillis());
		/**给每个访问打唯一标识，一年过期时间**/
		if(!isMark.matcher(((HttpServletRequest)request).getHeader("Cookie")).find()){
			QServiceUitls.setCookie((HttpServletResponse)response, "sid", java.util.UUID.randomUUID().toString(), 60*60*24*365);
		}
		chain.doFilter(request,response);
	}
	


	@Override
	public void destroy() {
	}
	
}
