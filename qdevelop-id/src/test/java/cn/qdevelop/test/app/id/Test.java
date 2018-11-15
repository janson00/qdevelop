package cn.qdevelop.test.app.id;

import cn.qdevelop.app.id.IDClient;

public class Test {

  public static void main(String[] args) {
    try {
      for(int i=0;i<1105;i++)
//        System.out.println(IDClient.getInstance().getID("random-test", 4, 10));
      System.out.println(IDClient.getInstance().getRandomID());
    } catch (Exception e) {
      e.printStackTrace();
    }
    IDClient.getInstance().shutdown();
//    int val = 99;int digit = 7;
//    StringBuffer sb = new StringBuffer();
//    sb.append(val);
//    int diff = digit - sb.length();
//    if(diff>0){
//      for(int i=0;i<diff;i++){
//        sb.insert(0, "0");
//      }
//    }
//    System.out.println(sb);
  }

}
