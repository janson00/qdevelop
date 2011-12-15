package com.test;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.qdevelop.model.utils.QDevelopConfig;

public class Generator {
	public static void  main(String[] args) throws Exception {
		ClassReader cr = new ClassReader("com.test.Account");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
		cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
		byte[] data = cw.toByteArray();
		//QDevelopConfig.getResource("Account.class").toURI()
		File file = new File("E:\\workspace\\Template\\WebRoot\\WEB-INF\\classes\\com\\test\\Account.class");
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(data);
		fout.close();
		
		
		Account account = new Account();
		account.operation();
	}


}
