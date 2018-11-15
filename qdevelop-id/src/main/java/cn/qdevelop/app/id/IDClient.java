package cn.qdevelop.app.id;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class IDClient {
  private static IDClient _IDClient = new IDClient();
  public IDClient(){
    Runtime.getRuntime().addShutdownHook(new Thread(){
      public void run(){
        IDClient.getInstance().shutdown();
      }
    });
  }
  public static IDClient getInstance() {
    return _IDClient;
  }

//  private long lastTimer=0;
//  private int continuousHit = 1;

  private ConcurrentHashMap<String, ArrayBlockingQueue<Integer>> queue = new ConcurrentHashMap<String, ArrayBlockingQueue<Integer>>();
  private HashMap<String,Integer> buffers = new HashMap<String,Integer>();

  /**
   * 获取用户ID
   * 
   * @return
   * @throws Exception
   */
  public int getUserID() throws Exception {
    return getID("user", 8, 2);
  }

  /**
   * 获取商品ID
   * 
   * @return
   * @throws Exception
   */
  public int getProductID() throws Exception {
    return getID("product", 6, 2);
  }

  /**
   * 获取随机验证码
   * 
   * @return
   * @throws Exception
   */
  public int getRandomID() throws Exception {
    return getID("random", 6, 5);
  }

  /**
   * 获取订单ID
   * 
   * @return
   * @throws Exception
   */
  public int getOrderID() throws Exception {
    return getID("order", 8, 2);
  }

  /**
   * 获取优惠券ID
   * 
   * @return
   * @throws Exception
   */
  public int getCouponID() throws Exception {
    return getID("coupon", 9, 2);
  }
  
  
  public String getIDStr(final String name, final int digit, final int buffer) throws Exception{
    int val = getID(name,digit,buffer);
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
   * 
   * @param name
   * @param digit
   * @param buffer
   * @return
   */
  public int getID(final String name, final int digit, final int buffer) throws Exception {
    try {
      ArrayBlockingQueue<Integer> _q = queue.get(name);
      if (_q == null) {
        synchronized (queue) {
          _q = queue.get(name);
          if (_q == null) {
            _q = new ArrayBlockingQueue<Integer>(500, true);
            queue.put(name, _q);
            buffers.put(name, buffer);
          }
        }
      }

      if (_q.size() < 1) {
        synchronized (_q) {
          sync(name, digit, buffer);
        }
      }
      return _q.poll(500,TimeUnit.MILLISECONDS); 
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  Socket server = null;
  private void sync(String name, final int digit, final int buffer) {
    Integer[] vals;
    ArrayBlockingQueue<Integer> _q = queue.get(name);
    if (_q.size() > 2) {
      return;
    }
    long startTime = System.currentTimeMillis();
//    int bufferSize = (continuousHit++)*buffer;
//    if(bufferSize>400)bufferSize = 400;
    vals = (Integer[]) socketSender(new StringBuilder().append(name).append(":").append(digit).append(":")
      .append(buffer).toString());
    if (vals != null && vals.length > 0) {
      for (Integer v : vals) {
        if (v > 0) {
          _q.offer(v);
        }
      }
    }
    long consumeTime = System.currentTimeMillis() - startTime;
    if (consumeTime > 500) {
      System.out.println("队列服务名称："+name+";耗时："+consumeTime);
    }
  }
  
  private Object socketSender(String cmd) {
    PrintWriter out = null;
    ObjectInputStream ois=null;
    try {
      if(server == null || server.isClosed()){
        synchronized(this){
          if(server == null || server.isClosed()){
            server = new Socket("127.0.0.1", 65500);//idgenerate.wangjiu.com
          }
        }
      }
      out = new PrintWriter(server.getOutputStream());
      ois = new ObjectInputStream(server.getInputStream());
      out.println(cmd);
      out.flush();
      return ois.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private void rollback(){
    StringBuilder sb;
    Iterator<Entry<String, ArrayBlockingQueue<Integer>>> itor = queue.entrySet().iterator();
    while(itor.hasNext()){
      Entry<String, ArrayBlockingQueue<Integer>> item = itor.next();
      ArrayBlockingQueue<Integer> _q = item.getValue();
      if(!_q.isEmpty()){
        sb = new StringBuilder();
        sb.append(item.getKey()).append("@");
        while(!_q.isEmpty()){
          sb.append(_q.poll()).append(",");
        }
        System.out.println(sb.toString());
        socketSender(sb.substring(0,sb.length()-1).toString());
      }
    }
  }

  public void shutdown(){
    if(server==null)return;
    synchronized(server){
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
}
