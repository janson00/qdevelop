package cn.qdevelop.control;

import com.alibaba.fastjson.JSON;

import cn.qdevelop.service.bean.OutputJson;

public class Tessssss {

	public static void main(String[] args) {
//		JsonResult<String> jr = new JsonResult<String>();
//		jr.setData("");
		System.out.println(JSON.toJSONString(10));
		System.out.println(JSON.toJSONString("x"));
		System.out.println(JSON.toJSONString(new String[]{"1","2"}));
	}

}
