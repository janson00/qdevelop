/**
 * 
 */
package cn.qdevelop.common.event.asynrequestwithdb;

import cn.qdevelop.common.httpclient.impl.HttpClient;

/**
 * @author square
 * 
 */
public class HttpEventClient extends HttpClient {
//	private static final transient Logger log = LoggerFactory
//			.getLogger(HttpEventClient.class);
//
//	private AbstractHttpRequest irr;
//	private String sysName;
//
//	public HttpEventClient(String sysName, AbstractHttpRequest irr) {
//		this.irr = irr;
//		this.sysName = sysName;
//	}
//
//	public HttpEventClient(String sysName) {
//		this.sysName = sysName;
//		AbstractHttpRequest irr = new AbstractHttpRequest() {
//			/**
//			 * @Fields serialVersionUID : TODO
//			 */
//			private static final long serialVersionUID = -734685237416058726L;
//
//			@Override
//			public void successCallBack(Object info) {
//				System.out.println(((ServerInfo) info).getContent());
//
//			}
//		};
//		this.irr = irr;
//	}
//	public HttpEventClient() {
//		this.sysName = "default";
//		AbstractHttpRequest irr = new AbstractHttpRequest() {
//			/**
//			 * @Fields serialVersionUID : TODO
//			 */
//			private static final long serialVersionUID = -734685237416058726L;
//
//			@Override
//			public void successCallBack(Object info) {
//				System.out.println(((ServerInfo) info).getContent());
//
//			}
//		};
//		this.irr = irr;
//	}
//
//	@Override
//	public ServerInfo send(String urlString, String method, String parameters,
//			Map<String, String> propertys, int timeout) throws IOException {
//		irr.setMethod(method);
//		irr.setParameters(parameters);
//		irr.setPropertys((HashMap<String, String>) propertys);
//		irr.setUrlString(urlString);
//		AsynRunQueue asynRunQueue = new AsynRunQueue(sysName,
//				new DBRequestQueue());
//		ServerInfo info = new ServerInfo();
//		try {
//			asynRunQueue.addRequest(irr);
//		} catch (Exception e) {
//			log.error("请求放入异步队列失败", e);
//			throw new IOException(e);
//		}
//		info.setCode(0);
//		info.setMessage("success");
//		return info;
//	}
}
