package com.mg.groovy.define.bean;

import org.apache.commons.lang3.StringUtils;

/** 
 * asm语句 
 * 用于直接写groovy 脚本，避开编译器的检查
 * @author: liukefu
 * @date: 2015年3月13日 上午10:52:48  
 */
public class GSentenceAsm extends GSentenceBase{

	/**
	 * 内嵌的语句
	 */
	String gSentence;

	public String getgSentence() {
		return gSentence;
	}

	public void setgSentence(String gSentence) {
		this.gSentence = gSentence;
	}

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		
		if(StringUtils.isNotBlank(gSentence)){
			//asm 指令内容暂时不作处理
			sb.append(gSentence).append("\n");
		}
		
		return sb.toString();
	}
}
