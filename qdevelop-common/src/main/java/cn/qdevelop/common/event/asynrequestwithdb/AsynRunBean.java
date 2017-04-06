package cn.qdevelop.common.event.asynrequestwithdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import cn.qdevelop.common.event.IRequest;

public class AsynRunBean implements Serializable {

	public AsynRunBean() {
	}

	public AsynRunBean(Serializable rpcRequest) throws IOException {
		this.className = rpcRequest.getClass().getName();
		this.setSerializable(rpcRequest);
	}

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 540563585491122759L;

	int rqid, status, retryTime;
	String className, sysName;
	Date createTime, loadTime;
	byte[] serializable;

	public int getRqid() {
		return rqid;
	}

	public void setRqid(int rqid) {
		this.rqid = rqid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(int retryTime) {
		this.retryTime = retryTime;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public byte[] getSerializable() {
		return serializable;
	}

	public void setSerializable(byte[] serializable) {
		this.serializable = serializable;
	}

	public Date getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(Date loadTime) {
		this.loadTime = loadTime;
	}

	public void setSerializable(Serializable claZZ) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(claZZ);
			this.serializable = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public IRequest getRpcRequest() {
		ByteArrayInputStream bis = new ByteArrayInputStream(serializable);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			return (IRequest) o;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
