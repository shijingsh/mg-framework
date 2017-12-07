package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;


/** 
 * groovy 判断语句 
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午3:13:48  
 */
public class GSentenceIf extends GSentenceBase{

	GSentenceBase condition;
	
	public GSentenceBase getCondition() {
		return condition;
	}

	public void setCondition(GSentenceBase condition) {
		this.condition = condition;
	}

	@Override
	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n").append(KeyWords.kw_if).append(GroovyConstants.gc_brackets_small);
		if(condition != null){
			sb.append(condition.toGroovy());
		}else{
			sb.append(true);
		}
		sb.append(GroovyConstants.gc_brackets_small_end).append(GroovyConstants.gc_brackets_big);
		sb.append(super.toGroovy());		
		sb.append(GroovyConstants.gc_brackets_big_end);
		
		return sb.toString();
	}

}
