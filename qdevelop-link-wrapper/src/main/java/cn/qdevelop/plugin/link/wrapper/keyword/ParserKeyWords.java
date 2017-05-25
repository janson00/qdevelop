package cn.qdevelop.plugin.link.wrapper.keyword;

import java.io.IOException;
import java.net.URLEncoder;

public class ParserKeyWords {
	
	public void getUrl(String text) {
		String sCurrentLine=null; 
		java.io.InputStream l_urlStream=null; 
		java.io.BufferedReader l_reader=null;
		java.net.HttpURLConnection l_connection=null;
		try {
			java.net.URL l_url = new java.net.URL(new StringBuffer().append("http://ltpapi.voicecloud.cn/analysis/?api_key=P1H4M822a4B0T854b980TzPf0GqvtvVPRUkPMmEg&pattern=ws&format=plain&text=").append(URLEncoder.encode(text, "utf-8")).toString()); 
			l_connection = (java.net.HttpURLConnection) l_url.openConnection();
			l_connection.connect(); 
			l_urlStream = l_connection.getInputStream(); 
			l_reader = new java.io.BufferedReader(new java.io.InputStreamReader(l_urlStream)); 
			while ((sCurrentLine = l_reader.readLine()) != null){ 
//				String[] tmp = sCurrentLine.split(" ");
//				for(String t:tmp){
//					if(t.length()>1){
//						
//					}
//				}
				System.out.println(sCurrentLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(l_reader!=null)
					l_reader.close();
				if(l_urlStream!=null)
					l_urlStream.close();
				l_connection=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public static void main(String[] args) {
		new ParserKeyWords().getUrl("【月瘦10斤起】买2送1左旋肉碱茶多酚高效配方只减脂肪不减水份不反弹 左旋肉碱");
	}

}
