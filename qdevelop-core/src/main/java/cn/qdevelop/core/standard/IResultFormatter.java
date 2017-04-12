package cn.qdevelop.core.standard;

import java.util.Map;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;

public interface IResultFormatter  extends Cloneable{
	public IResultFormatter clone();
	
	/**
	 * sqlConfig 每次执行前初始化当前sql配置的参数
	 * @param conf
	 * @throws QDevelopException
	 */
	public void initFormatter(Element conf) throws QDevelopException;
	
	/**
	 * 从模版中获取具体的配置参数
	 * @param attrs
	 */
	public void setConfigAttrs(Map<String,String> attrs);
	
	/**
	 * 是否是查询
	 * @return
	 */
	public boolean isQBQuery();
	
	/**
	 * 获取数据结果集后，对每行数据进行嵌入式自定义处理
	 * @param data
	 */
	public void formatter(Map<String,Object> data);
	
	/**
	 * 对结果最后总体再自定义处理一次
	 * @param result
	 * @throws QDevelopException
	 */
	public void flush(IDBResult result)  throws QDevelopException;
	
	/**
	 * formatter每次执行时，自定义自己的初始化方法
	 */
	public void init();
	
	/**
	 * 加载时，检查参数是否配置完整（自动执行）
	 * @param conf
	 * @return
	 */
	public boolean validConfig(Element conf);
	
}
