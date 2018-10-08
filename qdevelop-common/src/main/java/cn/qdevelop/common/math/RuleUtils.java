package cn.qdevelop.common.math;

import java.util.HashMap;

public class RuleUtils {
	private static int MAX = 127;
	private static HashMap<String,Integer> rulesMap = new HashMap<String,Integer>(MAX);
	
	public static void addRule(String rule,Integer idx){
		rulesMap.put(rule, idx);
	}
	
	public static Long getRuleValue(String ... rules){
		Integer[] collect = new Integer[MAX];
		for(int i =0;i<rules.length;i++){
			Integer bit = rulesMap.get(rules[i]);
			if(bit!=null){
				collect[bit] = 1;
			}
		}
		StringBuilder r = new StringBuilder();
		for(int i=0;i<collect.length;i++){
			r.append(collect[i]==null?0:1);
		}
		return Long.parseLong(r.toString(), 2);
	}

	public static void main(String[] args) {
		System.out.println(Long.parseLong("111111111111111111111111111111111111111111111111111111111111111", 2));
		System.out.println(Long.parseLong("000000000000000000000000000000000000000000000000000000000000001", 2));
//		Long long1 = new Long(Long.MAX_VALUE);
		System.out.println(Long.toBinaryString(Long.MAX_VALUE));
//		System.out.println(Long.MAX_VALUE);
	}
	

}
