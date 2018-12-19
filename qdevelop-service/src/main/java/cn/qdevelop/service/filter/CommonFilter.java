package cn.qdevelop.service.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.files.QSource;
import cn.qdevelop.core.QDevelopUtils;
import cn.qdevelop.service.interfacer.IService;
import cn.qdevelop.service.utils.QServiceHelper;
import cn.qdevelop.service.utils.QServiceUitls;

@WebFilter(urlPatterns="/*")
public class CommonFilter  implements Filter{
	private static Logger log = QLog.getLogger(CommonFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("system load on init!");
		QDevelopUtils.initAll(); 
		initApiData();
		initPressCollection();
	}

	private static Pattern isMark  = Pattern.compile("sid=[A-Za-z0-9-]{36};?");

	private static Boolean isCollectPressData = new Boolean(false);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setAttribute("__startTime", System.currentTimeMillis());
		HttpServletRequest req = (HttpServletRequest)request;
		String cookie = req.getHeader("Cookie");

		/**给每个访问打唯一标识，一年过期时间**/
		if(cookie == null || !isMark.matcher(cookie).find()){
			QServiceUitls.setCookie(req,(HttpServletResponse)response, "sid", java.util.UUID.randomUUID().toString(), 60*60*24*365);
		}
		/*当有假结果数据时，优先使用假结果数据集*/
		if(apiDatas!=null){
			if(idx == null){
				idx = req.getContextPath().length();
			}
			String val = apiDatas.get(clean.matcher(req.getRequestURI().substring(idx)).replaceAll(""));
			if(val!=null){
				HttpServletResponse rpo = (HttpServletResponse)response;
				StringBuffer out = new StringBuffer().append("{")
						.append("\"tag\":0").append(",")
						.append("\"data\":").append(val).append(",")
						.append("\"errMsg\":\"").append("").append("\"");

				out.append("}").toString();
				QServiceUitls.output(out.toString(), IService.RETURN_OUT_JSON, req, rpo);
				return;
			}
		}

		if(isCollectPressData){
			collectData(req);
		}

		chain.doFilter(request,response);
	}

	private static Integer idx ;

	@Override
	public void destroy() {
	}
	private static Pattern clean = Pattern.compile("^\\/");

	Map<String,String> apiDatas = null;

	private void initApiData(){
		Properties prop = QSource.getInstance().loadProperties("plugin-config/api-demo-datas.properties",this.getClass());
		if(prop!=null && prop.size()>0){
			apiDatas = new HashMap<String,String>();
			Enumeration<?> enu = prop.propertyNames();  
			while (enu.hasMoreElements()) {  
				String key = (String)enu.nextElement();  
				apiDatas.put(clean.matcher(key).replaceAll(""), prop.getProperty(key));
			}  
		}
		if(apiDatas!=null && apiDatas.size()==0){
			apiDatas.clear();
			apiDatas = null;
		}
	}

	private void initPressCollection(){
		Properties prop = QSource.getInstance().loadProperties("plugin-config/qdevelop_service.properties",this.getClass());
		if(prop!=null && prop.size()>0){
			if(prop.getProperty("is-collect-press-data")!=null){
				isCollectPressData = Boolean.parseBoolean(prop.getProperty("is-collect-press-data"));
			}
		}
	}
	private static QServiceHelper qsh;
	protected void collectData(HttpServletRequest req){
		if(qsh == null){
			qsh = new QServiceHelper();
		}
		int port = req.getServerPort();
		StringBuilder uri = new StringBuilder();
		uri.append("[").append(qsh.getUserIP(req)).append("] ").append(" [CommonFilter] ");
		if(port==443){
			uri.append("https://");
		}else{
			uri.append("http://");
		}
		uri.append(req.getRemoteHost());
		if(port!=80 && port!=443){
			uri.append(":").append(port);
		}
		uri.append(req.getRequestURI());
		uri.append("?");
		Enumeration<?> paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String key = (String) paramNames.nextElement();
			uri.append(key).append("=").append(req.getParameter(key)).append("&");
		}
		
		uri.append(" COOKIES=[ ");
		Cookie[] cookies = req.getCookies();
		for(int i=0;cookies!=null && i<cookies.length;i++) {
			uri.append(i>0?"&":"").append(cookies[i].getName()).append("=").append(cookies[i].getValue());
		}
		uri.append(" ]");
		
		log.info(uri.toString());
		
		

	}

}
