/**
 * 
 */
package cn.qdevelop.common.event.requestimpl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import cn.qdevelop.common.event.IRequest;

/**
 * @author square
 * 
 */
public abstract class AbstractRequest implements IRequest {

	private static final long serialVersionUID = 1L;
	protected int timeout = 5000;
	private int retryNum = 0;
	// 0标示无限次重试
	private int maxRetryNum = 0;

	public int getRetryNum() {
		return retryNum;
	}

	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRetryNum() {
		return maxRetryNum;
	}

	public void setMaxRetryNum(int maxRetryNum) {
		this.maxRetryNum = maxRetryNum;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		Class<?> localClass = getClass();
		while (localClass.getSuperclass() != null) {
			Field[] fields = localClass.getDeclaredFields();
			sb.append(localClass.getSimpleName()).append("={");
			for (Field field : fields) {
				if (field.getName().equals("serialVersionUID")) {
					continue;
				}
				try {
					field.setAccessible(true);
					sb.append(field.getName()).append("=");
					Object value = field.get(this);
					sb.append(getValueString(value));
				} catch (Exception e) {
					System.out.println("AbstractRequest，toString方法报错");
					e.printStackTrace();
				}
			}
			sb.append("},");
			localClass = localClass.getSuperclass();
		}
		sb.append("}");
		return sb.toString();
	}

	private String getValueString(Object value) {
		if (value == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		if (value.getClass().isArray() == true) {
			int fieldLength = Array.getLength(value);
			sb.append("[");
			for (int i = 0; i < fieldLength; i++) {
				sb.append(getValueString(Array.get(value, i)));
			}
			sb.append("],");
		} else {
			sb.append(value).append(",");
		}
		return sb.toString();
	}
}
