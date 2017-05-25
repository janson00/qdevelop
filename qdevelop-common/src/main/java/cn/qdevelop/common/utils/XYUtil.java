package cn.qdevelop.common.utils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public final class XYUtil {

	/**
	 * 转换成byte
	 * @param obj
	 * @return
	 */
	public final static Byte transformByte(Object obj){
		Byte val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Byte){
				val = (Byte) obj;
			}else if(obj instanceof Number){
				val = ((Number)obj).byteValue();
			}else if(obj instanceof Boolean){
				val = (byte) (((Boolean)obj).booleanValue()?1:0);
			}else{
				try{
					val = Byte.valueOf(obj.toString());
				}catch(Exception e){val=null;}
			}
		}
		return val;
	}

	/**
	 * 转换成short
	 * @param obj
	 * @return
	 */
	public final static Short transformShort(Object obj){
		Short val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Short){
				val = (Short) obj;
			}else if(obj instanceof Number){
				val = ((Number)obj).shortValue();
			}else{
				try{
					val = Short.valueOf(obj.toString());
				}catch(Exception e){val=null;}
			}
		}
		return val;
	}

	/**
	 * 转换成integer
	 * @param obj
	 * @return
	 */
	public final static Integer transformInteger(Object obj){
		Integer val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Integer){
				val = (Integer) obj;
			}else if(obj instanceof Number){
				val = ((Number)obj).intValue();
			}else{
				try{
					val = Integer.valueOf(obj.toString());
				}catch(Exception e){val=null;}
			}
		}
		return val;
	}

	/**
	 * 转换成long
	 * @param obj
	 * @return
	 */
	public final static Long transformLong(Object obj){
		Long val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Long){
				val = (Long) obj;
			}else if(obj instanceof Number){
				val = ((Number)obj).longValue();
			}else{
				try{
					val = Long.valueOf(obj.toString());
				}catch(Exception e){val=null;}
			}
		}
		return val;
	}

	/**
	 * 转换成BigDecimal
	 * @param obj
	 * @return
	 */
	public final static BigDecimal transformBigDecimal(Object obj){
		BigDecimal val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof BigDecimal){
				val = (BigDecimal) obj;
			}else if(obj instanceof String){
				val = new BigDecimal((String)obj);
			}else{
				try{
					val = new BigDecimal(obj.toString());
				}catch(Exception e){val=null;}
			}
		}
		return val;
	}

	/**
	 * 转换成BigDecimal
	 * @param obj
	 * @return
	 */
	public final static Date transformDate(Object obj){
		Date val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Date){
				val = (Date) obj;
			}else if(obj instanceof Number){
				val = new Date(((Number)obj).longValue());
			}else{
				val = null;
			}
		}
		return val;
	}

	/**
	 * 转换成BigDecimal
	 * @param obj
	 * @return
	 */
	public final static Timestamp transformTimestamp(Object obj){
		Timestamp val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof Timestamp){
				val = (Timestamp) obj;
			}else if(obj instanceof Date){
				val = new Timestamp(((Date)obj).getTime());
			}else{
				val = null;
			}
		}
		return val;
	}

	/**
	 * 转换byte[]
	 * @param obj
	 * @return
	 */
	public final static byte[] transformByteArray(Object obj){
		byte[] val;
		if(obj==null){
			val = null;
		}else{
			if(obj instanceof byte[]){
				val = (byte[]) obj;
			}else if(obj instanceof java.sql.Blob){
				Blob blob = (java.sql.Blob)obj;
				try{
					val = blob.getBytes(0L, (int)blob.length());
				}catch(Exception e){val=null;}
			}else{
				val = null;
			}
		}
		return val;
	}

	/**
	 * 判断字符串是否为空
	 */
	public final static boolean isEmpty(String str){
		return str==null?true:str.trim().length()<1;
	}

	/**
	 * 判断object是否不为空
	 */
