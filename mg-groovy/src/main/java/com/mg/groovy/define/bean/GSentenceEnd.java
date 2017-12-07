package com.mg.groovy.define.bean;


import com.mg.groovy.define.keyword.GroovyConstants;

/**
 * 语句结束标记语句 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:24:05  
 */
public class GSentenceEnd extends GSentenceBase{

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.toGroovy()).append(GroovyConstants.gc_semicolon).append("\n");
		
		return sb.toString();
	}
}
