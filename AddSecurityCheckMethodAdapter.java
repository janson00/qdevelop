package com.test;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class AddSecurityCheckMethodAdapter extends MethodAdapter implements MethodVisitor{
	public AddSecurityCheckMethodAdapter(MethodVisitor mv) {
		  super(mv);
		  // TODO Auto-generated constructor stub
		 }

		 
		 public void visitCode() {
		  visitMethodInsn(Opcodes.INVOKESTATIC, "SecurityChecker",
		    "checkSecurity", "()V");
		 }


}
