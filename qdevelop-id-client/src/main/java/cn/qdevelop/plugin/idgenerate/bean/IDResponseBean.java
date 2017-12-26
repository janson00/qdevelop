package cn.qdevelop.plugin.idgenerate.bean;

import cn.qdevelop.plugin.common.IDResponse;

public class IDResponseBean implements IDResponse  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711524215661521109L;
	Long[] vals;
	String msg;
	
	public IDResponseBean(int buffer){
		if(buffer > 0){
			vals = new Long[buffer];
		}
	}
	
	@Override
	public Long[] getValues() {
		return vals;
	}
	
	public void setValues(int idx,long val){
		vals[idx] = val;
	}

	@Override
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg){
		this.msg = msg;
	}
	
}
