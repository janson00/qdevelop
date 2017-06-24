package cn.qdevelop.plugin.id.client.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;
import cn.qdevelop.plugin.id.client.IDGenerate;

public class IDGenerateFormatter extends AbstractParamFormatter{
	String paramKey,appIdName,wrapper,dateStyle;
	int digit,buffer;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		paramKey = conf.attributeValue("param-key");
		appIdName = conf.attributeValue("app-id-name");
		wrapper = conf.attributeValue("wrapper");
		digit = conf.attributeValue("digit") == null ? 6 : Integer.parseInt(conf.attributeValue("digit"));
		buffer = conf.attributeValue("buffer") == null ? 5 : Integer.parseInt(conf.attributeValue("buffer"));
		dateStyle = conf.attributeValue("date-format") == null ? "yyMMdd" : conf.attributeValue("date-format");

	}

	@Override
	public void init() {

	}

	@Override
	public void setConfigAttrs(Map<String, String> attrs) {

	}
	
	private static Pattern isTemplate = Pattern.compile("\\{.+\\}");

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		try {
			String id = IDGenerate.getInstance().getIDStr(appIdName, digit, buffer);
			if(wrapper!=null&&isTemplate.matcher(wrapper).find()){
				if(wrapper.indexOf("{ID}") > -1){
					wrapper =  wrapper.replace("{ID}", id);
				}
				if(wrapper.indexOf("{DATE}") > -1){
					wrapper =  wrapper.replace("{DATE}",  new SimpleDateFormat(dateStyle).format(new Date()));
				}
				query.put(paramKey, wrapper);
			}else{
				query.put(paramKey, id);
			}
			System.out.println(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getncreaseKeys() {
		// TODO Auto-generated method stub
		return new String[]{paramKey};
	}

}
