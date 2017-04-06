package com.wangjiu.common.util;

import cn.qdevelop.common.asynchronous.AsynExcutor;

public class AsyncRun {

	public static void main(String[] args) {
		for(int i=0;i<100;i++){
		AsynExcutor.getInstance().asynExec(new Runnable(){
			@Override
			public void run() {
				System.out.println(Math.random());
			}
		});
		}
	}

}
