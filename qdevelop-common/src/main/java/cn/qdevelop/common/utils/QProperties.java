package cn.qdevelop.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 系统文件配置读取工具,目前读取classes/prop/*.properties的文件
 * 
 * @ClassName: QProperties
 * @Description: TODO
 * @author Janson.Gu
 * @date 2013-9-4 下午12:23:38
 */
public class QProperties {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static QProperties _QProperties = null;
	private static Properties props;

	/**
	 * 
	 * @Description: 单例模式，默认获取classes/props/*.properties下所有的配置文件
	 * @Title: getInstance
	 * @param @return
	 * @return QProperties
	 * @throws
	 * @author
	 * @date 2013-9-4 下午1:26:29
	 */
	public static QProperties getInstance() {
		if (_QProperties == null) {
			_QProperties = new QProperties();
		}
		return _QProperties;
	}
	
	public QProperties(){
		props = new Properties();
		new SearchFileFromJars(){
			@Override
			public void desposeFile(String jarName,String fileName, InputStream is) {
				System.out.println(DateUtil.getNow()+" =====> prop:"+fileName);
				try {
					BufferedReader bf = new BufferedReader(new InputStreamReader(is));
					props.load(bf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.searchAllJarsFiles("qdevelop-prop.properties$");
		final int projectIdx = QSource.getProjectPath().length();
		new SearchFileFromProject(){
			@Override
			protected void disposeFile(File f) {
				System.out.println(DateUtil.getNow()+" =====> prop:"+f.getAbsolutePath().substring(projectIdx));
				try {
					BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
					props.load(bf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			protected void disposeFileDirectory(File f) {
			}
		}.searchProjectFiles("/props/*.properties|qdevelop-prop.properties$");
	}


	/**
	 * 当有值
	 * 
	 * @param value
	 * @return
	 */
	private String parseValue(String value) {
		if (value == null || value.indexOf("${") == -1)
			return value;
		String[] keys = value.replaceAll("\\}.+?\\{", "|")
				.replaceAll("^\\$\\{|^.+\\$\\{|\\}.+?$|\\}$", "").split("\\|");
		String _val = new String(value);
		for (String key : keys) {
			_val = _val.replace(append("${", key, "}"), (String) props.get(key));
		}
		return _val;
	}

	/**
	 * 
	 * @Description: 配置中编写参数 例： 配置： test=I'm is [1] [2] 方法：
	 *               getProperty(test,"janson","gu") 返回 : I'm is janson gu
	 * @Title: getProperty
	 * @param @param key
	 * @param @param paramValue
	 * @param @return
	 * @return String
	 * @throws
	 * @author
	 * @date 2013-9-4 下午12:25:12
	 */
	public String getProperty(String key, String... paramValue) {
		String tmp = getProperty(key);
		if (tmp == null)
			return null;
		for (int i = 0; i < paramValue.length; i++) {
			if (paramValue[i] != null)
				tmp = tmp.replace(append("[", (i + 1), "]"), paramValue[i]);
		}
		return tmp;
	}

	/**
	 * 
	 * @Description: 给定HashMap，将配置中值中含有{key}的参数做替换
	 * @Title: getProperty
	 * @param @param key
	 * @param @param parseVals
	 * @param @return
	 * @return String
	 * @throws
	 * @author
	 * @date 2013-9-5 下午3:15:05
	 */
	public String getProperty(String key, Map<String, Object> parseVals) {
		String tmp = getProperty(key);
		if (tmp == null)
			return null;
		Set<Map.Entry<String, Object>> set = parseVals.entrySet();
		for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
			tmp = tmp.replace(append("{", entry.getKey(), "}"), String.valueOf(entry.getValue()));
		}
		return tmp;
	}
	
	public String getJsonValue(String key,String jsonKey){
		String tmp = (String) props.get(key);
		if(tmp==null)return null;
		tmp = tmp.trim();
		if(!tmp.startsWith("{")||!tmp.endsWith("}")){
			return null;
		}
		JSONObject json = JSON.parseObject(tmp);
		return json.getString(jsonKey);
	}

	/**
	 * 
	 */
	public String getProperty(String key) {
		Object val = props.get(key);
		if (val == null)
			return null;
		String tmp = String.valueOf(val);
		if (tmp.indexOf("${") > -1) {
			tmp = parseValue(tmp);
			props.put(key, tmp);
		}
		return tmp;
	}

	public String getString(String key) {
		return getProperty(key);
	}

	public int getInt(String key) throws NumberFormatException {
		return (int) getDouble(key);
	}

	public double getDouble(String key) throws NumberFormatException {
		String tmp = getProperty(key);
		if (tmp == null)
			return -0;
		if (tmp.indexOf("*") > -1) {// 支持简单乘法计算
			String[] tmps = tmp.split("\\*");
			double value = 1.0;
			for (int i = 0; i < tmps.length; i++) {
				value = value * Double.parseDouble(tmps[i].trim());
			}
			return value;
		}
		return Double.parseDouble(tmp);
	}

	public boolean getBoolean(String key) {
		if (getProperty(key) == null)
			return false;
		return Boolean.parseBoolean(getProperty(key));
	}

	private String append(Object... args) {
		StringBuilder sb = new StringBuilder();
		for (Object arg : args)
			sb.append(arg);
		return sb.toString();
	}
	
}