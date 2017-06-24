package cn.qdevelop.plugin.sensitiveword.formatter;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;
import cn.qdevelop.plugin.sensitiveword.impl.KeyWordFinder;

public class SensitiveWordParameter extends AbstractParamFormatter{
	String paramKey,statusKey,statusExpect;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		paramKey = conf.attributeValue("param-key");
		statusKey = conf.attributeValue("status-key");
		statusExpect = conf.attributeValue("status-expect");
	}

	@Override
	public void init() {

	}

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		if(paramKey==null)return query;
		if(query.get(paramKey) != null){
			String sensitiveWords = KeyWordFinder.getInstance().findKeyWord(String.valueOf(query.get(paramKey)));
			if(sensitiveWords!=null&&sensitiveWords.length()>1){
				query.put(statusKey, statusExpect);
			}
		}
		return query;
	}

	@Override
	public String[] getncreaseKeys() {
		return null;
	}

}
