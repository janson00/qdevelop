package cn.qdevelop.common.httpclient;

import java.io.IOException;
import java.util.Map;

public interface IHttpClient {
	/**
	 * 发送http请求
	 * @param urlString
	 * @param method
	 * @param parameters
	 * @param propertys
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public ServerInfo send(String urlString, String method, String parameters,
			Map<String, String> propertys, int timeout) throws IOException;

}