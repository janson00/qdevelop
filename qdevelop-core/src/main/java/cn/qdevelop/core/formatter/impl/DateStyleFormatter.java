package cn.qdevelop.core.formatter.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;

public class DateStyleFormatter  extends AbstractResultFormatter{
	String[] resultKey,dateStyle;
	ThreadLocal<SimpleDateFormat>[] simpleDateFormats;
	@SuppressWarnings("unchecked")
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		if(attrs!=null){
			resultKey = conf.attributeValue("result-key").split(",");
			
			String style = conf.attributeValue("date-style");
			if(resultKey.length>1 && style.indexOf(",")==-1){
				dateStyle = new String[resultKey.length];
				for(int i=0;i<resultKey.length;i++){
					dateStyle[i] =  style;
				}
			}else{
				dateStyle = style.split(",");
			}
			if(dateStyle.length != resultKey.length)throw new QDevelopException(1001,"formatter 格式化参数出错");
			simpleDateFormats = new ThreadLocal[resultKey.length];
			for(int i=0;i<dateStyle.length;i++){
				simpleDateFormats[i] = new ThreadLocal<SimpleDateFormat>();
			}
		}
	}

	public SimpleDateFormat getSimpleDateFormat(int i){
		SimpleDateFormat sdf = simpleDateFormats[i].get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat(dateStyle[i]);  
            simpleDateFormats[i].set(sdf);  
        }  
        return sdf;
	}
	@Override
	public boolean isQBQuery() {
		return false;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		for(int i=0;i<resultKey.length;i++){
			if(data.get(resultKey[i])!=null){
				if(data.get(resultKey[i]).getClass().equals(java.sql.Timestamp.class)){
					data.put(resultKey[i], getSimpleDateFormat(i).format((java.sql.Timestamp)data.get(resultKey[i])));
				}else if(data.get(resultKey[i]).getClass().equals(java.sql.Date.class)){
					data.put(resultKey[i], getSimpleDateFormat(i).format((java.sql.Date)data.get(resultKey[i])));
				}else if(data.get(resultKey[i]).getClass().equals(java.lang.Long.class)){
					Date time = new Date();
					time.setTime((java.lang.Long)data.get(resultKey[i]));
					data.put(resultKey[i], getSimpleDateFormat(i).format(time));
				}
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
