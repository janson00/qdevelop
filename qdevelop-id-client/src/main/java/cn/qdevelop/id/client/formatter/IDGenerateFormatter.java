package cn.qdevelop.id.client.formatter;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;
import cn.qdevelop.id.client.IDClient;

public class IDGenerateFormatter extends AbstractParamFormatter{
	String paramKey,appIdName,wrapper;
	int digit,buffer;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		paramKey = conf.attributeValue("param-key");
		appIdName = conf.attributeValue("app-id-name");
		wrapper = conf.attributeValue("wrapper");
		digit = conf.attributeValue("digit") == null ? 6 : Integer.parseInt(conf.attributeValue("digit"));
		buffer = conf.attributeValue("buffer") == null ? 5 : Integer.parseInt(conf.attributeValue("buffer"));
	}

	@Override
	public void init() {

	}

	@Override
	public void setConfigAttrs(Map<String, String> attrs) {

	}

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		try {
			String id = IDClient.getInstance().getIDStr(appIdName, digit, buffer);
			if(wrapper!=null&&wrapper.indexOf("{ID}")>-1){
				query.put(paramKey, wrapper.replace("{ID}", id));
			}else{
				query.put(paramKey, id);
			}
			System.out.println(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
