package cn.qdevelop.plugin.sensitiveword.formatter;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.plugin.sensitiveword.impl.KeyWordFinder;
import cn.qdevelop.plugin.sensitiveword.press.ReplaceSensitive;

public class SensitiveFilterFormatter extends AbstractResultFormatter {
	String[] resultKey;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		resultKey = conf.attributeValue("result-key").split(",");
	}

	@Override
	public boolean isQBQuery() {
		return false;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		if(resultKey==null)return;
		for(String key : resultKey){
			if(data.get(key)!=null){
				data.put(key, KeyWordFinder.getInstance().wrapKeyWord(String.valueOf(data.get(key)), new ReplaceSensitive()));
			}
		}
	}

	@Override
	public void flush(IDBResult result) throws QDevelopException {

	}

}
