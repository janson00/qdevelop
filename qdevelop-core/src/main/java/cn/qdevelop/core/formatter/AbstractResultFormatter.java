package cn.qdevelop.core.formatter;

import java.util.Map;

import cn.qdevelop.core.standard.IResultFormatter;

/**
 * formatter抽象类，有些公共的方法在集成写的时候不需要定义
 * @author Janson.Gu
 *
 */
public abstract class AbstractResultFormatter  implements IResultFormatter{
	protected Map<String, String> attrs;
	public IResultFormatter clone(){
		try {
			return (IResultFormatter)super.clone();
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
