package cn.qdevelop.core.formatter.impl;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Element;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.formatter.AbstractResultFormatter;
import cn.qdevelop.core.standard.IDBResult;

/**
 * 对加密后的数值进行解密显示
 * @author janson
 *
 */
public class DecryptNumberFormatter extends AbstractResultFormatter{
	String resultKey;
	@Override
	public void initFormatter(Element conf) throws QDevelopException {
		resultKey = conf.attributeValue("result-key");
	}

	@Override
	public boolean isQBQuery() {
		return true;
	}

	@Override
	public void formatter(Map<String, Object> data) {
		if(data.get(resultKey)==null)return;
		String v = String.valueOf(data.get(resultKey));
		int len = v.length();
		if(isEncryptNumber.matcher(v).find() && len>4){
			StringBuffer sb = new StringBuffer();
			sb.append(decode(Long.parseLong(v.substring(1,len-1)),len-3));
			sb.append(v.substring(len-1));
			data.put(resultKey, sb.toString());
		}
	}

	@Override
	public void flush(IDBResult result) throws QDevelopException {
		
	}
	
	private final static String[] decrypt_dict = { "2819540367", "9180276345", "1720643958",
			"5736129480", "2638194705", "8104256973", "1620493578", "1739208645" };
	private final static Pattern isEncryptNumber = Pattern.compile("^@[0-9]+(X|x)?$");

	public long decode(long cryptNum , int digit) {
		long head = 0;
		char[] val = String.valueOf(cryptNum).toCharArray();
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
		StringBuffer digitStringBuffer = new StringBuffer(digit);
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		String ticketNo = new DecimalFormat(digitString.toString()).format(cryptNum-head);
		int shift = ticketNo.charAt(computerDigit) - '0';
		String decrypt_number = digitString;
		char[] s = decrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = decrypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(ticketNo.charAt(i) - '0');
		}
		s[computerDigit] = ticketNo.charAt(computerDigit);
		decrypt_number = String.valueOf(s);
		return Long.parseLong(decrypt_number) + head;
	}
	
}