//	public final static boolean isEmpty(Object obj){
//		return obj==null ? true : isEmpty(obj.toString());
//	}

	/**
	 * 移除空白字符(ascii表中小于32的字符)
	 * @param str
	 * @return
	 */
	public final static String removeWhiteSpaceChars(String str) {
        if (str==null || str.length()<1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
        	if(chars[i]>31){
        		chars[pos++] = chars[i];
        	}
        }
        return chars.length==pos ? str : new String(chars, 0, pos);
    }

	/**
	 * 截取字符串
	 * @param str
	 * @param len
	 * @return
	 */
	public final static String subStr(String str, int len){
		if(str!=null && len>0){
			if(str.length()>len){
				str = str.substring(0, len);
			}
		}
		return str;
	}

	/**
	 * 进制转换
	 * 目前支持 二进制,八进制,十六进制,三十二进制,六十四进制转换
	 * 只能转换正数和零
	 * 二进制,八进制,十六进制,三十二进制 转换前后应该均为小写字母
	 * 六十四进制 转换前后字母大小写敏感
	 * */
	private final static char[] DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z', '$', '_'
		};

	/**
	 * 将Long转换为指定进制字符串
	 */
	private static String toString(long i, int shift) {
		char[] buf = new char[64];
		int charPos = 64;
		int radix = 1 << shift;
		long mask = radix - 1;
		do {
			buf[--charPos] = DIGITS[(int) (i & mask)];
			i >>>= shift;
		} while (i != 0);
		return new String(buf, charPos, (64 - charPos));
	}

	/**
	 * 将指定进制字符串转换为Long
	 * @param decompStr
	 * @param shift
	 * @return
	 */
	private static Long fromString(String decompStr, int shift) {
		long result = 0;
		final char[] da = decompStr.toCharArray();
		for (int i=da.length-1 ; i>=0; i--) {
			if (i == da.length-1) {
				long val = getVal(da[i]);
				if(val==-1){//非法字符
					return null;
				}
				result += val;
				continue;
			}
			for (int j=0; j<DIGITS.length; j++) {
				if (da[i] == DIGITS[j]) {
					result += ((long) j) << shift * (da.length - 1 - i);
					break;
				}else if(j==DIGITS.length-1){//非法字符
					return null;
				}
			}
		}
		return result;
	}
	
    /** 
     * 个位值运算
     */  
	private static long getVal(char ch) {
		int num = ((int) ch);
		if (num >= 48 && num <= 57) {//0-9
			return num - 48;
		} else if (num >= 97 && num <= 122) {//a-z
			return num - 87;
		} else if (num >= 65 && num <= 90) {//A-Z
			return num - 29;
		} else if (num == 36) {//'$'
			return 62;
		} else if (num == 95) {//'_'
			return 63;
		}
		return -1;
	} 

	/**
	 * 转二进制
	 * */
	public static String toBinary(long i) {
		return toString(i, 1);
	}

	/**
	 * 二进制数转Long
	 * */
	public static Long fromBinary(String s) {
		return fromString(s, 1);
	}

	/**
	 * 转八进制
	 * */
	public static String toOctal(long i) {
		return toString(i, 3);
	}

	/**
	 * 八进制数转Long
	 * */
	public static Long fromOctal(String s) {
		return fromString(s, 3);
	}

	/**
	 * 转十六进制
	 * @return 字母小写
	 * */
	public static String toHex(long i) {
		return toString(i, 4);
	}

	/**
	 * 十六进制数转Long
	 * @param s 字母小写
	 * */
	public static Long fromHex(String s) {
		return fromString(s, 4);
	}

	/**
	 * 转三十二进制
	 * @return 字母小写
	 * */
	public static String toM32(long i) {
		return toString(i, 5);
	}

	/**
	 * 三十二进制数转Long
	 * @param s 字母小写
	 * */
	public static Long fromM32(String s) {
		return fromString(s, 5);
	}

	/**
	 * 转六十四进制
	 * 字母大小写敏感
	 * */
	public static String toM64(long i) {
		return toString(i, 6);
	}

	/**
	 * 六十四进制数转Long
	 * 字母大小写敏感
	 * */
	public static Long fromM64(String s) {
		return fromString(s, 6);
	}

	public final static String randomString(int len){
		if(len<1){
			len=6;
		}
		java.security.SecureRandom sr = new java.security.SecureRandom();
		char[] ch = new char[len];
		for(int i=0;i<len;i++){
			ch[i]=DIGITS[sr.nextInt(DIGITS.length-2)];
		}
		return String.valueOf(ch);
	}

	/**
	 * 分割字符串
	 * */
	public final static String[] split(String str, char separatorChar) {
		if (str == null || str.length()==0) {
			return null;
		}
		int len = str.length();
		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				start = ++i;
				continue;
			}
			lastMatch = false;
			match = true;
			i++;
		}
		if (match || lastMatch) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public final static String DATE_FORMAT = "yyyy-MM-dd";

	public final static String TIME_FORMAT = "HH:mm:ss";

	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public final static String formatDate(Date date){
		return formatDate(date, DATE_TIME_FORMAT);
	}

	public final static String formatDate(Date date, String format){
		if(date!=null && format!=null){
			java.text.DateFormat df = new java.text.SimpleDateFormat(format);
			return df.format(date);
		}
		return null;
	}

	public final static String transformString(Object obj){
		if(obj!=null){
			return (obj instanceof String) ? (String)obj : obj.toString();
		}
		return null;
	}

	public final static String encode(String s, String charset){
		if(s!=null && s.length()>0){
			try{
				return java.net.URLEncoder.encode(s, charset);
			}catch(Exception e){}
		}
		return s;
	}

	/**
	 * 是否是合法的email
	 * @return 
	 * */
	public static boolean isEmail(String s){
		if(s==null || s.length()<1){
			return false;
		}else{
			int _p1 = s.indexOf('@');//查找'@'位置
			if(_p1==-1){//没有'@'符号
				return false;
			}else{
				String name = s.substring(0, _p1), domain=s.substring(_p1+1);
				char[] ext = new char[]{'-','_','.'};
				if(isDomain(domain)){
					char lc=name.charAt(name.length()-1), fc=name.charAt(0);
					for(char c : ext){
						if(c==lc || c==fc){
							return false;
						}
					}
					char pc=(char)-1, cc=(char)-1, nc=(char)-1;//上一个字符:pc, 当前字符:cc,下一个字符:nc
					for(int i=0 ; i<name.length() ; i++){
						cc = name.charAt(i);
						if(!isAlphaNum(cc, ext)){
							return false;
						}
						if(i<name.length()-1){//不是最后一个字符
							nc = name.charAt(i+1);
						}
						if(contain(ext, pc) && contain(ext, cc)){//连续2个字符是特定字符
							return false;
						}else if(contain(ext, cc) && contain(ext, nc)){//连续2个字符是特定字符
							return false;
						}
						pc = cc;
					}
					return true;
				}else{
					return false;
				}
			}
		}
	}

	/**
	 * 是否是域名
	 * */
	public final static boolean isDomain(String s){
		if(s==null || s.length()<1){
			return false;
		}
		String[] aa = split(s, '.');
		if(aa==null || aa.length<1){
			return false;
		}
		char[] ext = new char[]{'-'};
		for(int i=0;i<aa.length;i++){
			if(i<aa.length-1){//只要不是顶级域名
				char lc = aa[i].charAt(aa[i].length()-1), fc=aa[i].charAt(0);
				if('-'==lc || fc=='-'){
					return false;
				}
				char pc=(char)-1, cc=(char)-1, nc=(char)-1;//上一个字符:pc, 当前字符:cc,下一个字符:nc
				for(int j=0;j<aa[i].length();j++){
					cc=aa[i].charAt(j);
					if(isAlphaNum(cc, ext)){
						if(j<aa[i].length()-1){//不是最后一个字符
							nc = aa[i].charAt(j+1);
						}
						if(j>0 && j<aa[i].length()-1){//不是第一位也不是最后一位
							//不能有连续两个'-'
							if(pc=='-' && cc=='-'){
								return false;
							}else if(cc=='-' && nc=='-'){
								return false;
							}
						}
						pc = cc;
					}else{
						return false;
					}
				}
			}else{//顶级域名,必须由字母组成
				return isAlpha(aa[i]);
			}
		}
		return true;
	}

	/**
	 *  是否是合法字符:ascii表中的字母,数字,下划线,短横线
	 * */
	private final static boolean isAlphaNum(char c, char[] ext){
		if(c>47 && c<58){//0-9
			return true;
		}else if(c>64 && c<91){//A-Z
			return true;
		}else if(c>96 && c<123){//a-z
			return true;
		}else{//拓展字符数组
			if(ext!=null && ext.length>0){
				for(char a : ext){
					if(a==c){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *  是否是合法字符:ascii表中的字母
	 * */
	private final static boolean isAlpha(char c){
		if(c>64 && c<91){//A-Z
			return true;
		}else if(c>96 && c<123){//a-z
			return true;
		}
		return false;
	}

	/**
	 *  是否是合法字符串:ascii表中的字母
	 * */
	public final static boolean isAlpha(String s){
		if(s==null || s.length()<1){
			return false;
		}
		for(int i=0;i<s.length();i++){
			if(isAlpha(s.charAt(i))){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}

	private final static boolean contain(char[] arr, char c){
		for(char ch : arr){
			if(ch==c){
				return true;
			}
		}
		return false;
	}

	private final static Pattern CN_MOBILE_PATTERN = Pattern.compile("^1[34578]{1}[\\d]{9}$");
	/**
	 * 是否是中国移动手机号码(1[3458])11位手机号码
	 * */
	public static boolean isCNMobile(String s){
		return s==null||s.length()!=11 ? false : CN_MOBILE_PATTERN.matcher(s).matches();
	}

	private final static Pattern CN_TEL_PATTERN = Pattern.compile("^(0[1-9]{1}[\\d]{1,2}\\-)?[2-9]{1}[\\d]{6,7}(\\-[\\d]{1,})?$");
	/**
	 * 是否是中国国内电话号码
	 * */
	public final static boolean isCNTel(String tel){
		return tel==null||tel.length()<7 ? false : CN_TEL_PATTERN.matcher(tel).matches();
	}

	private final static Pattern IP_V4_PATTERN = Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|22[0-3])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");
	/**
	 * 是否是ip v4 地址
	 * */
	public final static boolean isIpV4(String ip){
		return ip==null||ip.length()<7 ? false : IP_V4_PATTERN.matcher(ip).matches();
	}
	
	public static String join(Object[] o, String flag) {
		StringBuffer bf = new StringBuffer();
		for(int i = 0, len = o.length; i < len; i++) {
			bf.append(String.valueOf(o[i]));
			if(i < len - 1) bf.append(flag);
		}
		return bf.toString();
	}

}
