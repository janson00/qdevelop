package cn.qdevelop.common.httpclient.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

import com.google.common.base.Charsets;

import cn.qdevelop.common.httpclient.IHttpClient;
import cn.qdevelop.common.httpclient.ServerInfo;

/**
 * HTTP请求对象
 * 
 * @author
 */
public class HttpClient implements IHttpClient {
	private static IHttpClient instance = new HttpClient();

	public static IHttpClient getInstance() {
		return instance;
	}

	private String defaultContentEncoding = Charsets.UTF_8.toString();

	public HttpClient() {
		// this.defaultContentEncoding = Charset.defaultCharset().name();
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String)
	 */
	public ServerInfo sendGet(String urlString) throws IOException {
		return this.send(urlString, "GET", null, null, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String,
	 * int)
	 */
	public ServerInfo sendGet(String urlString, int timeout) throws IOException {
		return this.send(urlString, "GET", null, null, timeout);
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String,
	 * java.util.Map)
	 */
	public ServerInfo sendGet(String urlString, Map<String, String> params)
			throws IOException {
		return this.sendGet(urlString, params, null, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String,
	 * java.util.Map, int)
	 */
	public ServerInfo sendGet(String urlString, Map<String, String> params,
			int timeout) throws IOException {
		return this.sendGet(urlString, params, null, timeout);
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param propertys
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String,
	 * java.util.Map, java.util.Map)
	 */
	public ServerInfo sendGet(String urlString, Map<String, String> params,
			Map<String, String> propertys) throws IOException {
		return this.sendGet(urlString, params, propertys, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendGet</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param propertys
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendGet(java.lang.String,
	 * java.util.Map, java.util.Map, int)
	 */
	public ServerInfo sendGet(String urlString, Map<String, String> params,
			Map<String, String> propertys, int timeout) throws IOException {
		return this.send(urlString, "GET", getGETQueryString(params),
				propertys, timeout);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * )
	 */
	public ServerInfo sendPost(String urlString) throws IOException {
		return this.sendPost(urlString, null, null, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , int)
	 */
	public ServerInfo sendPost(String urlString, int timeout)
			throws IOException {
		return this.send(urlString, "POST", null, null, timeout);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , java.util.Map)
	 */
	public ServerInfo sendPost(String urlString, Map<String, String> params)
			throws IOException {
		return this.sendPost(urlString, params, null, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , java.util.Map)
	 */
	public ServerInfo sendPost(String urlString, String params)
			throws IOException {
		return this.send(urlString, "POST", params, null, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , java.util.Map, int)
	 */
	public ServerInfo sendPost(String urlString, Map<String, String> params,
			int timeout) throws IOException {
		return this.sendPost(urlString, params, null, timeout);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param propertys
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , java.util.Map, java.util.Map)
	 */
	public ServerInfo sendPost(String urlString, Map<String, String> params,
			Map<String, String> propertys) throws IOException {
		return this.sendPost(urlString, params, propertys, 0);
	}

	/*
	 * (非 Javadoc) <p>Title: sendPost</p> <p>Description: </p>
	 * 
	 * @param urlString
	 * 
	 * @param params
	 * 
	 * @param propertys
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @see
	 * com.youmei.common.rpcClient.httputil.HttpClient#sendPost(java.lang.String
	 * , java.util.Map, java.util.Map, int)
	 */
	public ServerInfo sendPost(String urlString, Map<String, String> params,
			Map<String, String> propertys, int timeout) throws IOException {
		return this.send(urlString, "POST", getPOSTQueryString(params),
				propertys, timeout);
	}

	/**
	 * 发HTTP请求
	 * 
	 * @param urlString
	 * @return 响映对象
	 * @throws IOException
	 */
	public ServerInfo send(String urlString, String method, String parameters,
			Map<String, String> propertys, int timeout) throws IOException {
		HttpURLConnection urlConnection = null;
		if (method.equalsIgnoreCase("GET") && parameters != null) {
			urlString += parameters;
		}
		URL url = new URL(urlString);
		urlConnection = (HttpURLConnection) url.openConnection();
		// String cookies = "session_cookie=value";
		// urlConnection.setRequestProperty("Cookie", cookies);
		urlConnection.setRequestMethod(method);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		// 设置超时时间
		urlConnection.setConnectTimeout(timeout);
		urlConnection.setReadTimeout(timeout);

		if (propertys != null)
			for (String key : propertys.keySet()) {
				urlConnection.addRequestProperty(key, propertys.get(key));
			}

		if (method.equalsIgnoreCase("POST") && parameters != null) {

			urlConnection.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			OutputStreamWriter outer = new OutputStreamWriter(
					urlConnection.getOutputStream(), "utf-8");
			outer.write(parameters);
			outer.flush();
			outer.close();
		}

		return this.makeContent(urlString, urlConnection);
	}

	/**
	 * @Description: TODO
	 * @Title: getPOSTQueryString
	 * @param @param parameters
	 * @param @return
	 * @return StringBuffer
	 * @throws
	 * @author
	 * @date 2013年12月27日 下午1:51:53
	 */
	public String getPOSTQueryString(Map<String, String> parameters) {
		StringBuffer param = new StringBuffer();
		for (String key : parameters.keySet()) {
			param.append("&");
			param.append(key).append("=")
					.append(String.valueOf(parameters.get(key)));
		}
		return param.toString();
	}

	/**
	 * @Description: TODO
	 * @Title: getGETQueryString
	 * @param @param parameters
	 * @param @return
	 * @return StringBuffer
	 * @throws
	 * @author
	 * @date 2013年12月27日 下午12:24:18
	 */
	public String getGETQueryString(Map<String, String> parameters) {
		StringBuffer param = new StringBuffer();
		int i = 0;
		for (String key : parameters.keySet()) {
			if (i == 0)
				param.append("?");
			else
				param.append("&");
			param.append(key).append("=").append(parameters.get(key));
			i++;
		}
		return param.toString();
	}

	/**
	 * 得到响应对象
	 * 
	 * @param urlConnection
	 * @return 响应对象
	 * @throws IOException
	 */
	private ServerInfo makeContent(String urlString,
			HttpURLConnection urlConnection) throws IOException {
		ServerInfo httpResponser = new ServerInfo();
		try {
			String ecod = urlConnection.getContentEncoding();
			if (ecod == null) {
				ecod = this.defaultContentEncoding;
			}

			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, ecod));

			httpResponser.setContentCollection(new Vector<String>());
			StringBuffer temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				httpResponser.getContentCollection().add(line);
				temp.append(line).append("\r\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

			httpResponser.setUrlString(urlString);

			httpResponser.setDefaultPort(urlConnection.getURL()
					.getDefaultPort());
			httpResponser.setFile(urlConnection.getURL().getFile());
			httpResponser.setHost(urlConnection.getURL().getHost());
			httpResponser.setPath(urlConnection.getURL().getPath());
			httpResponser.setPort(urlConnection.getURL().getPort());
			httpResponser.setProtocol(urlConnection.getURL().getProtocol());
			httpResponser.setQuery(urlConnection.getURL().getQuery());
			httpResponser.setRef(urlConnection.getURL().getRef());
			httpResponser.setUserInfo(urlConnection.getURL().getUserInfo());

			httpResponser.setContent(temp.toString());
			httpResponser.setContentEncoding(ecod);
			httpResponser.setCode(urlConnection.getResponseCode());
			httpResponser.setMessage(urlConnection.getResponseMessage());
			httpResponser.setContentType(urlConnection.getContentType());
			httpResponser.setMethod(urlConnection.getRequestMethod());
			httpResponser.setConnectTimeout(urlConnection.getConnectTimeout());
			httpResponser.setReadTimeout(urlConnection.getReadTimeout());

			return httpResponser;
		} catch (IOException e) {
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}

	/**
	 * 默认的响应字符集
	 */
	public String getDefaultContentEncoding() {
		return this.defaultContentEncoding;
	}

	/**
	 * 设置默认的响应字符集
	 */
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	}
}
