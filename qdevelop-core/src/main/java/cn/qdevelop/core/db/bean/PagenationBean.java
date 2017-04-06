package cn.qdevelop.core.db.bean;

import cn.qdevelop.core.standard.IPagenation;

public class PagenationBean implements IPagenation{
	private int page,pageNum;
	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public int getPageNum() {
		return pageNum;
	}

	@Override
	public String getPagenationInfo() {
		if(page < 1){
			return "";
		}
		return new StringBuilder().append(" limit ")
				.append((page-1)*pageNum)
				.append(",")
				.append(pageNum)
				.toString();
	}

}
