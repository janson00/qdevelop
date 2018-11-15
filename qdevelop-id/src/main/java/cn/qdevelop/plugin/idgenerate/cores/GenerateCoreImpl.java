package cn.qdevelop.plugin.idgenerate.cores;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import cn.qdevelop.plugin.common.IDRequest;

public class GenerateCoreImpl {
	/**
	 * 
	 */
	private static GenerateCoreImpl _IDGenerate = new GenerateCoreImpl();
	public static GenerateCoreImpl getInstance(){return _IDGenerate;}
	private Properties props;
	private File config;
	private final int step = 100; 
	private ConcurrentHashMap<String,AtomicBoolean> isRunning = new ConcurrentHashMap<String,AtomicBoolean>();
	private ConcurrentHashMap<String,ArrayBlockingQueue<Long>> queue = new ConcurrentHashMap<String,ArrayBlockingQueue<Long>>();
	private ExecutorService exec = Executors.newSingleThreadExecutor();
	private ConcurrentHashMap<String,String> lastCreateDate = new ConcurrentHashMap<String,String>();
	private AtomicBoolean isChanged = new AtomicBoolean(false);
	private long lastModifyTime;
	private final static  Pattern propertyReg = Pattern.compile("^[a-zA-Z]+=[0-9]+$");
	public GenerateCoreImpl(){
		reloadProperties();
	}

