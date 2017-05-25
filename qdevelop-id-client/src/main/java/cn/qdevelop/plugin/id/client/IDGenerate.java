package cn.qdevelop.plugin.id.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.qdevelop.common.utils.QSource;

public class IDGenerate {
	private static IDGenerate _IDClient = new IDGenerate();
	private static String SERVER_IP;
	private static int SERVER_PORT;

	public IDGenerate(){
		init();
	}

	public static IDGenerate getInstance() {
		return _IDClient;
	}

	//  private long lastTimer=0;
	//  private int continuousHit = 1;

	private ConcurrentHashMap<String, ArrayBlockingQueue<Long>> queue = new ConcurrentHashMap<String, ArrayBlockingQueue<Long>>();
	private ConcurrentHashMap<String, AtomicBoolean> queueRunning = new ConcurrentHashMap<String, AtomicBoolean>();

	private HashMap<String,Integer> buffers = new HashMap<String,Integer>();
//		private ReadWriteLock lock = new ReentrantReadWriteLock();
//	private static Lock locker = new ReentrantLock();
//	private AtomicBoolean isRunning = new AtomicBoolean();


	/**
	 * 获取用户ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getUserID() throws Exception {
		return getID("user", 8, 2);
	}

	/**
	 * 获取商品ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getProductID() throws Exception {
		return getID("product", 6, 2);
	}

	/**
	 * 获取随机验证码
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getRandomID() throws Exception {
		return getID("random", 6, 5);
	}

	/**
	 * 获取订单ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getOrderID() throws Exception {
		return getID("order", 8, 2);
	}

	/**
	 * 获取优惠券ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getCouponID() throws Exception {
		return getID("coupon", 9, 2);
	}



	/**
	 * 获取ID字符串
	 * @param name 设置需要获取队列的名称
	 * @param digit 设置此队列中自增ID的长度
	 * @param buffer 每次从server端获取的本地缓存的数量
	 * @return
	 * @throws Exception
	 */
	public String getIDStr(final String name, final int digit, final int buffer) throws Exception{
		Long val = getID(name,digit,buffer);
		StringBuffer sb = new StringBuffer();
		sb.append(val);
		int diff = digit - sb.length();
		if(diff>0){
			for(int i=0;i<diff;i++){
				sb.insert(0, "0");
			}
		}
		return sb.toString();
	}

