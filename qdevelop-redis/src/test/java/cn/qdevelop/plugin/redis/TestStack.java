package cn.qdevelop.plugin.redis;

public class TestStack {
	public Class<?> getCallClass(Class<?> currentClass){
		String current = currentClass.getName();
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int idx = 0;
		for(StackTraceElement t : trace){
			if(t.getClassName().equals(current)){
				++idx;
				break;
			}
		}
		try {
			return Class.forName(trace[idx+1].getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


}
