package cn.qdevelop.core.db.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.files.QProperties;
import cn.qdevelop.common.files.QSource;
import cn.qdevelop.common.files.SearchFileFromJars;
import cn.qdevelop.common.files.SearchFileFromProject;
import cn.qdevelop.common.utils.QXMLUtils;
import cn.qdevelop.core.Contant;

public class SQLConfigLoader extends ConcurrentHashMap<String,Element>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2514613541934551967L;

	private static Logger log  = QLog.getLogger(SQLConfigLoader.class);

	private static SQLConfigLoader _SQLConfigLoader = new SQLConfigLoader();
	public static SQLConfigLoader getInstance(){return _SQLConfigLoader;}

	private static HashSet<String> tablesIndex;
	
	private	String is_full_param = "true",is_complex_build="true",is_convert_null="false",is_need_total = "false",fetch_zero_err = "true",result_format_type = "0";


	public SQLConfigLoader(){
		cleanSplit = Pattern.compile("[0-9a-zA-Z\\_]+");
		tableArgs = Pattern.compile("\\$\\[.+\\]");
		isOpenReg = Pattern.compile("\\/(api|open)\\/");
		nameSpaceClean = Pattern.compile("\\-[0-9]+?\\..+$");
		isJarFile = Pattern.compile("\\.jar\\!");
		cleanPrefix = Pattern.compile("^.*(/|\\\\)");
		clearJarPrefix = Pattern.compile("^.+\\.jar\\!");
		tablesIndex = new HashSet<String>();
		projectIndex = QSource.getProjectPath().length();
		
		is_full_param = QProperties.getInstance().getValue("sqlconfig_is_full_param", "true");
		is_complex_build = QProperties.getInstance().getValue("sqlconfig_is_complex_build", "true");
		is_convert_null = QProperties.getInstance().getValue("sqlconfig_is_convert_null", "false");
		is_need_total = QProperties.getInstance().getValue("sqlconfig_is_need_total", "false");
		fetch_zero_err = QProperties.getInstance().getValue("sqlconfig_fetch_zero_err", "true");
//		System.out.println(QProperties.getInstance().getValue("sqlconfig_fetch_zero_err", "true"));
		
		isExistIndex = new Boolean(false);
		errIndexInfo = new ArrayList<String>();
		
		long s = System.currentTimeMillis();
		loadConfigFromProject();
		loadConfigFromJars();
		storeDebugXML();
		System.out.println("SQLConfigLoader load all *.sql.xml count:"+this.size()+" use:"+(System.currentTimeMillis()-s)+"ms");
		if(isExistIndex){
			for(String info : errIndexInfo){
				System.err.println(info);
			}
			System.out.println("检测SQLConfig存在不可识别的重复索引Index值，系统强制退出；请认真检查以上文件。");
//			System.exit(0);
		}
		//		System.out.println(tablesIndex);
	}
	
	/**
	 * 热加载本地配置文件
	 */
	public void hotLoadConfig(){
		loadConfigFromProject();
		loadConfigFromJars();
		storeDebugXML();
	}

	/**
	 *  获取sql配置
	 * @param index
	 * @return
	 */
	public Element getSQLConfig(String index){
		return this.get(index);
	}


	private void loadConfigFromJars(){	
		new SearchFileFromJars(){
			@SuppressWarnings("unchecked")
			@Override
			public void desposeFile(String jarName,String fileName, InputStream is) {
				try {
					Element root = xmlUtils.getDocument(is).getRootElement();
					if(root.getName().equals(Contant.SQL_CONFIG_ROOT)){
						initSqlConfig(root.elementIterator(),jarName+"!"+fileName,true);
					}
				} catch (Exception e) {
					log.error(jarName+"!"+fileName,e);
					e.printStackTrace();
				}
			}
		}.searchAllJarsFiles("*.sql.xml$");
	}

	private void loadConfigFromProject(){
		new SearchFileFromProject(){
			@SuppressWarnings("unchecked")
			@Override
			protected void disposeFile(File f) {
				try {
					Element root = xmlUtils.getDocument(f).getRootElement();
					if(root.getName().equals(Contant.SQL_CONFIG_ROOT)){
						initSqlConfig(root.elementIterator(),f.getAbsolutePath().substring(projectIndex).replaceAll("\\\\", "/"),false);
					}
				} catch (Exception e) {
					log.error(f.getAbsolutePath(),e);
					e.printStackTrace();
				}
			}

			@Override
			protected void disposeFileDirectory(File f) {
			}
		}.searchProjectFiles("*.sql.xml$");
	}



	private static int projectIndex = 0;

	/**
	 * 热加载本地指定配置文件
	 * @param configPath
	 */
	@SuppressWarnings("unchecked")
	public void  hotLoadConfigFile(File configPath){
		try {
			Element root = xmlUtils.getDocument(configPath).getRootElement();
			if(root.getName().equals(Contant.SQL_CONFIG_ROOT)){
				Iterator<Element> items = root.elementIterator();
				String fileName  = configPath.getAbsolutePath().substring(projectIndex);
				log.info("hot load sqlConfig: "+fileName);
				while (items.hasNext()) {
					Element property = items.next();
					if (property.getName().equals("property")) {
						initProperty(property,fileName.replaceAll("\\\\", "/"));
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private Boolean isExistIndex ;
	private ArrayList<String> errIndexInfo;

	private static Pattern cleanPrefix,clearJarPrefix;
	private void initSqlConfig(Iterator<Element> items,String fileName,boolean isJarConfig){
		log.info("load sqlConfig: "+fileName);
		while (items.hasNext()) {
			Element property = items.next();
			if (property.getName().equals("property")) {
				String index = property.attributeValue("index");
				Element ele = this.get(index);
				if(ele != null ){
					if(isJarConfig){
						System.err.println("【"+index+"】 原文件【"+fileName+"】\r\n\t 被当前【"+ele.attributeValue("file")+"】文件中的配置覆盖");
						if(clearJarPrefix.matcher(fileName).replaceAll("").equals(clearJarPrefix.matcher(ele.attributeValue("file")).replaceAll(""))){
							//System.out.println();
							log.warn("【"+index+"】 原文件【"+fileName+"】被当前【"+ele.attributeValue("file")+"】文件中的配置覆盖");
						}else{
							//System.out.println();
							errIndexInfo.add("【"+index+"】 当前文件【"+fileName+"】\n和文件【"+ele.attributeValue("file")+"】存在重复索引");
							isExistIndex = true;
							//System.exit(0);
						}
					}else{
						if(cleanPrefix.matcher(fileName).replaceAll("").equals(cleanPrefix.matcher(ele.attributeValue("file")).replaceAll(""))){
							System.err.println("【"+index+"】 文件【"+fileName+"】和【"+ele.attributeValue("file")+"】含有重复索引，本次检测跳过，如不是临时编译文件，请手动检查index："+index);
							log.warn("【"+index+"】 文件【"+fileName+"】和【"+ele.attributeValue("file")+"】含有重复索引，本次检测跳过，如不是临时编译文件，请手动检查index："+index);
							continue;
						}
						errIndexInfo.add("【"+index+"】当前文件【"+fileName+"】\n和文件【"+ele.attributeValue("file")+"】");
						isExistIndex = true;
						//System.exit(0);
					}
				}
				try {
					initProperty(property,fileName);
				} catch (Exception e) {
					System.err.println(fileName);
					System.err.println(property.asXML());
					e.printStackTrace();
				}
			}
		}
	}


	public static Pattern cleanSplit,tableArgs,isOpenReg,nameSpaceClean,isJarFile;
	private void initProperty(Element property,String fileName){
		/**init default values**/
		property.addAttribute("file", fileName);
		if(property.attributeValue("connect")==null){
			property.addAttribute("connect", Contant.CONNECT_DEFAULT);
		}
		if(property.attributeValue("is-open")==null){
			property.addAttribute("is-open", String.valueOf(isOpenReg.matcher(fileName).find()));
		}
		property.addAttribute("name-space", isJarFile.matcher(fileName).find()?nameSpaceClean.matcher(fileName).replaceAll(""):"");
		if(property.attributeValue("sql")!=null){
			Element s = property.addElement("sql");
			s.addText(cleanSQL(property.attributeValue("sql")));
			property.remove(property.attribute("sql"));
		}
		HashSet<String> tables = new HashSet<String>();
		Boolean isSelect = null;
		@SuppressWarnings("unchecked")
		Iterator<Element> sqls = property.elementIterator("sql");
		while (sqls.hasNext()) {
			Element sql = sqls.next();
			String sqlStr = cleanSQL(sql.getText());
			if(sqlStr== null )continue;
			
			String param = getParamKey(sqlStr);
			if(param!=null){
				sql.addAttribute("params", param);
			}
			
			if(sql.attributeValue("repeat")!=null){
				String repeat = sql.attributeValue("repeat").trim();
				if(repeat.length()>0){
					try {
						if(sql.attributeValue("repeat-split")==null){
							if(param!=null){
								String[] tmp = param.split("\\|");
								String repearSplit = new String(repeat);
								for(String t:tmp){
									repearSplit = repearSplit.replace(t, "");
								}
								if(repearSplit.length()>0){
									sql.addAttribute("repeat-split", repearSplit.substring(0,1));
								}
							}else{
								String repearSplit=cleanSplit.matcher(repeat).replaceAll("");
								if(repearSplit.length() > 0){
									sql.addAttribute("repeat-split", repearSplit.substring(0,1));
								}
							}
						}
						if(sql.attributeValue("repeat-concat")==null){
							sql.addAttribute("repeat-concat", "^");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					sql.remove(sql.attribute("repeat"));
				}
			}

			sql.setText(sqlStr);
			isSelect = sqlStr.length() > 6 && sqlStr.substring(0, 6).toLowerCase().equals("select") ? true: false;

			

			sql.addAttribute("is-select", isSelect.toString());
			if(sql.attributeValue("tables")==null){
				if(isSelect){
					sql.addAttribute("tables", getSelectTableNames(sqlStr));
				}else{
					sql.addAttribute("tables", getUpdateTableName(sqlStr));
				}
				if(tableArgs.matcher(sql.attributeValue("tables")).find()){
					System.err.println("【配置错误系统退出】 文件："+fileName+" 中\r\n	index="+property.attributeValue("index")
					+"含有动态表名，请在sql中指定参考表结构的表名，\r\n	例：<sql tables=\""+tableArgs.matcher(sql.attributeValue("tables")).replaceAll("")+"\">...</sql>");
					System.exit(0);
				}
			}
			if(sql.attributeValue("fetch-zero-err")==null){
				sql.addAttribute("fetch-zero-err", fetch_zero_err);
			}
			addTables(sql.attributeValue("tables"),property.attributeValue("connect"));
			if(sql.attributeValue("is-full-param")==null){
				sql.addAttribute("is-full-param", is_full_param);
			}
		}
//		if(property.attributeValue("is-master")==null){
//			property.addAttribute("is-master", isSelect?"false":"true");
//		}
		//是否需要转义编译SQL
		if(property.attributeValue("is-complex-build")==null){
			property.addAttribute("is-complex-build", is_complex_build);
		}
		//是否需要覆盖空值数据进行展示，字符串转为空串，数值型转为-1
		if(property.attributeValue("is-convert-null")==null){
			property.addAttribute("is-convert-null", is_convert_null);
		}
		//查询是否返回总记录数
		if(property.attributeValue("is-need-total")==null){
			property.addAttribute("is-need-total", is_need_total);
		}
		//result_format_type
		if(property.attributeValue("result-format-type")==null){
			property.addAttribute("result-format-type", result_format_type);
		}
		property.addAttribute("is-select", isSelect.toString());


		//兼容历史配置
		Element resultFormatter = property.element("formatter");
		if(resultFormatter!=null){
			Element _r = property.addElement("result-formatter");
			xmlUtils.copyElement(resultFormatter, _r);
			property.remove(resultFormatter);
		}
		tables.clear();
		this.put(property.attributeValue("index"), property);
	}

	private QXMLUtils xmlUtils = new QXMLUtils();
	private void storeDebugXML(){
		Document sqlModelConfigCache = DocumentHelper.createDocument();
		Element sqlModelRoot = sqlModelConfigCache.addElement("sql-config-debug");
		Collection<Element> entries = this.values();
		for(Element ele : entries){
			Element e = sqlModelRoot.addElement("poperty"); 
			xmlUtils.copyElement(ele, e);
		}
		try {
			xmlUtils.save(sqlModelConfigCache, new File(QSource.getProjectPath()+"/qdevelop_sql_debug.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 整理SQL语句格式；将使用者写的sql格式做一个统一
	 * 
	 * @param sql
	 * @return
	 * @throws QDevelopException
	 */
	private String cleanSQL(String sql)  {
		return sql_clean_2.matcher(sql_clean_3.matcher(sql_clean_1.matcher(sql.replace("$[_autoSearch]", Contant.AUTO_SEARCH_MARK).replace("{DYNAMIC}", Contant.AUTO_SEARCH_MARK)).replaceAll(" ").trim()).replaceAll(" ")).replaceAll("=");
	}
	//	private Pattern prest_sql_clean = Pattern.compile("'?\\$\\[{1}[0-9a-zA-Z._]+\\]{1}'?");
	private Pattern sql_clean_1 = Pattern.compile("\n|\t| +");
	private Pattern sql_clean_2 = Pattern.compile(" ?= ?");
	private Pattern sql_clean_3 = Pattern.compile(" +");
	private Pattern param_clear_1 = Pattern.compile("\\@\\[.+?\\]|SEQID\\.NEXT\\[.+?\\]");
	private Pattern param_clear_2 = Pattern.compile("\\].+?\\$\\[");
	private Pattern param_clear_3 = Pattern.compile("^.+?\\$\\[|\\].+?$|\\]");

	private String getParamKey(String sql) {
		if (sql.indexOf("$[") == -1) return null;
		return param_clear_3.matcher(
				param_clear_2.matcher(param_clear_1.matcher(sql).replaceAll("")).replaceAll("|"))
				.replaceAll("");
	}

	@SuppressWarnings("unused")
	private String append(Object... s) {
		StringBuffer sb = new StringBuffer();
		for (Object _s : s)
			sb.append(_s);
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String append(String[] s, String split) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			if (i > 0) sb.append(split);
			sb.append(s[i]);
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	private String append(Set s, String split) {
		StringBuffer sb = new StringBuffer();
		Iterator it = s.iterator();
		for (; it.hasNext();) {
			sb.append(split).append(it.next());
		}
		return sb.length() > 0 ? sb.toString().substring(1) : "";
	} 

	private Pattern tablePattern = Pattern
			.compile("^.+?into|^update| where.+?$| set.+?$| value.+?$|\\(.+?\\)|^.+?from ",Pattern.CASE_INSENSITIVE);

	private String getUpdateTableName(String... sqls) {
		if (sqls.length == 1) return tablePattern.matcher(sqls[0]).replaceAll("").trim();
		Set<String> tables = new HashSet<String>();
		for (String sql : sqls) {
			tables.add(cleanTableName.matcher(tablePattern.matcher(sql).replaceAll("").trim()).replaceAll(""));
		}
		return append(tables, "|");
	}

	private Pattern cleanTableName = Pattern.compile("\\)| .+$|`");
	private Pattern selectAnalsys = Pattern.compile(" from | join ",Pattern.CASE_INSENSITIVE);
	private Pattern cleanSelectRight = Pattern.compile(" where .+$| on .+$| union .+$|select .+ from|\\(|\\)",Pattern.CASE_INSENSITIVE);
	private Pattern cleanSelectTable = Pattern.compile(" .+$|\\)(.+)?$",Pattern.CASE_INSENSITIVE);
	private Pattern isRightName = Pattern.compile("^[0-9a-zA-Z_]+$");

	private  String getSelectTableNames(String sql) {
		Set<String> tables = new HashSet<String>(); 
		Matcher matcher = selectAnalsys.matcher(sql);
		while (matcher.find()) {
			String t = cleanSelectRight.matcher(sql.substring(matcher.end()).trim()).replaceAll("").trim();
			if(t.startsWith("(") || t.toLowerCase().startsWith("select")){
				continue;
			}
			if(t.indexOf(",") > -1 ){
				String[] tmp = t.split(",");
				for(int i=0;i<tmp.length;i++){
					tmp[i] = cleanSelectTable.matcher(tmp[i].trim()).replaceAll("");
					if(isRightName.matcher(tmp[i]).find()){
						tables.add(tmp[i]);
					}
				}
			}else{
				t= cleanSelectTable.matcher(t).replaceAll("");
				if(isRightName.matcher(t).find()){
					tables.add(t);
				}
			}
		}
		return append(tables,"|");
	}

	private void addTables(String tables,String connect){
		if(tables==null||connect==null)return;
		String[] ts = tables.split("\\|");
		for(String t : ts){
			tablesIndex.add(t+"@"+connect);
		}
	}
}
