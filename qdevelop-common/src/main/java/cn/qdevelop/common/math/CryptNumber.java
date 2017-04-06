package cn.qdevelop.common.math;

import java.text.DecimalFormat;

public class CryptNumber {
	private final static long baseNum = 2168715;

	private final static String[] crypt_dict = { "6207548913", "3147896520", "3026584197",
			"9452703186", "8402691735", "2149356807", "3026471895", "5042897163" };

	private final static String[] decrypt_dict = { "2819540367", "9180276345", "1720643958",
			"5736129480", "2638194705", "8104256973", "1620493578", "1739208645" };

	private long getBaseNum(int publishbId) {
		return (publishbId * baseNum) % 10000;
	}

	public int encryptTicketNo(int ticketNum) {
		return encryptTicketNo(ticketNum, 1);
	}

	public int decryptTicketNo(int ticketNum) {
		return decryptTicketNo(ticketNum, 1);
	}

	public int encryptTicketNo(int ticketNum, int publishbId) {
		return encryptTicketNo(ticketNum, publishbId, 9);
	}

	public int decryptTicketNo(int ticketNum, int publishbId) {
		return decryptTicketNo(ticketNum, publishbId, 9);
	}
	
	
	public int encryptTicketNo(int originalNum, int publishbId, int digit) {
		int computerDigit = digit - 1;
		StringBuffer digitStringBuffer = new StringBuffer(digit);
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		long base = getBaseNum(publishbId);
		originalNum = originalNum + new Long(base).intValue();
		String numString = new DecimalFormat(digitString).format(originalNum);
		int shift = numString.charAt(computerDigit) - '0';
		String encrypt_number = digitString;
		char[] s = encrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = crypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(numString.charAt(i) - '0');
		}
		s[computerDigit] = numString.charAt(computerDigit);
		encrypt_number = String.valueOf(s);
		return Integer.parseInt(encrypt_number);
	}
	
	public static void main(String[] args) {
		CryptNumber crypt  = new CryptNumber();
		System.out.println(crypt.encryptTicketNo(123451));
	}

	public int decryptTicketNo(int cryptNum, int publishbId, int digit) {
		//int diff = 0;
//		if (ticketNum > 1000000000) {
//			diff = 1000000000 * (int) (ticketNum / 1000000000) - 100000000;
//			ticketNum = ticketNum - diff - 100000000;
//		}
		int computerDigit = digit - 1;
		StringBuffer digitStringBuffer = new StringBuffer(digit);
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		String ticketNo = new DecimalFormat(digitString.toString()).format(cryptNum);
		int shift = ticketNo.charAt(computerDigit) - '0';
		String decrypt_number = digitString;
		char[] s = decrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = decrypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(ticketNo.charAt(i) - '0');
		}
		s[computerDigit] = ticketNo.charAt(computerDigit);
		decrypt_number = String.valueOf(s);
		decrypt_number = String
				.valueOf((Integer.parseInt(decrypt_number) - getBaseNum(publishbId)));
		return Integer.parseInt(decrypt_number);
	}
}
