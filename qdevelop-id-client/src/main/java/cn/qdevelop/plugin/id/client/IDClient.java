package cn.qdevelop.plugin.id.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import cn.qdevelop.common.QLog;
import cn.qdevelop.common.files.QFileLoader;
import cn.qdevelop.plugin.common.IDRequest;
import cn.qdevelop.plugin.common.IDResponse;
import cn.qdevelop.plugin.idgenerate.bean.ClientBufferBean;
import cn.qdevelop.plugin.idgenerate.bean.IDRequestBean;

public class IDClient {

	protected static Logger log = QLog.getLogger(IDClient.class);

	private static IDClient _IDClient = new IDClient();
	private static String SERVER_IP;
	private static int SERVER_PORT;

	public IDClient(){
		init();
	}

	public static IDClient getInstance() {
		return _IDClient;
	}

	private ConcurrentHashMap<String, ArrayBlockingQueue<Long>> queue = new ConcurrentHashMap<String, ArrayBlockingQueue<Long>>();
	private ConcurrentHashMap<String, AtomicBoolean> queueRunning = new ConcurrentHashMap<String, AtomicBoolean>();
	private HashMap<String,ClientBufferBean> buffers = new HashMap<String,ClientBufferBean>();


	/**
	 * 获取用户ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getUserID() throws Exception {
		return getIDStr(new IDRequestBean("user",6,5));
	}

	/**
	 * 获取商品ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getProductID() throws Exception {
		return getIDStr(new IDRequestBean("product",6,5));
	}

	/**
	 * 获取随机验证码
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getRandomID() throws Exception {
		IDRequestBean req = new IDRequestBean("random",6,5);
		req.setRandom(true);
		return getIDStr(req);
	}

	/**
	 * 获取订单ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getOrderID() throws Exception {
		IDRequestBean req = new IDRequestBean("order",5,10);
		req.setDateRange(true);
		return getIDStr(req);
	}
	
	/**
	 * 获取支付ID
	 * @return
	 * @throws Exception
	 */
	public String getPaymentID() throws Exception {
		IDRequestBean req = new IDRequestBean("payment",5,5);
		req.setDateRange(true);
		return getIDStr(req);
	}

	/**
	 * 获取优惠券ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCouponID() throws Exception {
		return getIDStr(new IDRequestBean("coupon",8,10));
	}

	
	public String getIDStr(String name, final int digit, final int buffer) throws Exception{
		IDRequestBean rb = new IDRequestBean(name,digit,buffer);
		return getIDStr(rb);
	}


	/**
	 * 获取ID字符串
	 * @param name 设置需要获取队列的名称
	 * @param digit 设置此队列中自增ID的长度
	 * @param buffer 每次从server端获取的本地缓存的数量
	 * @return
	 * @throws Exception
	 */
	public String getIDStr(final IDRequest req) throws Exception{
		Long val = getID(req);
		StringBuffer sb = new StringBuffer();
		sb.append(val);
		int diff = req.getDigit() - sb.length();
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
	public Long getID(final IDRequest req) throws Exception {
		try {
			ArrayBlockingQueue<Long> _q = queue.get(req.getName());
			if (_q == null) {
				synchronized (queue) {
					_q = queue.get(req.getName());
					if (_q == null) {
						_q = new ArrayBlockingQueue<Long>(500, true);
						queue.put(req.getName(), _q);
						buffers.put(req.getName(),new ClientBufferBean(req.getBuffer()));
					}
				}
			}
			
			
			ClientBufferBean cbb = buffers.get(req.getName());
			double size = _q.size();
			double rangeLeft =  cbb.getBuffer()*0.5 < 3 ? 2 : cbb.getBuffer()*0.5;
			if(size < rangeLeft){
				synchronized (queueRunning) {
					AtomicBoolean _run = queueRunning.get(req.getName());
					if(_run==null){
						_run = new AtomicBoolean();
						queueRunning.put(req.getName(),_run);
					}
					
					//根据请求时间差，动态调整缓存，减少网络请求
					cbb.reSetBuffer(req);
					
					if (size < 2) {
						if(_run.get()){
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}finally{
								_run.set(false);
							}
						}
						sync(req);
					}else{
						if(!_run.get()){
							new Thread(){
								public void run(){
									sync(req);
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
	private void sync(IDRequest req) {
		AtomicBoolean _run = queueRunning.get(req.getName());
		if(_run.get())return;
		_run.set(true);
		ArrayBlockingQueue<Long> _q = queue.get(req.getName());
		long consumeTime = System.currentTimeMillis() ;
		IDResponse rv = socketSender(req);
		if(rv!=null && rv.getValues()!=null){
			for(Long v : rv.getValues()){
				if(v!=null && v>0){
					_q.offer(v);
				}
			}
			long s = System.currentTimeMillis() - consumeTime;
			if (s > 50) {
				System.out.println("队列服务名称："+req.getName()+";耗时："+s);
			}
		}
		_run.set(false);
	}

	private IDResponse socketSender(IDRequest req) {
		ObjectOutputStream out = null;
		ObjectInputStream ois=null;
		try {
			if(server == null || server.isClosed()){
				synchronized(this){
					if(server == null || server.isClosed()){
						server = new Socket(SERVER_IP, SERVER_PORT);
						server.setKeepAlive(true);
						System.out.println("[ID Generate Server] "+SERVER_IP+":"+SERVER_PORT);
					}
				}
			}
			out = new ObjectOutputStream(server.getOutputStream());
			ois = new ObjectInputStream(server.getInputStream());
			out.writeObject(req);
			out.flush();
			IDResponse v = (IDResponse)ois.readObject();
			return v;
		} catch (Exception e) {
			System.err.println("[ID Generate Server Err] "+SERVER_IP+":"+SERVER_PORT);
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
		Iterator<Entry<String, ArrayBlockingQueue<Long>>> itor = queue.entrySet().iterator();
		while(itor.hasNext()){
			Entry<String, ArrayBlockingQueue<Long>> item = itor.next();
			ArrayBlockingQueue<Long> _q = item.getValue();
			if(!_q.isEmpty()){
				IDRequestBean req = new IDRequestBean();
				req.setName(item.getKey());
				req.setOper(1);
				ArrayList<Long> vals = new ArrayList<Long>();
				while(!_q.isEmpty()){
					vals.add(_q.poll());
				}
				req.setRollVals(vals.toArray(new Long[]{}));
				IDResponse resp = socketSender(req);
				System.out.println("roll back num : "+resp.getMsg());
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
		try {
			new QFileLoader(){
				@Override
				public void despose(InputStream is) {
					try {
						Properties prop = new Properties();
						prop.load(is);
						SERVER_IP = prop.getProperty("server_ip") == null ? "127.0.0.1" : prop.getProperty("server_ip");
						SERVER_PORT = prop.getProperty("server_port") == null ? 65501 : Integer.parseInt(prop.getProperty("server_port"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.loadFile("plugin-config/qdevelop-id-client.properties", this.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				IDClient.getInstance().shutdown();
			}
		});
	}

	public static void main(String[] args) {
		try {
			for(int i=0;i<10000;i++){
				//				String id = IDClient.getInstance().getRandomID();
//				System.out.println(IDClient.getInstance().getRandomID());
				System.out.println(IDClient.getInstance().getCouponID());
				System.out.println(IDClient.getInstance().getProductID());
//				IDClient.getInstance().getOrderID();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
