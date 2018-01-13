package cn.qdevelop.core.db.connect;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QString;

/**
 * DBCP 数据库链接池
 * @author Janson Gu
 * @version 1.0
 *
 */
public class DBCPConnect implements IConnect {
	private static Logger log  = QLog.getLogger(DBCPConnect.class);

  private BasicDataSource bds;
  public String database = "MYSQL";
  @SuppressWarnings("unused")
private static String DECODE_KEY="";

  public DBCPConnect(Element config){
      if(bds==null)init(config);
  }

  public void close(Connection conn) {
      try {
          if(conn!=null&&!conn.isClosed())
              conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }

  }

  public int getCanUseNum(){
      if(bds==null)return 0;
      return bds.getMaxIdle() - bds.getNumActive();
  }

  public Connection getConnection()  throws QDevelopException{
      try {
          return bds.getConnection();
      } catch (SQLException e) {
          throw new QDevelopException(1001,QString.append(QString.getNow(),"\t数据库链接[URL:",bds.getUrl(),",user:",bds.getUsername(),"]获取异常!"),e);
      }
  }

  public void init(Element config) {
      if(bds!=null){
          shutdown();
      }
      bds = new BasicDataSource();
      try {
          DECODE_KEY = "qdevelop"+decodeInfo("user-name",config);
          bds.setUrl(config.elementText("driver-url").replaceAll("\t|\r|\n| ", ""));
          bds.setDriverClassName(config.elementText("driver-class").replaceAll("\t|\r|\n| ", ""));
          bds.setUsername(decodeInfo("user-name",config));        
          bds.setPassword(decodeInfo("password",config));
          
          int initSize = Integer.parseInt(getValue(config.elementText("init-idle").replaceAll("\t|\r|\n| ", ""),"1"));
          int maxIdle = Integer.parseInt(getValue(config.elementText("max-idle").replaceAll("\t|\r|\n| ", ""),"1"));
          bds.setInitialSize(initSize);
          bds.setMinIdle(Integer.parseInt(getValue(config.elementText("min-idle").replaceAll("\t|\r|\n| ", ""),"1")));
          bds.setMaxIdle(maxIdle);
          bds.setMaxTotal(maxIdle);
          bds.setMaxWaitMillis(Integer.parseInt(getValue(config.elementText("max-wait").replaceAll("\t|\r|\n| ", ""),"1")));
          
//        bds.setMinEvictableIdleTimeMillis(-1);
          bds.setTimeBetweenEvictionRunsMillis(1000 * 60 * 5);
          bds.setMinEvictableIdleTimeMillis(1000 * 60 * 5);
          
          bds.setValidationQuery(config.elementText("test-query").replaceAll("\t|\r|\n", ""));
          bds.setTestOnBorrow(Boolean.parseBoolean(getValue(config.elementText("test-borrow").replaceAll("\t|\r|\n| ", ""),"false"))); 
          bds.setTestOnReturn(Boolean.parseBoolean(getValue(config.elementText("test-return").replaceAll("\t|\r|\n| ", ""),"false"))); 
          bds.setTestWhileIdle(Boolean.parseBoolean(getValue(config.elementText("test-while-idle").replaceAll("\t|\r|\n| ", ""),"false")));
          
//        bds.setLogAbandoned(false);
//        bds.setRemoveAbandonedOnBorrow(true);
//        bds.setRemoveAbandonedOnMaintenance(false);
//        bds.setRemoveAbandonedTimeout(60);
          //          bds.setPoolPreparedStatements(true);
          //          bds.setMaxOpenPreparedStatements(maxIdle);
          parseDataBase(config.elementText("driver-url"));
      } catch (Exception e) {
    	  log.error(append("数据库连接池[",config.attributeValue("index"),"]初始话失败！"),e);
      }
  }

  private String decodeInfo(String childElementName,Element config) throws Exception{
      Element _c = config.element(childElementName);
      String _v = _c.getText().replaceAll("\t|\r|\n| ", "");
      if(_c.attributeValue("encode")!=null ){
//          _v = DesUtil.decrypt(_v, DECODE_KEY);
//        System.out.println("==>"+_v);
      }
      return _v;
  }

  private void parseDataBase(String driverUrl){
      if(driverUrl.toLowerCase().indexOf("oracle")>-1){
          database = "ORACLE";
      }else if(driverUrl.toLowerCase().indexOf("mysql")>-1){
          database = "MYSQL";
      }
  }
  private String getValue(String val,String defaultVal){
      if(val==null)return defaultVal;
      return val; 
  }

  public void printInfo() {
      System.out.println(append(
              "数据库地址:\t",bds.getUrl(),"\n",
              "用     户     名:\t",bds.getUsername(),"\n",
              "活动的连接数:\t",bds.getNumActive(),"\n",
              "剩余连接数:\t",bds.getNumIdle(),"\n",
              "最大连接数:\t",bds.getMaxIdle()
              ));
  }

  public String monitor(){
      return append(bds.getUrl(),"|",bds.getUsername(),"|",bds.getNumActive(),"|",bds.getNumIdle(),"|",bds.getMaxIdle());
  }

  public void init() {
  }

  public String getDataBase() {
      return database;
  } 
  private static String append(Object ... s){
      StringBuffer sb = new StringBuffer();
      for(Object _s:s)sb.append(_s);
      return sb.toString();
  }

  public void shutdown() {
      try {
          if(bds!=null){
        	  DriverManager.deregisterDriver(bds.getDriver());
        	  bds.getConnection().close();
              bds.close();
          }
          bds = null;
      } catch (SQLException ex) {
      }
  }
}
