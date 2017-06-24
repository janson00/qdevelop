package cn.qdevelop.core.formatter.impl;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractParamFormatter;

/**
 * 对参数数值进行加密存储
 * @author janson
 *
 */
public class EncryptNumberFormatter  extends AbstractParamFormatter{
	String paramKey;
	
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		paramKey = conf.attributeValue("param-key");
	}

	@Override
	public void init() {
		
	}

	@Override
	public Map<String, Object> formatter(Map<String, Object> query) {
		if(query.get(paramKey)==null)return query;
		String val = String.valueOf(query.get(paramKey));
		int len = val.length();
		if(isNumber.matcher(val).find() && len > 3){
			StringBuffer sb = new StringBuffer();
			sb.append("@");
			sb.append(encode(Long.parseLong(val.substring(0,len-1)),len-2));
			sb.append(val.substring(len-1));
			query.put(paramKey, sb);
		}
		return query;
	}
	
	private final static String[] crypt_dict = { "6207548913", "3147896520", "3026584197",
			"9452703186", "8402691735", "2149356807", "3026471895", "5042897163" };
	
	private final static Pattern isNumber = Pattern.compile("^[0-9]+(X|x)?$");
	
	private long encode(long num,int digit) {
		long head = 0;
		char[] val = String.valueOf(num).toCharArray();
		if(val.length > digit){
			StringBuffer sb = new StringBuffer();
			int idx = val.length - digit;
			for(int i=0;i<val.length;i++){
				if(i<idx){
					sb.append(val[i]);
				}else{
					sb.append(0);
				}
			}
			head = Long.parseLong(sb.toString());
		}
		
		int computerDigit = digit - 1;
		StringBuffer digitStringBuffer = new StringBuffer();
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		String number = new DecimalFormat(digitString).format(num-head);
		int shift = number.charAt(computerDigit) - '0';
		String encrypt_number = digitString;
		char[] s = encrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = crypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(number.charAt(i) - '0');
		}
		s[computerDigit] = number.charAt(computerDigit);
		encrypt_number = String.valueOf(s);
		return Long.parseLong(encrypt_number) + head;
	}

	@Override
	public String[] getncreaseKeys() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
