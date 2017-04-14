package cn.qdevelop.service.common.file;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.UploadControl;


//使用@WebServlet配置UploadServlet的访问路径
@WebServlet(name="SaveImageServlet",urlPatterns={"/svr/ajax/imageUpload.json","/svr/ajax/imageUpload.jsonp"})
//使用注解@MultipartConfig将一个Servlet标识为支持文件上传
@MultipartConfig//标识Servlet支持文件上传
public class ImageUpload extends UploadControl{

	private static final long serialVersionUID = 9147332702716036194L;
	
	@Override
	protected boolean disposeFile(InputStream file, String fileName, String storeName, long size) {
		return true;
	}

	@Override
	protected String execute(Map<String, String> args, String[] storeName, IOutput output) {
		output.setData(storeName);
		return null;
	}

	@Override
	protected String setFileStoreRootPath() {
		return "/data/upload";
	}

	@Override
	protected String[] setFileAllowType() {
		return new String[]{".jpg",".png",".bmp",".gif",".jpeg"};
	}

	@Override
	public void init(Map<String, String> args) {
		
	}

}