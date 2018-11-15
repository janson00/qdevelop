package cn.qdevelop.app.id.server.impl;

import java.util.concurrent.atomic.AtomicLong;

import cn.qdevelop.app.id.server.IDRule;

public class RandomRule implements IDRule{

	@Override
	public AtomicLong rule(String keyName, AtomicLong currentStartNumber, int maxNum) {
		
		return null;
	}
}