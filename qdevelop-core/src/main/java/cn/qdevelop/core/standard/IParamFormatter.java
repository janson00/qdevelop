package cn.qdevelop.core.standard;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;

public interface IParamFormatter  extends Cloneable {
	
	public IParamFormatter clone();
	
	public void initFormatter(Element conf) throws QDevelopException;
	public void init();
	public void setConfigAttrs(Map<String,String> attrs);
	
	public Map<String,Object> formatter( Map<String,Object> query);
	
	/**
	 * 获取新增的参数,接口请求参数检测时自动忽略这些参数
	 * @return
	 */
	public String[] getncreaseKeys();
	
	/**
	 * 加载时，检查参数是否配置完整（自动执行）
	 * @param conf
	 * @return
	 */
	public boolean validConfig(Element conf);
}
