package cn.qdevelop.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.common.utils.QString;
import cn.qdevelop.service.utils.QServiceUitls;

/**
 * 抽象文件上传接口类
 * @author janson
 *
 */
public abstract class UploadControl extends HttpServlet  implements IService{
	private static Logger log = QLog.getLogger(UploadControl.class);
	private static final long serialVersionUID = -726532824668251561L;
	private static MultipartConfig config = UploadControl.class.getAnnotation(MultipartConfig.class);
	
	private static String STORE_ROOT = "/data/upload";
	
	/**
	 * 文件上传过程中，对每一个文件进行单独处理
	 * @param file 上传的文件流
	 * @param fileName	当前上传文件的名称
	 * @param storeName	实际存储的名称
	 * @param size		当前上传文件的大小
	 * @return  返回是否需要存储上传文件 （true|false）
	 */
	protected abstract boolean disposeFile(InputStream file,String fileName,String storeName, long size,StringBuffer output);
	
	/**
	 * 文件处理完成后，统一执行入口
	 * @param args 当前请求的所有参数
	 * @param storeName 当前所有处理过的文件名称
	 * @param output	需要输出到前台的内容收集器
	 * @return 输出内容的格式 return IQDevelopService.
	 */
	protected abstract String execute(Map<String, String> args,String[] storeName,StringBuffer output);
	
	private StringBuffer out;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		out = new StringBuffer();
		init(out);
		Collection<Part> parts = request.getParts();
		String[] storeNames = new String[parts.size()];
		int idx = 0;
		for(Part part : parts){
			try{
				part = request.getPart("file");//获取part用于处理上传的文件
			}catch(IllegalStateException ise){
				//上传的单个文件超出maxFileSize或者上传的总的数据量超出maxRequestSize时会抛出此异常
				if(config.maxRequestSize() == -1L)//如果注解中没设置此项，那就是单个文件超出限制
					log.error("单个文件超限");
				else if(config.maxFileSize() == -1L)//如果注解中没有设置单个文件最大限制，那就是总数据量超限。
					log.error("总数据量超限");
				else
					log.error("Error");
			}
			if(part == null)
				return;
			String header = part.getHeader("content-disposition");
			String fileName = getFileName(header);
			String storeName = getUploadFileSaveName(fileName,QServiceUitls.getCookie("sid",request),STORE_ROOT);
			if(disposeFile(part.getInputStream(),fileName,storeName,part.getSize(),out)){
				part.write(STORE_ROOT+storeName);
			}
			storeNames[idx++] = storeName;
			part.delete();
		}
		String type = execute(QServiceUitls.getParameters(request),storeNames,out);
		QServiceUitls.output(out.toString(), type, request, response);
	}

	private String getFileName(String header) {
		/**
		 * String[] tempArr1 = header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
		 * 火狐或者google浏览器下：tempArr1={form-data,name="file",filename="snmp4j--api.zip"}
		 * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
		 */
		String[] tempArr1 = header.split(";");
		/**
		 *火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
		 *IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
		 */
		String[] tempArr2 = tempArr1[2].split("=");
		//获取文件名，兼容各种浏览器的写法
		return  tempArr2[1].substring(tempArr2[1].lastIndexOf("\\")+1).replaceAll("\"", "");
	}
	
	/**
	 * 获取全局输出内容收集器
	 * @return
	 */
	public StringBuffer getOutPut(){
		return out;
	}
	
	/**
	 * 初始化
	 * @param out
	 */
	public void init(StringBuffer output){
		
	}

	private static String[] pic = new String[]{"jpg","jpeg","gif","png","bmp","ico"}; 
	private static String[] doc = new String[]{"doc","ppt","pdf","xls","xlsx","docx","pptx","csv","tif"}; 

	private static String getUploadFileSaveName(String fileName,String sid,String root){
		String md5 = QString.get32MD5(fileName+sid);
		String date = new SimpleDateFormat("yyMM").format(new Date());
		String type = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
		String parent = "non";
		if(ArrayUtils.contains(pic, type)){
			parent = "pic";
		}else if(ArrayUtils.contains(doc, type)){
			parent = "doc";
		}
		StringBuffer f = new StringBuffer().append(root).append(root.endsWith("/")?"":"/")
				.append(parent).append("/")
				.append(date).append("/")
				.append(md5.substring(0, 2)).append("/");
		File path = new File(f.toString());
		if(!path.exists()){
			path.mkdirs();
		}
		return f.append(md5.substring(2)).append(".").append(type).toString().substring(root.length()+(root.endsWith("/")?-1:0));
	}

}
