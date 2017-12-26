package cn.qdevelop.core.formatter.impl;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.files.QProperties;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;

/**
 * 专门用户翻译状态的
 * @author Janson.Gu
 *
 */
public class PropTranFormatter   extends AbstractResultFormatter{
	String[] resultKey,propKey;

	public boolean isQBQuery(){
		return false;
	}

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			resultKey = conf.attributeValue("result-key").split(",");
			propKey = conf.attributeValue("prop-key").split(",");
			if(resultKey.length!=propKey.length){
				throw new QDevelopException(1001,"prop-formatter配置错误,分割逗号长度不相等："+conf.attributeValue("result-key")+" : "+conf.attributeValue("prop-key"));
			}
		}
	}

	@Override
	public void formatter(Map<String, Object> data) {
		for(int i=0;i<resultKey.length;i++){
			Object val = data.get(resultKey[i]);
			if(val!=null){
				String v = QProperties.getInstance().getJsonValue(propKey[i], String.valueOf(val));
				data.put("__"+resultKey[i], v);
			}
		}
	}
	

	@Override
	public void flush(IDBResult result) throws QDevelopException {

	}

	@Override
	public void init() {

	}

}
