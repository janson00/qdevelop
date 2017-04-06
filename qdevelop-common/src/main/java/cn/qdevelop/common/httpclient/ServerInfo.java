package cn.qdevelop.common.httpclient;

import java.util.Vector;

/**
 * 响应对象
 */
public class ServerInfo {

	private String urlString;

	private int defaultPort;

	private String file;

	private String host;

	private String path;

	private int port;

	private String protocol;

	private String query;

	private String ref;

	private String userInfo;

	private String contentEncoding;

	private String content;

	private String contentType;
	/**
	 * reponseCode
	 */
	private int code;

	private String message;

	private String method;

	private int connectTimeout;

	private int readTimeout;

	private Vector<String> contentCollection;

	public String getContent() {
		return content;
	}

	public String getContentType() {
		return contentType;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Vector<String> getContentCollection() {
		return contentCollection;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public String getMethod() {
		return method;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public String getUrlString() {
		return urlString;
	}

	public int getDefaultPort() {
		return defaultPort;
	}

	public String getFile() {
		return file;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getQuery() {
		return query;
	}

	public String getRef() {
		return ref;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public void setDefaultPort(int defaultPort) {
		this.defaultPort = defaultPort;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setContentCollection(Vector<String> contentCollection) {
		this.contentCollection = contentCollection;
	}
	
	public String toString(){
		return content;
	}
}