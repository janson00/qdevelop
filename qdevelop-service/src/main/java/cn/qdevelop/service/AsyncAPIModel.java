package cn.qdevelop.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.utils.QServiceUitls;

public abstract class AsyncAPIModel  implements Runnable {

	private AsyncContext asyncContext;
	private String[] checkColumns,ignoreColumns;


	public AsyncAPIModel(AsyncContext asyncContext){
		this.asyncContext = asyncContext;
	}


	public abstract void executor(Map<String,String> parameters,IOutput outPut);

	public void run(){
		IOutput outPut = QServiceUitls.getOutput(getRequest(), getResponse());
		Map<String,String> args = getParameters(getRequest()); 
		if(new QServiceUitls().validParameters(args,outPut,checkColumns,ignoreColumns)){
			executor(args,outPut);
		}
		QServiceUitls.output(outPut.toString(), outPut.getOutType(), getRequest(), getResponse());
		this.asyncContext.complete();
		args.clear();
		args = null;
		outPut = null;
	}

	private Map<String,String> getParameters(HttpServletRequest request){
		Map<String,String> paramMap = new HashMap<String,String>();
		Enumeration<?> paramNames = request.getParameterNames();
		String key;
		String[] value;
		while (paramNames.hasMoreElements()) {
			key = (String) paramNames.nextElement();
			value = request.getParameterValues(key);
			if(value!=null&&value.length==1){
				paramMap.put(key, value[0].trim());
			}else {
				StringBuffer tmp = new StringBuffer();
				int len = value.length;
				for(int i=0;i<len;i++){
					if(i>0)tmp.append("&");
					tmp.append(value[i]);
				}
				paramMap.put(key, tmp.toString().trim());
			}
		}
		return paramMap;
	}

	protected HttpServletRequest getRequest(){
		return (HttpServletRequest)this.asyncContext.getRequest();
	}

	protected HttpServletResponse getResponse(){
		return (HttpServletResponse)this.asyncContext.getResponse();
	}

	public void setCheck(String[] checkColumns,String[] ignoreColumns){
		this.checkColumns = checkColumns ;
		this.ignoreColumns = ignoreColumns;
	}

}
