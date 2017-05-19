package cn.qdevelop.core.db.connect;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.common.utils.QSource;
import cn.qdevelop.common.utils.QString;
import cn.qdevelop.common.utils.QXMLUtils;
import cn.qdevelop.common.utils.SearchFileFromJars;
import cn.qdevelop.common.utils.SearchFileFromProject;
import cn.qdevelop.core.Contant;

public class ConnectFactory {

	private final static Logger log  = QLog.getLogger(ConnectFactory.class);

	final static ConcurrentHashMap<String,IConnect> connCache = new  ConcurrentHashMap<String,IConnect>();

	private static ConnectFactory _ConnectFactory = new ConnectFactory();
	private Lock lock = new ReentrantLock();
	private static byte[] locker = new byte[0];

	public static IConnect getInstance() throws QDevelopException{
		return getInstance(Contant.CONNECT_DEFAULT);
	}

	private static  Document databaseConfig;

	@SuppressWarnings("unchecked")
	private  void copyConfigNode(Document config,Element root,QXMLUtils xmlUtils){
		Iterator<Element> elem = config.getRootElement().elementIterator("connect");
		while(elem.hasNext()){
			Element e = elem.next();
			if(root.element(e.attributeValue("index"))==null){
				Element connect  = root.addElement("connect");
				xmlUtils.copyElement(e,connect);
			}else{
				log.warn(e.asXML());
			}
		}
	}

	public static void initAllConnect(){
		_ConnectFactory.loadConfig();
		@SuppressWarnings("unchecked")
		Iterator<Element> iter = databaseConfig.getRootElement().elementIterator("connect");
		while(iter.hasNext()){
			Element config = iter.next();
			try {
				_ConnectFactory.init(config.attributeValue("index"));
			} catch (QDevelopException e) {
				e.printStackTrace();
			}
		}

	}
	private void loadConfig(){
		if(databaseConfig!=null)return;
		try {
			System.out.println("**加载数据库配置文件qdevelop-database.xml**");
			databaseConfig= DocumentHelper.createDocument();
			final Element root = databaseConfig.addElement("database-config");
			final QXMLUtils xmlUtils = new QXMLUtils();
			final int projectIndex = QSource.getProjectPath().length();
			new SearchFileFromProject() {
				@Override
				protected void disposeFileDirectory(File f) {
					
				}
				@Override
				protected void disposeFile(File f) {
					log.info(QString.append("load database : ",f.getAbsolutePath().substring(projectIndex)));
					try {
						copyConfigNode(xmlUtils.getDocument(f,"UTF-8"),root,xmlUtils);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}
			}.searchProjectFiles("qdevelop-database.xml");
			
			new SearchFileFromJars(){
				@Override
				public void desposeFile(String jarName,String fileName, InputStream is) {
					try {
						if(root.getName().equals(Contant.CONNECT_CONFIG_ROOT)){
							log.info(QString.append("load database : ",jarName,"!",fileName));
							copyConfigNode(xmlUtils.getDocument(is, "UTF-8"),root,xmlUtils);
						}
					} catch (Exception e) {
						log.error("err:"+jarName+"!"+fileName);
						e.printStackTrace();
					}
				}
			}.searchAllJarsFiles("qdevelop-database.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				log.info("shutdown all connects!");
				ConnectFactory.shutdown();
			}
		});
	}
	private synchronized  IConnect init(final String config) throws QDevelopException{
		lock.lock();
		IConnect connection=null;
		try {
			loadConfig();
			connection = null;
			Element configs = (Element) databaseConfig.selectSingleNode(append("/database-config/connect[@index='",config,"']"));
			if(configs==null && config.endsWith("_R"))configs = (Element) databaseConfig.selectSingleNode(append("/database-config/connect[@index='",config.substring(0,config.length()-2),"']"));
			if(configs==null)throw new QDevelopException(1001,"数据库配置["+config+"]不存在！请检查src/databaseConfig.xml");
			connection=new DBCPConnect(configs);
			connection.printInfo();
			connCache.put(config, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		lock.unlock();
		return  connection;
	}

	public static Element getElementByIndex(String config){
		_ConnectFactory.loadConfig();
		return (Element) databaseConfig.selectSingleNode(append("/database-config/connect[@index='",config,"']"));
	}

	public static  IConnect getInstance(String config) throws QDevelopException{
		IConnect connection=null;
		try {
			if(config==null)config = Contant.CONNECT_DEFAULT;
			connection = connCache.get(config);
			if(connection==null && config.endsWith("_R")){//只读配置不存在时，转向主库
				connection = connCache.get(config.substring(0,config.length()-2));
			}
			if(connection==null){
				synchronized(locker){
					connection = _ConnectFactory.init(config);
				}
			}
			if(connection == null){
				throw new QDevelopException(1002,"数据库连接["+config+"]获取异常,也许还没有加入配置吧？？");
			}
			return connection;
		} catch (QDevelopException e) {
			e.printStackTrace();
			log.error(append("数据库连接获取异常\t",config," = [",(connection == null?"":connection.monitor()),"]"));
			throw new QDevelopException(1002,"数据库连接["+config+"]获取异常");
		}
	}

	public static String[] watchConnect(){
		if(connCache==null||connCache.size()==0)return new String[]{};
		Iterator<Map.Entry<String, IConnect>> itor = connCache.entrySet().iterator();
		String[] str = new String[connCache.size()];
		int i = 0;
		while(itor.hasNext()){
			Entry<String, IConnect> entry = itor.next();
			IConnect value = entry.getValue();
			str[i++] = value.monitor()+"|"+entry.getKey();
		}
		return str;
	}

	public static void shutdown(){
		if(connCache==null||connCache.size()==0)return;
		Iterator<Map.Entry<String, IConnect>> itor = connCache.entrySet().iterator();
		while(itor.hasNext()){
			Entry<String, IConnect> entry = itor.next();
			entry.getValue().shutdown();
		}
		connCache.clear();

	}

	public static void close(Connection conn){
		try {
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void clear(){
		shutdown();
		databaseConfig=null;
	}

	public static String getDatabase(String databaseConfig) throws QDevelopException{
		return getInstance(databaseConfig).getDataBase();
	}

	private static String append(Object ... s){
		StringBuffer sb = new StringBuffer();
		for(Object _s:s)sb.append(_s);
		return sb.toString();
	}

}

