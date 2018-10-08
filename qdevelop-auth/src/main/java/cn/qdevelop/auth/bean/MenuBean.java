package cn.qdevelop.auth.bean;

public class MenuBean {
	String name;
	String url;
	String icon;
	MenuBean[] childs;
	public MenuBean(){

	}
	public MenuBean(String name,String url,String icon){
		this.name = name;
		this.url = url;
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public MenuBean[] getChilds() {
		return childs;
	}
	public void setChilds(MenuBean[] childs) {
		this.childs = childs;
	}

}
