package cn.qdevelop.plugin.idgenerate.bean;

import cn.qdevelop.plugin.common.IDRequest;

public class IDRequestBean implements IDRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3901539413081531861L;
	
	/**
	 * 请求操作类型
	 * 0: 获取ID数据值
	 * 1: 客户端数据还回操作
	 * 
	 **/
	public int  oper = 0;
	public String name;
	public int digit;
	public int buffer;
	public String dateFomatter;
	public boolean isRandom,isDateRange;
	public Long[] rollVals;
	
	public IDRequestBean(){
		
	}
	
	public IDRequestBean(String name,int digit,int buffer){
		this.oper = 0 ;
		this.name = name;
		this.digit = digit;
		this.buffer = buffer;
	}
	
	public IDRequestBean(String name,int digit){
		this.oper = 0 ;
		this.name = name;
		this.digit = digit;
		this.buffer = 2;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	public int getBuffer() {
		return buffer;
	}
	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public int getOper() {
		return oper;
	}

	public void setOper(int oper) {
		this.oper = oper;
	}

	public Long[] getRollVals() {
		return rollVals;
	}

	public void setRollVals(Long[] rollVals) {
		this.rollVals = rollVals;
	}
	
	public void setRandom(boolean isRandom){
		this.isRandom = isRandom;
	}
	@Override
	public boolean isRandom() {
		return isRandom;
	}

	@Override
	public Long selfControlSeed(Long currentVal) {
		return null;
	}

	@Override
	public boolean isDateRange() {
		return isDateRange;
	}

	@Override
	public String getDateFormatter() {
		return dateFomatter;
	}
	
	public void setDateRange(boolean isDateRange){
		this.isDateRange = isDateRange;
	}
}
