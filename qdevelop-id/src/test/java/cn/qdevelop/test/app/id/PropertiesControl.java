package cn.qdevelop.test.app.id;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import cn.qdevelop.app.id.IDClient;
import cn.qdevelop.app.id.server.impl.IDGenerate;

public class PropertiesControl {

	public static void resetValueByKey(String key,String value){
		File f = IDGenerate.getInstance().getStoreFile();
		Properties props = new Properties();
		try {
			props.load(new FileReader(f));
			props.setProperty(key, value);
			props.store(new FileWriter(f), props.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			for(int i=0;i<1;i++)
				System.out.println(IDClient.getInstance().getCouponID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
