package cn.qdevelop.core.formatter;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.QLogFactory;
import cn.qdevelop.core.standard.IUpdateHook;

public abstract class AbstractUpdateHook implements IUpdateHook{
	private final static Logger log  = QLogFactory.getLogger(AbstractUpdateHook.class);

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
	
	public boolean validConfig(Element conf){
		if(attrs!=null){
			Set<String> keys = attrs.keySet();
			for(String attr:keys){
				if(conf.attributeValue(attr)==null){
					log.error("formatter配置不全错误："+attrs.toString());
					return false;
				}
			}
		}
		return true;
	}
}
