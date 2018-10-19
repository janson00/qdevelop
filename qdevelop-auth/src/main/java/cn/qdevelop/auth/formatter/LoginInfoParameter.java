package cn.qdevelop.auth.formatter;

import java.lang.reflect.Field;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;

public class LoginInfoParameter extends AbstractParamFormatter {
	String[] userAttrs;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		userAttrs = conf.attributeValue("user-attrs") == null ? null : conf.attributeValue("user-attrs").replaceAll(" ", "").split(",");
	}

	@Override
	public void init() {

	}

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		if(query.get("HTTP_HEAD_SID")==null || userAttrs == null || userAttrs.length==0){
			return query;
		}
		LoginInfo li = (LoginInfo)XMemcached.getInstance().get(query.get("HTTP_HEAD_SID").toString());
		if(li==null){
			return query;
		}
		putJavaBean(li,userAttrs,query);
		li=null;
		return query;
	}

	@Override
	public String[] getncreaseKeys() {
		return null;
	}


	public void putJavaBean(LoginInfo li,String[] fileds,Map<String, Object> query){
		for(String fieldName : fileds){
			Object value = li.getExtraInfo(fieldName);
			if(value == null){
				try {
					Field target = li.getClass().getDeclaredField(fieldName);
					target.setAccessible(true);
					value = target.get(li);
					if(value == null){
						value = li.getExtraInfo(fieldName);
					}

				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if(value!=null){
				query.put(fieldName, value);
			}
		}
	} 

}
