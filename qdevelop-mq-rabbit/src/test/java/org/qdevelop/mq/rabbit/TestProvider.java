package org.qdevelop.mq.rabbit;

import java.util.HashMap;

public class TestProvider {

	public static void main(String[] args) {
		HashMap<String,String> aa = new HashMap<String,String>();
		aa.put("zzz", "1、双11（可以延伸为11.1-11.11）按照减肥、美容、男号三个巴的销售情况及团队管理情况 2、按照产品的销售情况、新产品的开发建议 3、双11客户问题的发现，反馈，改进。包括收货、退款、物料、产品功效等 4、大健康人员流失情况及改进措施 1、神灯巴如何支撑业务部门在如下两方面做出提升： （1）提高工作效率 （2）更好的做经营数据分析 2、蜜桃app开发规划、进展及存在的问题、app运营策略及目标&实现路径。");
		long s = System.currentTimeMillis();
		MQProvider mq = new MQProvider("plugin-config/rabbit-mq.properties");
		for(int i=0;i<10000;i++){
			mq.publish("Janson", aa);
		}
//		mq.publish("Janson", aa);
		System.out.println("===> "+(System.currentTimeMillis()-s));
//		 MQProvider.getInstance().publish("Janson", "janson");


//		for(int i=0;i<10000;i++){
//			Object[] v = new Object[]{"test",i};
//			 MQProvider.getInstance().publish("Janson", v);
//			 System.out.print(".");
////			 try {
////				Thread.sleep(1000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
//		}
	}

}
