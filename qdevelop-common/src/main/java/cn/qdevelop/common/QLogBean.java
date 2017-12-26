package cn.qdevelop.common;

public class QLogBean {
	int type;
	String[] infos;
	Exception e;
	
	public QLogBean(int type,String[] infos){
		this.type = type;
		this.infos = infos;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getInfos() {
		if(infos==null)return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<infos.length;i++){
			sb.append(infos[i]);
		}
		return sb.toString();
	}
	
	public void setInfos(String[] infos) {
		this.infos = infos;
	}
	public Exception getE() {
		return e;
	}
	public void setE(Exception e) {
		this.e = e;
	}
	
	
	
	

}
