package cn.qdevelop.service.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
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

import cn.qdevelop.common.files.QSource;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

@WebFilter(urlPatterns="/svr/*")
public class SvrQueryFilter  implements Filter{
	Pattern localIps,allowIps; 

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		localIps = Pattern.compile(getIp());
		Properties prop = QSource.getInstance().loadProperties("plugin-config/allow_ips.properties",this.getClass());
		if(prop!=null){
			Iterator<Object> ips = prop.keySet().iterator();
			StringBuffer sb = new StringBuffer();
			sb.append("^");
			while(ips.hasNext()){
				String ip = String.valueOf(ips.next());
				sb.append(sb.length()>5?"|":"").append("(").append(ip.replaceAll("\\*", "[0-9]+?")).append(")");
			}
			sb.append("$");
			allowIps = Pattern.compile(sb.toString());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String ip = QServiceUitls.getUserIP((HttpServletRequest)request);
		if(localIps!=null && localIps.matcher(ip).find()){
			chain.doFilter(request,response);
		}else if(allowIps!=null && allowIps.matcher(ip).find()){
			chain.doFilter(request,response);
		}else{
			IOutput out = QServiceUitls.getOutput((HttpServletRequest)request, (HttpServletResponse)response);
			out.setErrMsg("当前IP[",ip,"]未在访问许可范围之内。");
			QServiceUitls.output(out.toString(), out.getOutType(), (HttpServletRequest)request, (HttpServletResponse)response);
		}
	}

	@Override
	public void destroy() {

	}

	private static Pattern isIP = Pattern.compile("^[0-9]+?\\.[0-9]+?\\.[0-9]+?\\.[0-9]+?$");
	public static String getIp(){
		StringBuffer ipRules = new StringBuffer();
		ipRules.append("^");
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					String ip = ips.nextElement().getHostAddress();
					if(isIP.matcher(ip).find()){
						ipRules.append(ipRules.length()>5?"|":"");
						ipRules.append("(").append(ip.substring(0, ip.lastIndexOf(".")+1)).append("[0-9]+?").append(")");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipRules.append("$");
		return ipRules.toString();
	}

	public static void main(String[] args) {
		//		Pattern isTarget = Pattern.compile("^(192.168.1.[0-9]+?)|(127.0.0.1)$");
		//		System.out.println(isTarget.matcher("192.168.1.2").find());
		System.out.println(getIp());

	}

}
