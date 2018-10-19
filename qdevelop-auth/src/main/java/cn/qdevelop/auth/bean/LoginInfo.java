package cn.qdevelop.auth.bean;

import java.io.Serializable;
import java.util.HashMap;

import cn.qdevelop.auth.utils.AuthUtils;

public class LoginInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3795881104365218794L;

	private String userName,ip,sysName,loginName,permitId;
	private long uid,lastLoginTimer = System.currentTimeMillis();
	private HashMap<String,String> extra;
	private HashMap<String,Object> menuPermit = new HashMap<String,Object>();

	
	
	public String getExtraInfo(String key) {
		return extra==null?null:extra.get(key);
	}


	public void setExtra(HashMap<String, String> extra) {
		this.extra = extra;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getSysName() {
		return sysName;
	}


	public long getUid() {
		return uid;
	}


	public void setUid(long uid) {
		this.uid = uid;
	}


	public void setSysName(String sysName) {
		this.sysName = sysName;
	}


	public String getLoginName() {
		return loginName;
	}


	public String getPermitId() {
		return permitId;
	}


	public void setPermitId(String permitId) {
		this.permitId = permitId;
	}


	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void addMenuPermit(String url,Long permit){
		String[] tmp = toPermitStyle(url);
		loopAddchild(tmp,menuPermit,0);
	}

	@SuppressWarnings("unchecked")
	private void loopAddchild(String[] urls,HashMap<String,Object> map,int idx){
		HashMap<String,Object> tmpMap = (HashMap<String, Object>) map.get(urls[idx]);
		if(tmpMap==null){
			tmpMap = new HashMap<String,Object>(1);
			map.put(urls[idx], tmpMap);
		}
		if(++idx < urls.length){
			loopAddchild(urls,tmpMap,idx);
		}
	}
	private String[] toPermitStyle(String uri){
		String[] tmp;
		if(uri.startsWith("http")){
			uri = AuthUtils.formatUriClean.matcher(AuthUtils.formatUri.matcher(uri).replaceAll("")).replaceAll("/");
			tmp = uri.substring(uri.indexOf("/")+1).split("/");
		}else{
			tmp = AuthUtils.formatUriClean.matcher(AuthUtils.formatUri.matcher(uri).replaceAll("")).replaceAll("/").split("/");
		}
		return tmp;
	}

	/**
	 * 根据访问URI给出是否具备权限
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("unused")
	public boolean hasUriPermit(String uri){
		if(menuPermit.get("*")!=null){//有全部匹配
			return true;
		}
		String[] tmp = toPermitStyle(uri);
		if(menuPermit.get(tmp[0])!=null){
			Boolean hasPermit = null ;
			return hasPerimit(tmp,menuPermit,0);
		}else if(menuPermit.get(tmp[1])!=null) {//兼容contentPath
			Boolean hasPermit = null ;
			return hasPerimit(tmp,menuPermit,1);
		}else{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean hasPerimit(String[] urls,HashMap<String,Object> map,int idx){
		HashMap<String,Object> tmpMap = (HashMap<String, Object>) map.get(urls[idx]);
		if(tmpMap==null){
			if(map.get("*") != null){
				return true;
			}else{
				return false;
			}
		}else{
			if(++idx >= urls.length){
				return true;
			}
			return hasPerimit(urls,tmpMap,idx);
		}
	}

	
	public boolean isNeedResetSession(){
		if(System.currentTimeMillis() - this.lastLoginTimer > 60000){
			this.lastLoginTimer = System.currentTimeMillis();
			return true;
		}
		return false;
	}


	public String toString(){
		return new StringBuffer().append(this.getLoginName())
				.append("|").append(this.getIp())
				.append("|").append(this.menuPermit.toString())
				.toString();
	}
}
