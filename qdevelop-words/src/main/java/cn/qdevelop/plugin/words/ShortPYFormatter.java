package cn.qdevelop.plugin.words;

import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Element;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;

public class ShortPYFormatter extends AbstractResultFormatter{
	String resultKey,newName,defaultValue="#";int start=0,end=1;

	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		resultKey = conf.attributeValue("result-key");
		newName = conf.attributeValue("py-key");
		if(conf.attributeValue("start")!=null&&isNum.matcher(conf.attributeValue("start")).find()){
			start = Integer.parseInt(conf.attributeValue("start"));
		}
		if(conf.attributeValue("end")!=null&&isNum.matcher(conf.attributeValue("end")).find()){
			end = Integer.parseInt(conf.attributeValue("end"));
		}
		if(conf.attributeValue("default")!=null){
			defaultValue = conf.attributeValue("default");
		}
	}

	@Override
	public boolean isQBQuery() {
		return false;
	}

	private final static Pattern isPY = Pattern.compile("^[A-Z]+?$") ;
	private final static Pattern isNum = Pattern.compile("^[0-9]+?$") ;
	@Override
	public void formatter(Map<String, Object> data) {
		data.put(newName, defaultValue);
		if(data.get(resultKey)!=null){
			String s = String.valueOf(data.get(resultKey));
			if(s.length() >= end){
				try {
					String py = PinyinHelper.getShortPinyin(s.substring(start, end)).toUpperCase();
					if(isPY.matcher(py).find()){
						data.put(newName, py);
					}
				} catch (PinyinException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void flush(IDBResult result) throws QDevelopException {

	}

}
