package com.mg.groovy.define.bean;

import org.apache.commons.lang3.StringUtils;

/** 
 * groovy 元语句  
 * 
 * @author: liukefu
 * @date: 2015年2月7日 下午5:20:01  
 */
public class GSentenceSimple extends GSentenceBase{
	
	String gSentence;
		
	public GSentenceSimple(String gSentence) {
		this.gSentence = gSentence;
	}

	public String getgSentence() {
		return gSentence;
	}

	public void setgSentence(String gSentence) {
		this.gSentence = gSentence;
	}

	@Override
	public String toGroovy() {
		
		StringBuilder sb = new StringBuilder();
		
		if(StringUtils.isNotBlank(gSentence) && !"null".equals(gSentence.trim())){
			sb.append(" ").append(gSentence);
		}
		sb.append(super.toGroovy());
		return sb.toString();
	}

}
