package cn.qdevelop.plugin.sensitiveword.press;

import cn.qdevelop.plugin.sensitiveword.impl.Wrap;

public class ReplaceSensitive extends Wrap {

	@Override
	public String prefix(String keyword) {
		return "";
	}

	@Override
	public String keyword(String keyword) {
		return "**";
	}

	@Override
	public String suffix(String keyword) {
		return "";
	}

}
