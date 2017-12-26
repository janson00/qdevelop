package cn.qdevelop.service.control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.common.utils.QString;

@WebServlet(urlPatterns={"*.htmlx"})
public class AdaptorControl extends HttpServlet{
	
	private static final long serialVersionUID = -1913598480802435091L;
	private static Integer webPathIdx = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if(webPathIdx == null){
			webPathIdx = request.getContextPath().length();
		}
		request.getRequestDispatcher(QString.append("/htmlx/",uri.substring(webPathIdx, uri.length()-5),"jsp")).forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request,response);
	}

}
