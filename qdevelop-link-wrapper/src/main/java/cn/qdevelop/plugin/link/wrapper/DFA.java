package cn.qdevelop.plugin.link.wrapper;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class DFA {
	
	/**
	 * 根节点
	 */
	private TreeNode rootNode = new TreeNode();



	/**
	 * 关键词编码
	 */
	public String charset = "UTF-8";

	/**
	 * 创建DFA
	 * @param keywordList
	 * @throws UnsupportedEncodingException 
	 */
	public void createKeywordTree(List<String> keywordList) throws UnsupportedEncodingException{
		for (String keyword : keywordList) {
			if(keyword == null)continue;
			addKeyWord(keyword);
		}//end for
	}

	public void addKeyWord(String ... keywords) throws UnsupportedEncodingException{
		if(keywords == null || keywords.length ==0) return;
		for(String keyword:keywords){
			keyword = keyword.trim();
			byte[] bytes = keyword.getBytes(charset);
			TreeNode tempNode = rootNode;
			//循环每个字节
			for (int i = 0; i < bytes.length; i++) {
				int index = bytes[i] & 0xff; //字符转换成数字
				TreeNode node = tempNode.getSubNode(index);
				if(node == null){ //没初始化
					node = new TreeNode();
					tempNode.setSubNode(index, node);
				}
				tempNode = node;
				if(i == bytes.length - 1){
					tempNode.setKeywordEnd(true);         //关键词结束， 设置结束标志
				}
			}//end for
		}
	}



	public String wrapKeyWord(String text,Wrap _d) throws UnsupportedEncodingException{
		byte[] bytes = text.getBytes(charset);
		if(bytes == null || bytes.length == 0){
			return null;
		}
		/**
		 * 关键词缓存
		 */
		ByteBuffer keywordBuffer = ByteBuffer.allocate(1024);  
		StringBuffer targetText = new StringBuffer();
		TreeNode tempNode = rootNode;
		int rollback = 0;        //回滚数
		int position = 0; //当前比较的位置
		int lastPosition=0;
		ByteBuffer buffer;
		while (position < bytes.length) {
			int index = bytes[position] & 0xFF;
			keywordBuffer.put(bytes[position]);        //写关键词缓存
			tempNode = tempNode.getSubNode(index);
			//当前位置的匹配结束
			if(tempNode == null){ 
				position = position - rollback; //回退 并测试下一个字节
				rollback = 0;
				tempNode = rootNode;          //状态机复位
				keywordBuffer.clear();        //清空
			} else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词
				keywordBuffer.flip();

				int poi = keywordBuffer.limit();
				buffer = ByteBuffer.allocate(position-poi-lastPosition+1);
				for(int i=lastPosition;i<=position-poi;i++){
					buffer.put(bytes[i]);
				}
				buffer.flip();
				targetText.append(getStr(buffer));
				buffer.clear();
				String keyword = getStr(keywordBuffer);
				targetText.append(_d.prefix(keyword)).append(_d.keyword(keyword)).append(_d.suffix(keyword));
				keywordBuffer.limit(keywordBuffer.capacity());
				lastPosition = position+1; //记录上一次的终结点
				rollback = 1;        //遇到结束点  rollback 置为1
			}else{        
				rollback++;        //非结束点 回退数加1
			}

			position++;
		}

		buffer = ByteBuffer.allocate(bytes.length-lastPosition);
		for(int i=lastPosition;i<bytes.length;i++){
			buffer.put(bytes[i]);
		}
		buffer.flip();
		buffer.clear();

		targetText.append(getStr(buffer));
		return targetText.toString();
	}

	/**
	 * 搜索关键字
	 * @throws UnsupportedEncodingException 
	 */
	public String searchKeyword(String text) throws UnsupportedEncodingException{
		byte[] bytes = text.getBytes(charset);
		/**
		 * 关键词缓存
		 */
		ByteBuffer keywordBuffer = ByteBuffer.allocate(1024);    
		StringBuilder words = new StringBuilder();

		if(bytes == null || bytes.length == 0){
			return words.toString();
		}

		TreeNode tempNode = rootNode;
		int rollback = 0;        //回滚数
		int position = 0; //当前比较的位置

		while (position < bytes.length) {
			int index = bytes[position] & 0xFF;
			keywordBuffer.put(bytes[position]);        //写关键词缓存
			tempNode = tempNode.getSubNode(index);
			//当前位置的匹配结束
			if(tempNode == null){ 
				position = position - rollback; //回退 并测试下一个字节
				rollback = 0;
				tempNode = rootNode;          //状态机复位
				keywordBuffer.clear();        //清空
			}
			else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词
				//				position = keywordBuffer.limit()+1;
				keywordBuffer.flip();
				String keyword = getStr(keywordBuffer);
				keywordBuffer.limit(keywordBuffer.capacity());
				if( words.length() == 0 ) words.append(keyword);
				else words.append("|").append(keyword);
				rollback = 1;        //遇到结束点  rollback 置为1
			}else{        
				rollback++;        //非结束点 回退数加1
			}
			position++;
		}
		return words.toString();
	}

	private String getStr(ByteBuffer bb){
		return  Charset.forName(charset).decode(bb).toString();
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
