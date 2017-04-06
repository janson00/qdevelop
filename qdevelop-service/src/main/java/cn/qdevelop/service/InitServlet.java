package cn.qdevelop.service;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import cn.qdevelop.core.QDevelopUtils;

@WebServlet(name="initServlet")
public class InitServlet  extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6397263419464645976L;
	
	public void init(){
		QDevelopUtils.initAll();
	}
}
