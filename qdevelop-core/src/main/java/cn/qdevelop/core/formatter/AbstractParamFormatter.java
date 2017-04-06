package cn.qdevelop.core.formatter;

import java.util.Map;

import cn.qdevelop.core.standard.IParamFormatter;

public abstract class AbstractParamFormatter implements IParamFormatter{
	protected Map<String, String> attrs;
	public IParamFormatter clone(){
		try {
			return (IParamFormatter)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setConfigAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	
}
