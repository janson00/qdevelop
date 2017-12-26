package cn.qdevelop.service.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.common.files.QProperties;
import cn.qdevelop.service.interfacer.IService;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet("/svr/conf/*")
public class Property2Json extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6417632465063024306L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		StringBuffer out = new StringBuffer();
		String uri = request.getRequestURI();
		String[] keys = uri.substring(uri.lastIndexOf("/")+1).split(",|\\|");
		for(String key : keys){
			String val = QProperties.getInstance().getProperty(key);
			out.append("var ").append(key).append("=").append(val).append(";");
		}
		QServiceUitls.output(out.toString(),IService.RETURN_OUT_JSON,request,response);
	}

}
