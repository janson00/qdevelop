package cn.qdevelop.core.db.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.common.utils.QLog;
import cn.qdevelop.common.utils.QSource;
import cn.qdevelop.common.utils.QXMLUtils;
import cn.qdevelop.common.utils.SearchFileFromJars;
import cn.qdevelop.common.utils.SearchFileFromProject;
import cn.qdevelop.core.Contant;

public class SQLConfigLoader extends HashMap<String,Element>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2514613541934551967L;

	private static Logger log  = QLog.getLogger(SQLConfigLoader.class);

	private static SQLConfigLoader _SQLConfigLoader = new SQLConfigLoader();
	public static SQLConfigLoader getInstance(){return _SQLConfigLoader;}

	private static HashSet<String> tablesIndex;

	public SQLConfigLoader(){
		cleanSplit = Pattern.compile("[0-9a-zA-Z\\_]+");
		tablesIndex = new HashSet<String>();
		long s = System.currentTimeMillis();
		loadConfigFromJars();
		loadConfigFromProject();
		storeDebugXML();
		System.out.println("SQLConfigLoader load all *.sql.xml count:"+this.size()+" use:"+(System.currentTimeMillis()-s)+"ms");
		//		System.out.println(tablesIndex);
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
					Element root = new QXMLUtils().getDocument(is).getRootElement();
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
		final int projectIndex = QSource.getProjectPath().length();
		new SearchFileFromProject(){
			@SuppressWarnings("unchecked")
			@Override
			protected void disposeFile(File f) {
				try {
					Element root = new QXMLUtils().getDocument(f).getRootElement();
					if(root.getName().equals(Contant.SQL_CONFIG_ROOT)){
						initSqlConfig(root.elementIterator(),f.getAbsolutePath().substring(projectIndex),false);
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

	public static Pattern cleanSplit;
	@SuppressWarnings("unchecked")
	private void initSqlConfig(Iterator<Element> items,String fileName,boolean isJarConfig){
		log.info("load sqlConfig: "+fileName);
		while (items.hasNext()) {
			Element property = items.next();
			if (property.getName().equals("property")) {
				String index = property.attributeValue("index");
				Element ele = this.get(index);
				if(ele != null ){
					if(isJarConfig){
						log.warn("原文件【"+fileName+"】被当前【"+ele.attributeValue("file")+"】文件中index="+index+"的配置覆盖");
					}else{
						System.out.println("当前文件【"+fileName+"】\n和文件【"+ele.attributeValue("file")+"】\n存在重复索引 index : "+index+" ，系统自动退出！");
						System.exit(0);
					}
				}

				/**init default values**/
				property.addAttribute("file", fileName);
				if(property.attributeValue("connect")==null){
					property.addAttribute("connect", Contant.CONNECT_DEFAULT);
				}
				if(property.attributeValue("sql")!=null){
					Element s = property.addElement("sql");
					s.addText(cleanSQL(property.attributeValue("sql")));
					property.remove(property.attribute("sql"));
				}
				HashSet<String> tables = new HashSet<String>();
				Boolean isSelect = null;
				Iterator<Element> sqls = property.elementIterator("sql");
				while (sqls.hasNext()) {
					Element sql = sqls.next();
					String sqlStr = cleanSQL(sql.getText());
					if(sqlStr== null )continue;

					if(sql.attributeValue("repeat")!=null){
						String repeat = sql.attributeValue("repeat");

						try {
							if(sql.attributeValue("repeat-split")==null){
								String repearSplit=cleanSplit.matcher(repeat).replaceAll("");
								if(repearSplit.length() > 0){
									sql.addAttribute("repeat-split", repearSplit.substring(0,1));
								}
							}
							if(sql.attributeValue("repeat-concat")==null){
								sql.addAttribute("repeat-concat", "^");
							}
						} catch (Exception e) {
							System.out.println("==> "+repeat);
							e.printStackTrace();
						}
					}

					sql.setText(sqlStr);
					isSelect = sqlStr.length() > 6 && sqlStr.substring(0, 6).toLowerCase().equals("select") ? true: false;

					//					String execSql =  prest_sql_clean.matcher(sqlStr).replaceAll("?");
					//					if(sql.attributeValue("columns")==null){
					//						String fields = getParamsFileds(execSql,index,fileName);
					//						if(fields!=null){
					//							sql.addAttribute("columns", fields );
					//						}
					//					}
					String param = getParamKey(sqlStr);
					if(param!=null){
						sql.addAttribute("params", param);
					}


					//					/**兼容分析错误**/
					//					if(sql.attributeValue("columns")==null){
					//						sql.addAttribute("columns", sql.attributeValue("params"));
					//					}

					sql.addAttribute("is-select", isSelect.toString());
					if(isSelect){
						sql.addAttribute("tables", getSelectTableNames(sqlStr));
					}else{
						sql.addAttribute("tables", getUpdateTableName(sqlStr));
					}
					if(sql.attributeValue("fetch-zero-err")==null){
						sql.addAttribute("fetch-zero-err", "true");
					}
					//					sql.addAttribute("execSql",execSql);
					addTables(sql.attributeValue("tables"),property.attributeValue("connect"));
				}
				if(property.attributeValue("is-master")==null){
					property.addAttribute("is-master", isSelect?"false":"true");
				}
				property.addAttribute("is-select", isSelect.toString());

				//兼容历史配置
				Element resultFormatter = property.element("formatter");
				if(resultFormatter!=null){
					Element _r = property.addElement("result-formatter");
					xml.copyElement(resultFormatter, _r);
					property.remove(resultFormatter);
				}


				tables.clear();
				this.put(index, property);
			}
		}
	}
	QXMLUtils xml = new QXMLUtils();
	private void storeDebugXML(){
		Document sqlModelConfigCache = DocumentHelper.createDocument();
		Element sqlModelRoot = sqlModelConfigCache.addElement("sql-config-debug");
		Collection<Element> entries = this.values();
		for(Element ele : entries){
			Element e = sqlModelRoot.addElement("poperty"); 
			xml.copyElement(ele, e);
		}
		try {
			xml.save(sqlModelConfigCache, new File(QSource.getProjectPath()+"/qdevelop_sql_debug.xml"));
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
	//	private final Pattern fildsClear = Pattern.compile("=\\?");
	//	private final Pattern fildsInsertClear1 = Pattern.compile("^.+\\({1}|\\)$|`|\t");
	//	private final Pattern fildsInsertClear2 = Pattern.compile("^\\({1}|\\)$|`|\t");
	//	private final Pattern split = Pattern.compile(" value(s)?", Pattern.CASE_INSENSITIVE);
	//	private String getParamsFileds(String execSql,String index,String fileName){
	//		if(execSql.indexOf("?")==-1){
	//			return null;
	//		}
	//		String[] tmp;
	//		if(execSql.toLowerCase().startsWith("insert")){
	//			tmp =  execSql.toLowerCase().split(" ?value(s)?");
	//			if(tmp.length == 2){
	//				String[] filds = fildsInsertClear1.matcher(tmp[0].trim()).replaceAll("").split(",");
	//				String[] args = fildsInsertClear2.matcher(tmp[1].trim()).replaceAll("").split(",");
	//				if(filds.length == args.length){
	//					StringBuffer sb = new StringBuffer();
	//					for(int i = 0; i< filds.length ; i++){
	//						if(args[i].trim().equals("?")){
	//							sb.append("|").append(filds[i].trim());
	//						}
	//					}
	//					return sb.substring(1);
	//				}else{
	//					log.error("动态分析columns参数获取错误 ["+index+"]  ["+fildsInsertClear1.matcher(tmp[0]).replaceAll("")+"] ["+fildsInsertClear2.matcher(tmp[1]).replaceAll("")+"] in "+fileName);
	//					//					System.exit(0);
	//				}
	//			}else{
	//				log.error("动态分析columns参数获取错误 ["+index+"] {"+tmp[0]+"} in "+fileName);
	//				//				System.exit(0);
	//			}
	//
	//			return null;
	//		}
	//		tmp = execSql.replaceAll("," , " ").split(" ");
	//		StringBuffer sb = new StringBuffer();
	//		for(int i=0;i<tmp.length;i++){
	//			if(tmp[i]!=null && tmp[i].length() > 1 && tmp[i].endsWith("?")){
	//				sb.append("|").append(fildsClear.matcher(tmp[i].trim()).replaceAll(""));
	//			}
	//		}
	//		tmp = null;
	//		return sb.length() > 0 ? sb.substring(1) : null;
	//	}

	private Pattern tablePattern = Pattern
			.compile("^.+?INTO|^UPDATE| WHERE.+?$| SET.+?$| VALUE.+?$|\\(.+?\\)|^.+?FROM|`");

	private String getUpdateTableName(String... sqls) {
		if (sqls.length == 1) return tablePattern.matcher(sqls[0].toUpperCase()).replaceAll("").trim().toLowerCase();
		Set<String> tables = new HashSet<String>();
		for (String sql : sqls) {
			tables.add(cleanTableName.matcher(tablePattern.matcher(sql.toUpperCase()).replaceAll("").trim()).replaceAll(""));
		}
		return append(tables, "|").toLowerCase();
	}

	private Pattern cleanTableName = Pattern.compile("\\)| .+$|`");
	private  String getSelectTableNames(String sql) {
		Set<String> tables = new HashSet<String>(); 
		String[] tbs = sql.replaceAll("`", "").toUpperCase().split("FROM | JOIN ");
		for (String tb : tbs) {
			tb =tb.replaceAll(" WHERE .+| ORDER.+| GROUP.+|\\(.+", "").trim()
					.replaceAll(" .+,$|\\$\\[.+?\\]", "");
			if (tb.length() > 1 && !tb.startsWith("SELECT") && !tb.startsWith("(")) {
				if (tb.indexOf(",") == -1) {
					tables.add(cleanTableName.matcher(tb).replaceAll(""));
				} else {
					for (String ss : tb.split(",")) {
						tables.add(cleanTableName.matcher(ss).replaceAll(""));
					}
				}
			}
		}
		return append(tables,"|").toLowerCase();
	}

	private void addTables(String tables,String connect){
		if(tables==null||connect==null)return;
		String[] ts = tables.split("\\|");
		for(String t : ts){
			tablesIndex.add(t+"@"+connect);
		}
	}

	//	public static void main(String[] args) {
	//		//		Pattern cleanTableName = Pattern.compile("\\)| .+$|`");
	//		//		System.out.println(cleanTableName.matcher("customer_inventory_log    select customer_inventory_id,1,inventory_quantity,'签收入库',now(),0    from customer_inventory").replaceAll(""));
	//		Pattern o = Pattern.compile("\n|\t| +");
	//		Pattern t = Pattern.compile(" +");
	//		Element e = SQLConfigLoader.getInstance().get("test_query");
	//		System.out.println(SQLConfigLoader.getInstance().cleanSQL(e.elementText("sql")));
	//		//		String s = "update brand set `brand_name`='$[brand_name]',`english_name`='$[english_name]',`brand_type`=$[brand_type],`logo`='$[logo]',`first_letter`='$[first_letter]',`pc_level1_id`=$[pc_level1_id],`pc_level2_id`=$[pc_level2_id],`pc_level3_id`=$[pc_level3_id],`sort`=$[sort],`remark`='$[remark]',`uid`=$[uid],`uname`='$[uname]',`utime`=NOW() where brand_id=$[brand_id]";
	//		//		Pattern prestSqlClean = Pattern.compile("'?\\$\\[{1}[0-9a-zA-Z._]+\\]{1}'?",Pattern.CASE_INSENSITIVE);
	//		//		System.out.println(prestSqlClean.matcher(s).replaceAll("?"));
	//		//		System.out.println(e.asXML());
	//		//			System.out.println(StringEscapeUtils.unescapeXml("update order_use_stock set status=3 where order_id=? and status &lt;&gt; 3"));
	//	}

}