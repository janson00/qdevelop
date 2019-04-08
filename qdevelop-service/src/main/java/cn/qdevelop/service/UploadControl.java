package cn.qdevelop.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.utils.QString;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.interfacer.IService;
import cn.qdevelop.service.utils.QServiceUitls;

/**
 * 抽象文件上传接口类
 * @author janson
 *
 */
public abstract class UploadControl extends HttpServlet  implements IService{
	private static Logger log = QLog.getLogger(UploadControl.class);
	private static final long serialVersionUID = -726532824668251561L;
	private ThreadLocal<HttpServletResponse> httpServletResponse = new ThreadLocal<HttpServletResponse>();
	private ThreadLocal<HttpServletRequest> httpServletRequest = new ThreadLocal<HttpServletRequest>();
	//	private static MultipartConfig config = UploadControl.class.getAnnotation(MultipartConfig.class);
	private String[] checkColumns,ignoreColumns;
	private ThreadLocal<IOutput> out  = new ThreadLocal<IOutput>();

	public void init(ServletConfig config)throws ServletException{  
		super.init(config);  
		String columns = config.getInitParameter(IService.INIT_VALID_REQUIRED);
		if(columns!=null){
			checkColumns=(columns.split(","));
		}
		String ignore = config.getInitParameter(IService.INIT_VALID_IGNORE);
		if(ignore!=null){
			ignoreColumns=((ignore+",file").split(","));
		}else{
			ignoreColumns=(new String[]{"file"});
		}
	}

	/**
	 * 自定义初始化请求参数，在执行execute方法和验证参数之前，做参数的自定义处理
	 * @param files
	 */
	public abstract void init(Map<String,String> args);

	/**
	 * 设置文件存储的根路径，
	 * @return null，即为相对目录文件下
	 */
	protected abstract String setFileStoreRootPath();

	/**
	 * 设置文件允许接受类型；
	 * @return null，可接受所有类型
	 */
	protected abstract String[] setFileAllowType();

	/**
	 * 文件上传过程中，对每一个文件进行单独处理
	 * @param file 上传的文件流
	 * @param fileName	当前上传文件的名称
	 * @param storeName	实际存储的名称
	 * @param size		当前上传文件的大小
	 * @return  返回是否需要存储上传文件 （true|false）
	 */
	protected abstract boolean disposeFile(InputStream file,String fileName,String storeName, long size);

	/**
	 * 文件处理完成后，统一执行入口
	 * @param args 当前请求的所有参数
	 * @param storeName 当前所有处理过的文件名称
	 * @param output	需要输出到前台的内容收集器
	 * @return 输出内容的格式 return IQDevelopService.
	 */
	protected abstract String execute(Map<String, String> args,String[] storeName,IOutput output);


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {	
		httpServletResponse.set(response);
		httpServletRequest.set(request);
		out.set(QServiceUitls.getOutput(request, response));
		if(!out.get().isError()){
			Map<String,String> args = QServiceUitls.getParameters(request);
			init(args);
			if(new QServiceUitls().validParameters(args,out.get(),checkColumns,ignoreColumns)){
				Collection<Part> parts = request.getParts();
				//				String[] storeNames = new String[parts.size()];
				ArrayList<String> storeNames = new ArrayList<String>();
				if(parts != null){
					for(Part part : parts){
						String fileName = this.getFileName(part.getHeader("Content-Disposition"));
//						System.out.println(fileName);
						if(null!=fileName && !(fileName).trim().equals("")){
							String[] allowType = setFileAllowType();
							if(allowType!=null){
								String type = fileName.substring(fileName.lastIndexOf("."));
								if(!ArrayUtils.contains(allowType, type)){
									part.delete();
									out.get().setErrMsg("上传文件格式不在允许范围之内，允许格式为：",ArrayUtils.toString(allowType));
									QServiceUitls.output(out.get().toString(),RETURN_OUT_JSON,request,response);
									return;
								}
							}

							String STORE_ROOT = setFileStoreRootPath();
							if(STORE_ROOT == null){
								STORE_ROOT = "";
							}
							String storeName = getUploadFileSaveName(fileName,QServiceUitls.getCookie("sid",request),STORE_ROOT);
							if(disposeFile(part.getInputStream(),fileName,storeName,part.getSize())){
								checkPath(storeName,STORE_ROOT);
								log.info("store file: "+STORE_ROOT+storeName);
								//part.write(STORE_ROOT+storeName);
								writeTo(STORE_ROOT+storeName,part.getInputStream());
							}
							storeNames.add(storeName);
							part.delete();
						}
					}
				}
				execute(args,storeNames.toArray(new String[]{}),out.get());
			}
		}
		QServiceUitls.output(out.get().toString(), out.get().getOutType(), request, response);
		out.remove();
		httpServletResponse.remove();
		httpServletRequest.remove();
	}
	
	 private void writeTo(String fileName, InputStream in) throws IOException, FileNotFoundException {
	        OutputStream out = new FileOutputStream(fileName);
	        byte[] buffer = new byte[1024];
	        int length = -1;
	        while ((length = in.read(buffer)) != -1) {
	            out.write(buffer, 0, length);
	        }
	        in.close();
	        out.close();
	    }

	private String getFileName(String header) {
		/**
		 * String[] tempArr1 = header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
		 * 火狐或者google浏览器下：tempArr1={form-data,name="file",filename="snmp4j--api.zip"}
		 * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
		 */
		String[] tempArr1 = header.split(";");
		if(tempArr1.length<3)return null;
		/**
		 *火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
		 *IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
		 */
		String[] tempArr2 = tempArr1[2].split("=");
		if(tempArr2.length<2)return null;
		//获取文件名，兼容各种浏览器的写法
		return  tempArr2[1].substring(tempArr2[1].lastIndexOf("\\")+1).replaceAll("\"", "");
	}

	/**
	 * 获取全局输出内容收集器
	 * @return
	 */
	public IOutput getOutPut(){
		return out.get();
	}




	private static String[] pic = new String[]{"jpg","jpeg","gif","png","bmp","ico"}; 
	private static String[] doc = new String[]{"doc","ppt","pdf","xls","xlsx","docx","pptx","csv","tif"}; 

	private static void checkPath(String file,String root){
		File f = new File(root+file);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
	}

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
		return f.append(md5.substring(2)).append(".").append(type).toString().substring(root.length()+(root.endsWith("/")?-1:0));
	}

	public HttpServletResponse getResponse(){
		return httpServletResponse.get();
	}

	public HttpServletRequest getRequest(){
		return httpServletRequest.get();
	}


}
