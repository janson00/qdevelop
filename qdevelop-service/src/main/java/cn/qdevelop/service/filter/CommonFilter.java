package cn.qdevelop.service.filter;

import java.io.IOException;

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

@WebFilter(urlPatterns="/svr/*")
public class CommonFilter  implements Filter{
//	private static Logger log = QLog.getLogger(CommonFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("system load on init!");
		QDevelopUtils.initAll();  
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		long s = System.currentTimeMillis();
		request.setAttribute("__startTime", s);
		/**给每个访问打唯一标识，一年过期时间**/
		String sid = QServiceUitls.getCookie("sid",(HttpServletRequest)request);
		if(sid==null){
			QServiceUitls.setCookie((HttpServletResponse)response, "sid", java.util.UUID.randomUUID().toString(), 60*60*24*365);
		}
		//			request.setCharacterEncoding("UTF-8");
		//			response.setCharacterEncoding("UTF-8");
		chain.doFilter(request,response);
//		log
	}
	


	@Override
	public void destroy() {
	}

}
