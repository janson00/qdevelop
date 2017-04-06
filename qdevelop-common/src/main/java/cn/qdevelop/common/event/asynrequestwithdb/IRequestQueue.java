package cn.qdevelop.common.event.asynrequestwithdb;

import java.util.List;

public interface IRequestQueue {
	
	public int storeRequest(AsynRunBean args);
	
	public int requestComplete(int rqid);
	
	public int isCanRunning(int rqid);
	
	public int requestError(int rqid);
	
	public int resetRequest(int rqid);
	
	public int requestTimeout(int rqid);
	
	public List<AsynRunBean> loadNonComplete(String sysName);
	
}