	/**
	 * 获取ID数值
	 * @param name 设置需要获取队列的名称
	 * @param digit 设置此队列中自增ID的长度
	 * @param buffer 每次从server端获取的本地缓存的数量
	 * @return
	 * @throws Exception
	 */
	public Long getID(final String name, final int digit, final int buffer) throws Exception {
		try {
			ArrayBlockingQueue<Long> _q = queue.get(name);
			if (_q == null) {
				synchronized (queue) {
					_q = queue.get(name);
					if (_q == null) {
						_q = new ArrayBlockingQueue<Long>(500, true);
						queue.put(name, _q);
						buffers.put(name, buffer);
					}
				}
			}
			double size = _q.size();
			double rangeLeft =  buffer*0.5 < 3 ? 2 : buffer*0.5;
			if(size < rangeLeft){
				synchronized (queueRunning) {
					AtomicBoolean _run = queueRunning.get(name);
					if(_run==null){
						_run = new AtomicBoolean();
						queueRunning.put(name,_run);
					}
					if (size < 2) {
						if(_run.get()){
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}finally{
								_run.set(false);
							}
						}
						sync(name, digit, buffer);
					}else{
						if(!_run.get()){
							new Thread(){
								public void run(){
									sync(name, digit, buffer);
								}
							}.start();
						}
					}
				}
			}
			Long v = _q.poll(500,TimeUnit.MILLISECONDS);
			if(v==null)throw new Exception("ID生成器获取不到新数据，请检查id-generate-svr.properties配置是否正确");
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	Socket server = null;
	private void sync(String name, final int digit, final int buffer) {
		AtomicBoolean _run = queueRunning.get(name);
		if(_run.get())return;
		_run.set(true);
		ArrayBlockingQueue<Long> _q = queue.get(name);
		long startTime = System.currentTimeMillis();
		Object rv = socketSender(new StringBuilder().append(name).append(":").append(digit).append(":")
				.append(buffer).toString());
		if(rv ==null || rv.getClass().equals(String.class)){return;}
		Long[] vals = (Long[])rv; 
		if (vals != null && vals.length > 0) {
			for (Long v : vals) {
				if (v > 0) {
					_q.offer(v);
				}
			}
		}
		long consumeTime = System.currentTimeMillis() - startTime;
		if (consumeTime > 50) {
			System.out.println("队列服务名称："+name+";耗时："+consumeTime);
		}
		_run.set(false);
	}

	private Object socketSender(String cmd) {
		PrintWriter out = null;
		ObjectInputStream ois=null;
		try {
			if(server == null || server.isClosed()){
				synchronized(this){
					if(server == null || server.isClosed()){
						server = new Socket(SERVER_IP, SERVER_PORT);
					}
				}
			}
			out = new PrintWriter(server.getOutputStream());
			ois = new ObjectInputStream(server.getInputStream());
			out.println(cmd);
			out.flush();
			Object v = ois.readObject();
			return v;
		} catch (Exception e) {
			System.out.println("[ID Generate Server Err] "+SERVER_IP+":"+SERVER_PORT);
			e.printStackTrace();
		}finally{
			try {
				if(ois!=null)ois.close();
				if(out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void rollback(){
		StringBuilder sb;
		Iterator<Entry<String, ArrayBlockingQueue<Long>>> itor = queue.entrySet().iterator();
		while(itor.hasNext()){
			Entry<String, ArrayBlockingQueue<Long>> item = itor.next();
			ArrayBlockingQueue<Long> _q = item.getValue();
			System.out.println("rollback:["+item.getKey()+"]"+_q.size());
			if(!_q.isEmpty()){
				sb = new StringBuilder();
				sb.append(item.getKey()).append("@");
				while(!_q.isEmpty()){
					sb.append(_q.poll()).append(",");
				}
				socketSender(sb.substring(0,sb.length()-1).toString());
			}
		}
	}

	private final static String[] decrypt_dict = { "2819540367", "9180276345", "1720643958",
			"5736129480", "2638194705", "8104256973", "1620493578", "1739208645" };
	/**
	 * 用户反解数值
	 * @param cryptNum
	 * @param digit
	 * @return
	 */
	public long decodeNumber(long cryptNum , int digit) {
		long head = 0;
		char[] val = String.valueOf(cryptNum).toCharArray();
		if(val.length > digit){
			StringBuffer sb = new StringBuffer();
			int idx = val.length - digit;
			for(int i=0;i<val.length;i++){
				if(i<idx){
					sb.append(val[i]);
				}else{
					sb.append(0);
				}
			}
			head = Long.parseLong(sb.toString());
		}

		int computerDigit = digit - 1;
		StringBuffer digitStringBuffer = new StringBuffer(digit);
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		String ticketNo = new DecimalFormat(digitString.toString()).format(cryptNum-head);
		int shift = ticketNo.charAt(computerDigit) - '0';
		String decrypt_number = digitString;
		char[] s = decrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = decrypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(ticketNo.charAt(i) - '0');
		}
		s[computerDigit] = ticketNo.charAt(computerDigit);
		decrypt_number = String.valueOf(s);
		return Long.parseLong(decrypt_number) + head;
	}



	public void shutdown(){
		if(server==null)return;
		synchronized(this){
			rollback();
			if(!server.isClosed()){
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void init(){
		Properties prop = QSource.getInstance().loadProperties("plugin-config/qdevelop-id-client.properties");
		if(prop!=null){
			SERVER_IP = prop.getProperty("server_ip") == null ? "127.0.0.1" : prop.getProperty("server_ip");
			SERVER_PORT = prop.getProperty("server_port") == null ? 10701 : Integer.parseInt(prop.getProperty("server_port"));
		}

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				IDGenerate.getInstance().shutdown();
			}
		});
	}
	public static void main(String[] args) {
		try {
			long id = IDGenerate.getInstance().getRandomID();
			System.out.println(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
