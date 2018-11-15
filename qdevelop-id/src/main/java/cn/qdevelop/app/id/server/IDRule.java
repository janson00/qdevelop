package cn.qdevelop.app.id.server;

import java.util.concurrent.atomic.AtomicLong;

public interface IDRule {
  /**
   * 通过规则转换当前自增id序列值
   * @param currentStartNumber
   * @param digit
   * @return
   */
	public AtomicLong rule(String keyName,AtomicLong currentStartNumber,int maxNum);
}
