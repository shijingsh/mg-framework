package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.KeyWords;


/** 
 * 返回语句 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:24:33  
 */
public class GSentenceReturn extends GSentenceBase{

	/**
	 * 返回的表达式
	 */
	GSentenceBase gSentenceBase;
	
	
	public GSentenceBase getgSentenceBase() {
		return gSentenceBase;
	}


	public void setgSentenceBase(GSentenceBase gSentenceBase) {
		this.gSentenceBase = gSentenceBase;
	}


	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		sb.append(KeyWords.kw_return);
		sb.append(" ");
		if(gSentenceBase!=null){
			sb.append(gSentenceBase.toGroovy());
		}
		
		return sb.toString();
	}
}
