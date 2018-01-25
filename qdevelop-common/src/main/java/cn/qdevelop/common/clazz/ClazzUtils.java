package cn.qdevelop.common.clazz;

public class ClazzUtils {
	
	/**
	 * 获取调用类的上级调用类
	 * @param currentClass
	 * @return
	 */
	public static Class<?> getCallClass(Class<?> currentClass){
		String current = currentClass.getName();
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int idx = 0;
		for(StackTraceElement t : trace){
//			System.out.println(">>>> "+t.getClassName()+"\t"+current);
			if(idx >2 && !t.getClassName().startsWith(current)){
				System.out.println("call "+current+" from "+trace[idx].getClassName());
				try {
					return Class.forName(trace[idx].getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			idx++;
		}
		return null;
	}

}
