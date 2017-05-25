package cn.qdevelop.plugin.link.wrapper;

public abstract class Wrap {
	
	/**
	 * 前缀
	 * @param keyword
	 * @return
	 */
	public abstract String prefix(String keyword);
	
	/**
	 * 当前关键字
	 * @param keyword
	 * @return
	 */
	public abstract String keyword(String keyword);
	
	/**
	 * 后缀
	 * @param keyword
	 * @return
	 */
	public abstract String suffix(String keyword);
}
