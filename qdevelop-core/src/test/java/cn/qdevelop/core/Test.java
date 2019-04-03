package cn.qdevelop.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) throws IOException {
		List<String> content = new ArrayList<String>();
		File file = new File("/Users/janson/tmp/QA.txt");
		BufferedReader reader = null;
		try {
			//	            System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int start = -1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				if(tempString.trim().length()>0){
					tempString = tempString.trim();
					if(tempString.startsWith("问答")){
						start = 0;
					}
					if(start>0 && !tempString.startsWith("目录")){
						content.add(tempString);
					}
					if(tempString.startsWith("目录")){
						start = -1;
					}
//					System.out.println(tempString);
					if(start>-1){
						start++;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		FileOutputStream out = null;
		  out = new FileOutputStream(new File("/Users/janson/tmp/qa.csv"));  
		Pattern clean = Pattern.compile("A：|A:| ");
		ArrayList<String> answer=null;String title = null;
		for(int i = 0;i<content.size();i++){
			
			String cc = content.get(i);
//			out.write(cc.getBytes());
//			out.write("\n".getBytes());
			if(cc.endsWith("？")||cc.endsWith("?")){
				System.out.println(cc);
				title = cc;
				if(answer!=null){
//					answer.remove(answer.size()-1);
					for(int j=0;j<answer.size();j++){
						out.write(( "\""+(j==0?title:" ")+"\",\""+answer.get(j)+"\"" ).getBytes());
						out.write("\n".getBytes());
					}
				}
				answer = new ArrayList<String>(); 
			}
			if(answer!=null){
				String tmp = clean.matcher(cc).replaceAll("");
				if(tmp.trim().length()>0){
					answer.add(tmp.trim());
				}
			}
			
			
		}
		if(answer!=null){
			answer.remove(answer.size()-1);
			for(int j=0;j<answer.size();j++){
				out.write(( "\""+(j==0?title:" ")+"\",\""+answer.get(j)+"\"" ).getBytes());
			}
		}
		out.close();


		//		HashMap query = new HashMap();
		//		query.put("index", "products-search-action");
		//		query.put("pid", "50604|50605");
		//		try {
		//			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
		//			for(int i=0;i<rb.getSize();i++){
		//				System.out.println(rb.getResult(i));
		//			}
		//		} catch (QDevelopException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		//		QDevelopHelper.createSQLConfig("default", "products_log");
		//		String s = "insert into mytest_log(epuad_id,record,ctime) value ({my_test_0.LAST_INSERT_ID},'xxxxxxxx',now())";
		//		Pattern hasAutoIncrementParam = Pattern.compile("\\{[a-zA-z0-9_]+\\.LAST_INSERT_ID\\}");
		//		Pattern getAutoIncrementKey = Pattern.compile("^.*\\{|\\.LAST_INSERT_ID\\}.*$");
		//		System.out.println(getAutoIncrementKey.matcher(s).replaceAll(""));
		//		System.out.println(hasAutoIncrementParam.matcher(s).replaceAll("1"));
		//		Pattern clearColumnName = Pattern.compile("^.+?\\.|`");
		//		System.out.println(clearColumnName.matcher("e.`sad`").replaceAll(""));
		//		Pattern p = Pattern.compile("\\||>");
		//		//		String sql = "select * FROM janson where asdadasdasdasd";
		//		//		Pattern cleanPrev = Pattern.compile("^select .+ from", Pattern.CASE_INSENSITIVE);
		//		//		Matcher m = p.matcher("select * fRom (select * FROM janson)t");
		//		//		int i=0;
		//		//		while(m.find()){
		//		//			i++;
		//		//		}
		////		String[] tmp = "a".split("\\||>");
		////		for(String k : tmp)
		////			System.out.println(k);
		//		Pattern r = Pattern.compile("'\\?'");
		//		System.out.println(r.matcher("a'?'a").replaceAll("?"));

		//		QDevelopHelper.createSQLConfig("qd_product_center_write", "products");
		//		System.out.println(Charsets.UTF_8.toString());

	}



}

