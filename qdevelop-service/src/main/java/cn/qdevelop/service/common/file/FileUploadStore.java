package cn.qdevelop.service.common.file;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.service.UploadControl;
import cn.qdevelop.service.IService;


//使用@WebServlet配置UploadServlet的访问路径
@WebServlet(name="UploadServlet",urlPatterns={"/svr/ajax/fileUpload.json","/svr/ajax/fileUpload.jsonp"})
//使用注解@MultipartConfig将一个Servlet标识为支持文件上传
@MultipartConfig//标识Servlet支持文件上传
public class FileUploadStore extends UploadControl{

	private static final long serialVersionUID = 9147332702716036194L;

	public void init(StringBuffer output){
		output.append("{\"files\":[");
	}

	@Override
	protected boolean disposeFile(InputStream file, String fileName, String storeName, long size, StringBuffer output) {
		output.append(output.length()>20?"{":",{")
		.append("\"fileName\":\"").append(fileName).append("\",")
		.append("\"storeName\":\"").append(storeName).append("\",")
		.append("\"size\":").append(size)
		.append("}");
		return true;
	}

	@Override
	protected String execute(Map<String, String> args, String[] storeName, StringBuffer output) {
		output.append("]");
		Iterator<Map.Entry<String, String>> it = args.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> itor = it.next();
			output.append(",\"").append(itor.getKey()).append("\":\"").append(itor.getValue()).append("\"");
		}
		output.append("}");
		return IService.RETURN_OUT_JSON;
	}





}
