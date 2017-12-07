package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.KeyWords;
import org.apache.commons.lang3.StringUtils;

/** 
 * 导入类库语句 
 * 
 * @author: liukefu
 * @date: 2015年4月17日 上午9:26:48  
 */
public class GSentenceImport extends GSentenceBase{

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
			//import 指令
			sb.append(KeyWords.kw_import).append(" ");
			sb.append(gSentence).append("\n");
		}
		
		return sb.toString();
	}
}
