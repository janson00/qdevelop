package cn.qdevelop.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QLogBean {
	int type;
	String[] infos;
	Exception e;
	String date;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public QLogBean(int type,String[] infos){
		this.type = type;
		this.infos = infos;
		date = sdf.format(new Date());
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		if(infos==null)return "";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		switch(type){
		case 1: sb.append("INFO");break;
		case 2: sb.append("WARN");break;
		case 3: sb.append("ERROR");break;
		}
		sb.append("] ").append(date).append(" >");
		for(int i=0;i<infos.length;i++){
			sb.append(" ").append(infos[i]);
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
