package cn.qdevelop.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.MethodUtils;

import cn.qdevelop.common.exception.QDevelopException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapUtil {
	// private static final transient Logger LOG =
	// LoggerFactory.getLogger(MapUtil.class);

	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @param bean
	 *            要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */

	public static Map parseMapFromBean(Object bean) throws IntrospectionException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();

		BeanInfo beanInfo = Introspector.getBeanInfo(type);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				if (readMethod == null)
					continue;
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

	/**
	 * 基于url请求转成请求使用的HashMap
	 * @param args
	 * @return
	 */
	public static Map<String,Object> parseMapFromStr(String args){
		if(args==null || args.length()==0)return new HashMap(0);
		String[] tmp = args.split("&");
		Map<String,Object> argMap = new HashMap(tmp.length);
		for(String t : tmp){
			argMap.put(t.substring(0,t.indexOf("=")),t.substring(t.indexOf("=")+1));
		}
		return argMap;
	}

	/**
	 * 转化List
	 * 
	 * @param beanList
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<Map> convertBeanList(List beanList) throws IllegalAccessException,
	IllegalArgumentException, InvocationTargetException, IntrospectionException {
		List<Map> rs = new ArrayList<Map>();
		for (Object object : beanList) {
			rs.add(parseMapFromBean(object));
		}
		return rs;
	}

	public static Map beanToMap(Object bean) throws IllegalAccessException,
	InvocationTargetException, NoSuchMethodException, QDevelopException {
		if (bean == null) {
			return (new java.util.HashMap());
		}
		Map description = new HashMap();
		PropertyDescriptor[] descriptors = BeanUtilsBean.getInstance().getPropertyUtils()
				.getPropertyDescriptors(bean);
		Class clazz = bean.getClass();
		for (int i = 0; i < descriptors.length; i++) {
			String name = descriptors[i].getName();
			if (MethodUtils.getAccessibleMethod(clazz, descriptors[i].getReadMethod()) != null) {
				Object values = BeanUtilsBean.getInstance().getPropertyUtils()
						.getNestedProperty(bean, name);
				if (values instanceof Collection) {
					description.put(name, beanToMapByList((Collection) values));
				} else {
					description.put(name, values);
				}
			}
		}
		return (description);

	}

	public static List<Map> beanToMapByList(Collection<Object> collection) throws QDevelopException,
	IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (collection == null) {
			return new ArrayList();
		}
		ArrayList list = new ArrayList(collection.size());
		for (Object object : collection) {
			list.add(beanToMap(object));
		}
		return list;
	}

	private static Pattern isNumber = Pattern.compile("^[0-9]+$");

	public static int getInt(Map<String, Object> map, String key) {
		if (map == null)
			return 0;
		Object val = map.get(key);
		if (val == null)
			return 0;
		if (val instanceof Integer)
			return (Integer) val;
		String t = String.valueOf(val);
		if (!isNumber.matcher(t).find())
			return 0;
		return Integer.parseInt(t);
	}

	public static Byte getByte(Map<String, Object> map, String key) {
		if (map == null)
			return 0;
		Object val = map.get(key);
		if (val == null)
			return 0;
		if (val instanceof Byte)
			return (Byte) val;
		String t = String.valueOf(val);
		if (!isNumber.matcher(t).find())
			return 0;
		return Byte.parseByte(t);
	}

	public static long getLong(Map<String, Object> map, String key) {
		if (map == null)
			return 0;
		Object val = map.get(key);
		if (val == null)
			return 0;
		if (val instanceof Long)
			return (Long) val;
		String t = String.valueOf(val);
		if (!isNumber.matcher(t).find())
			return 0;
		return Long.parseLong(t);
	}

	public static String getString(Map<String, Object> map, String key) {
		if (map == null)
			return null;
		Object val = map.get(key);
		if (val == null)
			return null;
		return String.valueOf(val);
	}

	public static Date getDate(Map<String, Object> map, String key) throws ParseException {
		if (map == null)
			return null;
		Object val = map.get(key);
		if (val == null)
			return null;
		if (val instanceof Date)
			return (Date) val;
		return DateUtil.parse(String.valueOf(val));
	}

	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * 
	 * @param type
	 *            要转化的类型
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	public static final <T> T convertMap(Class<T> type, Map map) throws IntrospectionException,
	IllegalAccessException, InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		T obj = type.newInstance(); // 创建 JavaBean 对象
		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (map.containsKey(propertyName)) {
				Object value = map.get(propertyName);
				Object[] args = new Object[1];
				args[0] = value;
				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}
}
