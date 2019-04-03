package cn.qdevelop.core.utils;

import cn.qdevelop.common.files.QSource;
import cn.qdevelop.core.db.config.SQLConfigLoader;

public class TestLoadAll {
	public static void main(String[] args) {
		QSource.setProjectPath("/Users/janson/zmt_svn/online/zimeihui/");
		SQLConfigLoader.getInstance();
	}
}
