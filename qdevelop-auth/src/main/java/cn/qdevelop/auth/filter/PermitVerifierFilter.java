package cn.qdevelop.auth.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.AuthUtils;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.common.QLog;
import cn.qdevelop.common.files.QProperties;
import cn.qdevelop.service.bean.OutputJson;
import cn.qdevelop.service.utils.QServiceUitls;

//@WebFilter(urlPatterns={"/*"})
/**
 * 抽象权限托管类
 * @author janson
 *
 */
public abstract class PermitVerifierFilter implements Filter{
	private String loginUrl = "/login";
	private Pattern ignoreUrisReg,ignoreConfPattern;

	private final static Logger log  = QLog.getLogger(PermitVerifierFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		loginUrl = getLoginUrl();
		StringBuffer sb = new StringBuffer();
		sb.append(getLoginUrl());
		if(ignoreUris()!=null&&ignoreUris().length>0){
			for(int i=0;i<ignoreUris().length;i++){
				if(ignoreUris()[i]!=null&&ignoreUris()[i].length()>1){
					sb.append("|").append(ignoreUris()[i]);
				}
			}
		}
		ignoreUrisReg = Pattern.compile(sb.toString());
		XMemcached.getInstance().flush();
		System.err.println("role ignore:"+QProperties.getInstance().getProperty("PermitVerifierFilter"));
		ignoreConfPattern = Pattern.compile(QProperties.getInstance().getProperty("PermitVerifierFilter"),Pattern.CASE_INSENSITIVE);
	}

	/**
	 * 获取转向登陆地址
	 * @return
	 */
	protected abstract String getLoginUrl();

	/**
	 * 需要忽略的uri拦截
	 * @return
	 */
	protected abstract String[] ignoreUris();
	protected abstract String getApiErrMsg();

	private String toLogger(LoginInfo li,String msg){
		if(li == null){
			return msg;
		}
		return new StringBuilder()
				.append(li.getUserName()).append("@")
				.append(li.getUid()).append("@")
				.append(li.getIp()).append(" ")
				.append(msg).toString();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String uri = (req.getRequestURI()).substring(req.getContextPath().length());
		if(AuthUtils.isDevEnv() 
				|| (ignoreUrisReg!=null && ignoreUrisReg.matcher(uri).find()) 
				|| (ignoreConfPattern!=null && ignoreConfPattern.matcher(uri).find())
				){
			chain.doFilter(request,response);
			return;
		}
		String sid = QServiceUitls.getCookie("sid", req);
		if(sid!=null){
			LoginInfo li = (LoginInfo)XMemcached.getInstance().get(sid);
			boolean hasPermit = li != null && li.hasUriPermit(uri) ;
			if(li!=null&&li.isNeedResetSession()){
				XMemcached.getInstance().set(sid, li, XMemcached.EXP_30Min);
			}
			if(hasPermit){
				log.info(toLogger(li, uri));
				li = null;
				chain.doFilter(request,response);
				return;
			}else{
				log.warn(toLogger(li, uri));
				li = null;
			}
		}else{
			QServiceUitls.setCookie((HttpServletResponse)response, "sid", java.util.UUID.randomUUID().toString(), 60*60*24*365);
		}

		HttpServletResponse res = (HttpServletResponse)response;
		if(uri.endsWith("html")||uri.endsWith("htmlx")){
			StringBuffer sb = new StringBuffer()
					.append(req.getContextPath())
					.append(loginUrl.startsWith("/")?loginUrl:"/"+loginUrl)
					.append("?from=")
					.append(URLEncoder.encode(uri+(req.getQueryString()==null?"":"?"+req.getQueryString()), "utf-8"));
			res.sendRedirect(sb.toString());
		}else{
			res.setCharacterEncoding("utf-8");
			res.setHeader("Content-type", "text/html;charset=UTF-8");
			OutputStream stream = null ;
			try {
				stream = res.getOutputStream(); 
				OutputJson oj = new OutputJson();
				oj.setTag(2);//告诉前端接口没有权限
				oj.setErrMsg((getApiErrMsg()));
				stream.write(oj.toString().getBytes("utf-8"));
				stream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(stream!=null){
						stream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//	 private  String cnToUnicode(String cn) {
	//	        char[] chars = cn.toCharArray();
	//	        String returnStr = "";
	//	        for (int i = 0; i < chars.length; i++) {
	//	          returnStr += "\\u" + Integer.toString(chars[i], 16);
	//	        }
	//	        return returnStr;
	//	    }



	@Override
	public void destroy() {

	}

}
