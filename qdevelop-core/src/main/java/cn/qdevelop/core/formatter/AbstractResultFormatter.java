package cn.qdevelop.core.formatter;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.QLog;
import cn.qdevelop.core.standard.IResultFormatter;

/**
 * formatter抽象类，有些公共的方法在集成写的时候不需要定义
 * @author Janson.Gu
 *
 */
public abstract class AbstractResultFormatter  implements IResultFormatter{
	private final static Logger log  = QLog.getLogger(AbstractResultFormatter.class);

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
	
	public void init(){
		
	}
	
//	public Object clone() throws CloneNotSupportedException {
//		IResultFormatter obj = (IResultFormatter)super.clone();
//	      obj.inner = (Inner) inner.clone();
//	      return obj;
//	  }

	
}
