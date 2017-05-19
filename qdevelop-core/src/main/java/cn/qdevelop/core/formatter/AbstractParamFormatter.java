package cn.qdevelop.core.formatter;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.core.standard.IParamFormatter;

public abstract class AbstractParamFormatter implements IParamFormatter{
	private final static Logger log  = QLog.getLogger(AbstractParamFormatter.class);

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
