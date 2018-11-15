package cn.qdevelop.app.id.server.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class IDTemplate {
	private static IDTemplate _IDTemplate = new IDTemplate();
	public static IDTemplate getInstance(){return _IDTemplate;}
	private ConcurrentHashMap<String,LinkedBlockingQueue<Long>> tempQueue = new ConcurrentHashMap<String,LinkedBlockingQueue<Long>>();

	public int get(String name,Long[] collects){
		LinkedBlockingQueue<Long> _q = tempQueue.get(name);
		if(_q==null){
			synchronized(tempQueue){
				_q = tempQueue.get(name);
				if(_q==null){
					_q = new LinkedBlockingQueue<Long>(1000);
					tempQueue.put(name, _q);
					return 0;
				}
			}
		}
		if(_q.size()==0)return 0;
		int idx = 0;
		while(!_q.isEmpty() && idx < collects.length){
			collects[idx++] = _q.poll();
		}
		return idx;
	}

	public void put(String name,String[] vals){
		LinkedBlockingQueue<Long> _q = tempQueue.get(name);
		if(_q==null){
			synchronized(tempQueue){
				_q = tempQueue.get(name);
				if(_q==null){
					_q = new LinkedBlockingQueue<Long>();
				}
			}
		}
		for(String v : vals){
			_q.offer(Long.parseLong(v));
		}
	}
	
	public void put(String name,Long[] vals){
		LinkedBlockingQueue<Long> _q = tempQueue.get(name);
		if(_q==null){
			synchronized(tempQueue){
				_q = tempQueue.get(name);
				if(_q==null){
					_q = new LinkedBlockingQueue<Long>();
				}
			}
		}
		for(Long v : vals){
			_q.offer(v);
		}
	}

}
