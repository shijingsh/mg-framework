package com.mg.groovy.lib.emp;

import com.mg.groovy.define.bean.GVariable;

public class GroovyInstanceSeqId extends GVariable {

	public static final String name = "empInstanceSeqId";
	
	public GroovyInstanceSeqId() {
		super(name);
		
	}

	@Override
	public String toGroovy() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n").append("// 这里导入了 java 变量 ").append(name).append("\r\n");
		return sb.toString();
	}
}
