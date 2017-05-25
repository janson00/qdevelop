package cn.qdevelop.plugin.link.wrapper;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private static final int NODE_LEN = 256;

	/**
	 * true 关键词的终结 ； false 继续
	 */
	private boolean end = false; 

	private List<TreeNode> subNodes = new ArrayList<TreeNode>(NODE_LEN);

	public TreeNode(){
		for (int i = 0; i < NODE_LEN; i++) {
			subNodes.add(i, null);
		}
	}

	/**
	 * 向指定位置添加节点树
	 * @param index
	 * @param node
	 */
	public void setSubNode(int index, TreeNode node){
		subNodes.set(index, node);
	}

	public TreeNode getSubNode(int index){
		return subNodes.get(index);
	}


	public boolean isKeywordEnd() {
		return end;
	}

	public void setKeywordEnd(boolean end) {
		this.end = end;
	}
}
