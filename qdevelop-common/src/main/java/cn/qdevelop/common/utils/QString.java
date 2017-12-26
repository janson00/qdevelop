package cn.qdevelop.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 字符串处理相关辅助类
 * @author Janson.Gu
 *
 */
@SuppressWarnings("unchecked")
public class QString {
	//	private static QString _QString = new QString();
	//	public static QString getInstance(){
	//		return _QString;
	//	}
	/**
	 * 
	 * 将网页HTML语言以文字的形式显示在网页中
	 * 
	 * @param str 传入的网页源码
	 * @return String 替换后的可在网页中显示的网页源码
	 */
	public static String htmlToStr(String str){
		str = str.replaceAll("<","&lt;");
		str = str.replaceAll(">","&gt;");
		str=str.replaceAll("\"", "&quot;");
		return str;
	}

	/**
	 * 
	 * 将特殊字符转换为可传输的字符<br>
	 * 例：用于通过get方式传输特殊的sql语句
	 * @param str 待转换的特殊字符串
	 * @return String 转换好的可传输的字符串
	 */
	public static String formatSpecilStr(String str){
		str=str.replaceAll("%","%25");
		str=str.replaceAll("\\+","%2B");
		str=str.replaceAll("/","%2F");
		str=str.replaceAll("#","%23");
		str=str.replaceAll("&","%26");
		str=str.replaceAll("?","%3F");
		str=str.replaceAll("=","%3D");
		str=str.replaceAll(" ","%20");
		return str;
	}
	/**
	 * 获取一个String类型的ID 共计18位<br>
	 * 现有系统使用，得出来的ID是没有重复出现的可能性
	 * @return String
	 */
	public static String getSequenceId(){		
		return System.currentTimeMillis()+""+(int)(Math.random()*100000);
	}
	/**
	 * 32位MD5加密后输出
	 * @param args
	 * @return String
	 */
	public static String get32MD5(String args){ 
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'}; 
		try { 
			byte[] strTemp = args.getBytes(); 
			MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
			mdTemp.update(strTemp); 
			byte[] md = mdTemp.digest(); 
			int j = md.length; 
			char str[] = new char[j * 2]; 
			int k = 0; 
			for (int i = 0; i < j; i++) { 
				byte byte0 = md[i]; 
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; 
				str[k++] = hexDigits[byte0 & 0xf]; 
			} 
			return new String(str); 
		} 
		catch (Exception e){ 
			return null; 
		} 
	} 

	

	/**
	 * 字符串转double
	 * @param num
	 * @return
	 */
	public static double parseDouble(String num){
		try{
			if(num!=null&&!num.trim().equals("")){
				return Double.parseDouble(num);
			}
		}catch(Exception e){
		}
		return 0.0;
	}

	public static String[] split(String str){
		if(str!=null){
			return str.split(",");
		}
		return null;
	}

	/**
	 * 判断数据非空，转化字符串
	 * @param str
	 * @return
	 */
	public static String formatStr(String str){
		if(str!=null&&!str.trim().equals("null")){
			try {
				return new String(str.getBytes("ISO-8859-1"),"GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 字符串转int
	 * @param num
	 * @return
	 */
	public static int parseInt(String num){
		try{
			if(num!=null){
				return Integer.parseInt(num.trim());
			}
		}catch(Exception e){
		}
		return 0;
	}

	public static boolean parseBoolean(String _boolean){
		if(_boolean==null)return false;
		return Boolean.parseBoolean(_boolean.trim());
	}

	public static String append(Object ... s){
		StringBuffer sb = new StringBuffer();
		for(Object _s:s)sb.append(_s);
		return sb.toString();
	}
	public static String append(Object[] s,String split){
		StringBuffer sb = new StringBuffer();
		for(Object _s:s){
			if(sb.length()>0)sb.append(split);
			sb.append(_s);
		}
		return sb.toString();
	}

	public static String append(List<Object> s,String split){
		StringBuffer sb = new StringBuffer();
		for(Object _s:s){
			if(sb.length()>0)sb.append(split);
			sb.append(_s);
		}
		return sb.toString();
	}


	@SuppressWarnings("rawtypes")
	public static String append(Set s,String split){
		StringBuffer sb = new StringBuffer();
		Iterator it = s.iterator();
		for(;it.hasNext();){
			sb.append(split).append(it.next());
		}
		return sb.length()>0?sb.toString().substring(1):"";

	}

	@SuppressWarnings("rawtypes")
	public static List toList(Object[] args){
		List t = new ArrayList();
		for(Object o:args)t.add(o);
		return t;
	}

	/**
	 * 中文转unicode
	 * @param str
	 * @return 反回unicode编码
	 */
	public static String  chinaToUnicode(String str){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++){
			int chr1 = (char) str.charAt(i);
			if((chr1 >= 0x4e00 && chr1<=0x9fbb)
					//	||( chr1 >=0xFF01 &&chr1 >=0xFF5E)
					){
				result.append("\\u").append(Integer.toHexString(chr1).toUpperCase()); 
			}else{
				result.append(str.substring(i,i+1));
			}
		}
		return result.toString();
	}
	
	public static String getNow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	
	public static boolean isNull(String obj) {
		if(obj == null || "".equals(obj) || "null".equals(obj))
			return true;
		return false;
	}
	

//	/**
//	 * 64 MD5 加密
//	 * @param arg
//	 * @return
//	 */
//	public static String get64MD5(String arg){
//		if(arg==null)return null;
//		try {
//			MessageDigest md5 = MessageDigest.getInstance("MD5");
//			BASE64Encoder base64en = new BASE64Encoder();
//			return base64en.encode(md5.digest(arg.getBytes("utf-8")));
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return  null;
//	}
	
//	public static void main(String[] args) {
//		System.out.println(QString.get64MD5("tianrongxin123"));
//	}

}
