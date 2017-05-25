package cn.qdevelop.common.utils;

import java.io.InputStream;
import java.util.Properties;

public class QConfigLoader {
	
	public static Properties getConfigProperties(String name){
		try {
			InputStream idSvrConfig =  QSource.getInstance().getSourceAsStream("id-generate-svr.properties");
			if(idSvrConfig!=null){
				Properties prop = new Properties();
				prop.load(idSvrConfig);
				return prop;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

}
