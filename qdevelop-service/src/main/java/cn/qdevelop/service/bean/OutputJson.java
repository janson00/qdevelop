package cn.qdevelop.service.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;

import cn.qdevelop.service.IOutput;

public class OutputJson implements IOutput{
	/**返回结果状态标识，default 0 正常**/
	int tag = 0;
	String errMsg;
	Object data;
	HashMap<String,Object> attr = null;

	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public String getErrMsg() {
		return errMsg==null?"":errMsg;
	}
	public void setErrMsg(String errMsg) {
		if(tag==0){//0默认为正常，如果设置错误信息，则主动变更状态为1
			tag = 1;
		}
		this.errMsg = errMsg;
	}
	public Object getData() {
		return data;
	}

	public void addAttr(String key,Object val){
		if(attr==null){
			attr = new HashMap<String,Object>();
		}
		attr.put(key, val);
	}

	public void setData(Object data) {
		this.data = data;
	}


	public String toString(){
		StringBuffer out = new StringBuffer().append("{")
				.append("\"tag\":").append(this.getTag()).append(",")
				.append("\"data\":").append(JSON.toJSONString(this.getData())).append(",")
				.append("\"errMsg\":\"").append(this.getErrMsg()).append("\"");
		if(attr!=null){
			Iterator<Entry<String,Object>> iter = attr.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,Object> itor = iter.next();
				out.append(",\"").append(itor.getKey()).append("\":").append(JSON.toJSONString(itor.getValue()));
			}
		}
		return out.append("}").toString();
	}
	
	



}
