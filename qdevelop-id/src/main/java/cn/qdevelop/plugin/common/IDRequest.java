package cn.qdevelop.plugin.common;

public interface IDRequest extends java.io.Serializable {
	
	public String getName();
	public int getDigit();
	public int getBuffer();
	
	/**
	 * 请求操作类型
	 * 0: 获取ID数据值
	 * 1: 客户端数据还回操作
	 * 
	 **/
	public int getOper();
	
	public Long[] getRollVals();
	/**
	 * 是否生成随机数，当达到最大值时从0开始
	 * @return
	 */
	public boolean isRandom();
	
	/**
	 * 是否按照日期轮寻生产ID
	 * ID最大值未用满时，继续沿用前天ID值，
	 * 当天用满后，ID自动往上增长
	 * @return
	 */
	public boolean isDateRange();
	
	/**
	 * 或者日期生成格式，default： yymmdd
	 * @return
	 */
	public String getDateFormatter();
	
	public void setBuffer(int buffer);
	
	/**
	 * 自己控制种子数值，慎用！！
	 * 不用时返回null 或者 0 即可
	 * @param currentVal
	 * @return
	 */
	public Long selfControlSeed(Long currentVal);
}
