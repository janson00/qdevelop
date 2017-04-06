/**
 * 
 */
package cn.qdevelop.common.event.asynrequestwithdb;

import java.util.List;

/**
 * @author square
 *
 */
public class DBRequestQueue implements IRequestQueue {

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#storeRequest(com.wangjiu.common.event.AsynRunBean)
	 */
	@Override
	public int storeRequest(AsynRunBean args) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#requestComplete(int)
	 */
	@Override
	public int requestComplete(int rqid) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#isCanRunning(int)
	 */
	@Override
	public int isCanRunning(int rqid) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#requestError(int)
	 */
	@Override
	public int requestError(int rqid) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#resetRequest(int)
	 */
	@Override
	public int resetRequest(int rqid) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#requestTimeout(int)
	 */
	@Override
	public int requestTimeout(int rqid) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.wangjiu.common.event.RequestQueue#loadNonComplete(java.lang.String)
	 */
	@Override
	public List<AsynRunBean> loadNonComplete(String sysName) {
		// TODO Auto-generated method stub
		return null;
	}

}