	public void reloadProperties(){
		if(config == null){
			File path = new File("store");
			config = new File(path,"IDGenerate.properties");
			if(!path.exists()){
				path.mkdirs();
			}
		}

		if(props==null){
			props = new Properties();
			if(config.exists()){
				try {
					props.load(new FileReader(config));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(config.lastModified() > lastModifyTime){
			Properties p = new Properties();
			try {
				p.load(new FileReader(config));
				for(Object key:p.keySet()){
					String k = String.valueOf(key);
					if(!propertyReg.matcher(k).find())continue;
					long v = Long.parseLong(p.getProperty(k));
					if(props.getProperty(k)==null || v > Long.parseLong(props.getProperty(k))){
						System.out.println(new StringBuilder().append("[changed] ").append(new Date().toString()).append(" ").append(key).append(" value:").append(props.getProperty(k))
								.append(" changed:").append(v));
						isChanged.set(true);
						props.setProperty(k, p.getProperty(k));
					}
				}
				lastModifyTime = config.lastModified();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			props.store(new FileWriter(config), props.toString());
		} catch (IOException e) {
			e.printStackTrace();
		};
//		System.out.println(new Date().toString()+" reload !");
	}


	private final static String[] crypt_dict = { "6207548913", "3147896520", "3026584197",
			"9452703186", "8402691735", "2149356807", "3026471895", "5042897163" };

	private final static String[] decrypt_dict = { "2819540367", "9180276345", "1720643958",
			"5736129480", "2638194705", "8104256973", "1620493578", "1739208645" };

	public long encode(long num,int digit) {
		long head = 0;
		char[] val = String.valueOf(num).toCharArray();
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
		StringBuffer digitStringBuffer = new StringBuffer();
		for (int i = 0; i < digit; i++) {
			digitStringBuffer.append("0");
		}
		String digitString = digitStringBuffer.toString();
		String number = new DecimalFormat(digitString).format(num-head);
		int shift = number.charAt(computerDigit) - '0';
		String encrypt_number = digitString;
		char[] s = encrypt_number.toCharArray();
		for (int i = 0; i < computerDigit; i++) {
			String dict_item = crypt_dict[(i + shift) % 8];
			s[i] = dict_item.charAt(number.charAt(i) - '0');
		}
		s[computerDigit] = number.charAt(computerDigit);
		encrypt_number = String.valueOf(s);
		return Long.parseLong(encrypt_number) + head;
	}

	public long decode(long cryptNum , int digit) {
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
	/**
	 * 根据服务名称获取当前加密后序列值
	 * @param name
	 * @return
	 */
	public long getID(final IDRequest req){
		ArrayBlockingQueue<Long> _q = queue.get(req.getName());
		AtomicBoolean _s = isRunning.get(req.getName());
		if(_q == null){
			synchronized(queue){
				_q = queue.get(req.getName());
				if(_q==null){
					_q = new ArrayBlockingQueue<Long>(1200);
					queue.put(req.getName(), _q);
					_s = new AtomicBoolean(false);
					isRunning.put(req.getName(), _s);
				}
			}
		}
		int size = _q.size();
		if(size < 2){
			generate(req);
		}else if(!_s.get() && size<50){
			_s.getAndSet(true);
			exec.execute(new Runnable(){
				public void run() {
					generate(req);
				}
			});
		}

		try {
			return _q.poll(1000,TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			return -1;
		}
	}

	private void generate(IDRequest req){
		synchronized(config){
			AtomicBoolean _s = isRunning.get(req.getName());
			_s.getAndSet(true);
			ArrayBlockingQueue<Long> _q = queue.get(req.getName());
			if(_q.size()>step)return;
			AtomicLong v =  new AtomicLong(Long.parseLong(props.getProperty(req.getName(),"0")));
			Long maxNum = null;
			String today = null;
			if(req.isDateRange()){
				String formatter = req.getDateFormatter();
				if(formatter == null){
					formatter = "yyMMdd";
				}
				SimpleDateFormat sdf = new SimpleDateFormat(formatter);
				today = sdf.format(new Date());
				if(lastCreateDate.get(req.getName()) == null){
					lastCreateDate.put(req.getName(), today);
				}
			}
			
			for(int i=0;i<1000;i++){
				
				if(req.isRandom()){
					if(maxNum == null){
						maxNum = bitVal(req.getDigit());
					}
					if(v.get() > maxNum){
						v.set(0);
					}
				}
				
				if(req.isDateRange()){
					if(maxNum == null){
						maxNum = bitVal(req.getDigit());
					}
					//v.get() > maxNum &&
					if( !lastCreateDate.get(req.getName()).equals(today)){
						v.set(0);
						lastCreateDate.put(req.getName(), today);
					}
				}
				
				Long sv = req.selfControlSeed(v.get());
				if(sv != null && sv > 0){
					v.set(sv);
				}
				
				long encodeId = encode(v.getAndIncrement(),req.getDigit());
				if(req.isDateRange()){
					StringBuilder sb = new StringBuilder();
					sb.append(today);
					int dif = req.getDigit() - String.valueOf(encodeId).length();
					if(dif>0){
						for(int j=0;j<dif;j++){
							sb.append("0");
						}
					}
					sb.append(encodeId);
					_q.offer(Long.parseLong(sb.toString()));
				}else{
					_q.offer(encodeId);
				}
			}
			props.setProperty(req.getName(), v.toString());
			isChanged.set(true);
			_s.set(false);
		}
	}

	public String watch(){
		if(queue==null||props==null)return "";
		StringBuilder sb = new StringBuilder();
		sb.append("[monitor] ").append(new Date()).append("\t");
		for(Object k : props.keySet()){
			String key = String.valueOf(k);
			ArrayBlockingQueue<Long> q = queue.get(key);
			sb.append(" ").append(key).append(":").append(props.get(key)).append("-").append(q==null?0:q.size());
		}
		return sb.toString();
	}

	public void changePropertyValue(String key,String value){
		props.setProperty(key, value);
		try {
			props.store(new FileWriter(config), props.toString());
		} catch (IOException e) {
			e.printStackTrace();
		};
	}


	public File getStoreFile(){
		return config;
	}

	public ArrayBlockingQueue<Long> getQueue(String name){
		return queue.get(name);
	}
	
	/**
	 * 测试专用
	 * @param name
	 */
	public void clearQueue(String name){
		ArrayBlockingQueue<Long> q = queue.get(name);
		while(!q.isEmpty()){
			q.poll();
		}
	}

	public String shutdown(){
		synchronized(config){
			for(Object k : props.keySet()){
				String key = String.valueOf(k);
				ArrayBlockingQueue<Long> q = queue.get(key);
				if(q!=null && q.size() > 0){
					long curStart = Long.parseLong(props.getProperty(key));
					props.put(key, String.valueOf(curStart-q.size()));
				}
			}
			queue.clear();
			try {
				props.store(new FileWriter(config), props.toString());
			} catch (IOException e) {
				e.printStackTrace();
			};
		}
		exec.shutdown();
		try {
			exec.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return config.getAbsolutePath();
	}
	public long bitVal(int bit){
		char[] s = new char[bit];
		for(int i=0;i<bit;i++){
			s[i] = '9';
		}
		return Long.parseLong(String.valueOf(s));
	}

	public void setLastDay(String name,String date){
		lastCreateDate.put(name, date);
	}
	
	public String getLastCreateDate(String name){
		return lastCreateDate.get(name);
	}
}
