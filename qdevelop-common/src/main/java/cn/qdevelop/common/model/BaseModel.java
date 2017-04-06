/**   
 * @Title: BaseDao.java 
 * @Package com.square.common.dao 
 * @Description: 基础model类
 * @date 2013年11月6日 下午3:51:47 
 * @version V1.0  
 */
package cn.qdevelop.common.model;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @ClassName: BaseDao
 * @Description: TODO
 * @author squarezjz
 * @date 2013年11月6日 下午3:51:47
 */
public class BaseModel implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				sb.append(field.getName()).append("=").append(field.get(this)).append(",");
			} catch (Exception e) {
				System.out.println("BaseModel，toSTring方法报错");
				e.printStackTrace();
			}
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
