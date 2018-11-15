package cn.qdevelop.core.db.connect;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.alibaba.druid.pool.DruidDataSource;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.security.QBase64Utils;
import cn.qdevelop.common.utils.QString;

public class DruidConnect implements IConnect{
	private static Logger log  = QLog.getLogger(DruidConnect.class);

	DruidDataSource dataSource = null;

	public DruidConnect(Element config){
		if(dataSource!=null){
			shutdown();
		}
		try {
			dataSource = new DruidDataSource();
			dataSource.setUrl(config.elementText("driver-url").replaceAll("\t|\r|\n| ", ""));
			dataSource.setDriverClassName(config.elementText("driver-class").replaceAll("\t|\r|\n| ", ""));
			dataSource.setUsername(decodeInfo("user-name",config)); 
			String passwd = decodeInfo("password",config);
			if(config.element("password").attributeValue("encode")!=null&&config.element("password").attributeValue("encode").equals("true")){
				String s = QString.get32MD5((dataSource.getUsername()+config.attributeValue("index")));
				String t = QBase64Utils.decode(passwd);
				passwd = QBase64Utils.decode(t.substring(s.length()));
			}
			dataSource.setPassword(passwd);

			int initSize = Integer.parseInt(getValue(config.elementText("init-idle").replaceAll("\t|\r|\n| ", ""),"1"));
			int maxIdle = Integer.parseInt(getValue(config.elementText("max-idle").replaceAll("\t|\r|\n| ", ""),"1"));
			dataSource.setInitialSize(initSize);
			dataSource.setMinIdle(Integer.parseInt(getValue(config.elementText("min-idle").replaceAll("\t|\r|\n| ", ""),"1")));
			dataSource.setMaxActive(maxIdle);
			dataSource.setMaxWait(Integer.parseInt(getValue(config.elementText("max-wait").replaceAll("\t|\r|\n| ", ""),"1")));
			
			
//			dataSource.setPoolPreparedStatements(true);
//			dataSource.setTimeBetweenEvictionRunsMillis(2000);
//			dataSource.setMinEvictableIdleTimeMillis(600000);
//			dataSource.setMaxEvictableIdleTimeMillis(900000);
////			dataSource.setKeepAlive(true);
//			dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
//			dataSource.setFilters("stat");
			
//			//超过时间限制是否回收
//			dataSource.setRemoveAbandoned(true);
//			//超时时间；单位为秒。120秒=2分钟
//			dataSource.setRemoveAbandonedTimeout(120);
//			//关闭abanded连接时输出错误日志
//			dataSource.setLogAbandoned(true);
			
			dataSource.setValidationQuery(config.elementText("test-query").replaceAll("\t|\r|\n", ""));
			dataSource.setTestOnBorrow(Boolean.parseBoolean(getValue(config.elementText("test-borrow").replaceAll("\t|\r|\n| ", ""),"false"))); 
			dataSource.setTestOnReturn(Boolean.parseBoolean(getValue(config.elementText("test-return").replaceAll("\t|\r|\n| ", ""),"false"))); 
			dataSource.setTestWhileIdle(Boolean.parseBoolean(getValue(config.elementText("test-while-idle").replaceAll("\t|\r|\n| ", ""),"false")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log.error(append("数据库连接池[",config.attributeValue("index"),"]初始话失败！"),e);
			e.printStackTrace();
		}
	}

	@Override
	public void init() {

	}

	@Override
	public Connection getConnection() throws QDevelopException {
		try {
			if(dataSource.getActiveCount() > dataSource.getInitialSize()){
				
			}
//			dataSource.get
			return dataSource.getConnection();
		} catch (SQLException e) {
//			e.printStackTrace();
			throw new QDevelopException(1001,QString.append(QString.getNow(),"\t数据库链接[URL:",dataSource.getUrl(),",user:",dataSource.getUsername(),"]获取异常!"),e);
		}
	}

	@Override
	public void close(Connection conn) {
		 try {
	          if(conn!=null&&!conn.isClosed())
	              conn.close();
	      } catch (SQLException e) {
	          e.printStackTrace();
	      }
	}

	@Override
	public void shutdown() {
		if(dataSource!=null){
			dataSource.close();
		}
	}

	@Override
	public int getCanUseNum() {
		return dataSource.getMaxActive() - dataSource.getActiveCount();
	}

	@Override
	public void printInfo() {
		System.out.println(append(
				"数据库地址:\t",dataSource.getUrl(),"\n",
				"用 户 名:\t\t",dataSource.getUsername(),"\n",
				"活动的连接数:\t",dataSource.getActiveCount(),"\n",
				"初始化连接数:\t",dataSource.getInitialSize(),"\n",
				"最大连接数:\t",dataSource.getMaxActive()
				));
	}

	@Override
	public String monitor() {
		return  append(dataSource.getUrl(),"|",dataSource.getUsername(),"|",dataSource.getInitialSize(),"|",dataSource.getActiveCount(),"|",dataSource.getMaxIdle());
	}

	@Override
	public String getDataBase() {
		return null;
	}

	private String decodeInfo(String childElementName,Element config) throws Exception{
		Element _c = config.element(childElementName);
		String _v = _c.getText().replaceAll("\t|\r|\n| ", "");
		if(_c.attributeValue("encode")!=null ){
			//	          _v = DesUtil.decrypt(_v, DECODE_KEY);
			//	        System.out.println("==>"+_v);
		}
		return _v;
	}
	private String getValue(String val,String defaultVal){
		if(val==null)return defaultVal;
		return val; 
	}
	private static String append(Object ... s){
		StringBuffer sb = new StringBuffer();
		for(Object _s:s)sb.append(_s);
		return sb.toString();
	}

}
