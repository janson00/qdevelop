package cn.qdevelop.core;

import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.bean.DBResultBean;
import cn.qdevelop.core.db.bean.PagenationBean;

public class MapToBeanTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		PagenationBean pb = new PagenationBean();
		DBResultBean rb = new DBResultBean();
		rb.add(new DBArgs().put("page", "2").put("page_num", "10"));
		PagenationBean pb = (PagenationBean) rb.toJavaBean(0, new PagenationBean());
//		System.out.println(rb.getResult(0));
		System.out.println(pb.getPagenationInfo());
	}

}
