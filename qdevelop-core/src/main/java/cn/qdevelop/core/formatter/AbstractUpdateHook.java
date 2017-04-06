package cn.qdevelop.core.formatter;

import java.util.Map;

import cn.qdevelop.core.standard.IUpdateHook;

public abstract class AbstractUpdateHook implements IUpdateHook{
	protected Map<String, String> attrs;
	public IUpdateHook clone(){
		try {
			return (IUpdateHook)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setConfigAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	
	public void init(){
		
	}
}
