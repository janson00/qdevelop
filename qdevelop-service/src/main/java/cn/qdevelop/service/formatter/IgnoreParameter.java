package cn.qdevelop.service.formatter;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;

public class IgnoreParameter extends AbstractParamFormatter{
	String[] ignoreArgs;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		ignoreArgs = conf.attributeValue("param-key").split(",");
	}

	@Override
	public void init() {
		
	}

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		return query;
	}

	@Override
	public String[] getncreaseKeys() {
		return ignoreArgs;
	}

}
