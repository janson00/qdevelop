/**   
 * @Title: BaseException.java 
 * @Package com.square.common.exception 
 * @Description: TODO
 * @date 2013年11月6日 下午3:53:00 
 * @version V1.0  
 * Copyright www.youmei.com 版权所有 
 */
package cn.qdevelop.common.exception;

import cn.qdevelop.common.utils.DateUtil;

/**
 * @ClassName: BaseException
 * @Description: TODO
 * @author squarezjz
 * @date 2013年11月6日 下午3:53:00
 */
public class QDevelopException extends Exception {
	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = -9069679316336950534L;

	/*
	 * 错误编码
	 */
	private int code;
	/*
	 * 错误消息
	 */
	private String message;
	
	private Object detail;

	public Object getDetail() {
		return detail;
	}

	public void setDetail(Object detail) {
		this.detail = detail;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public QDevelopException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public QDevelopException(int code, String message, Exception e) {
		super(e);
		this.code = code;
		this.message = message;
	}
	
	
	public void printStackTrace(){
		System.out.println(DateUtil.getNow()+" ["+this.code+"] "+this.message);
		super.printStackTrace();
	}

}
