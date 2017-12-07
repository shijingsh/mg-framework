package com.mg.groovy.define.bean;

import org.apache.commons.lang3.StringUtils;


/** 
 * groovy 赋值语句  
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午3:17:44  
 */
public class GSentenceGiv extends GSentenceBase{

	/**
	 * 被赋值变量
	 */
	GVariable gVariable;
	
	/**
	 * 赋值常量
	 */
	String gValue;
		
	@Override
	public String toGroovy() {
		
		StringBuilder sb = new StringBuilder();
		if(gVariable.isNew()){
			sb.append(" def ");
		}
		if(gVariable.getVarType()!=null){
			sb.append(" ").append(gVariable.getVarType().getgGClassName());
		}
		sb.append(gVariable.getVarName());
		sb.append(" = ");
		
		if(StringUtils.isBlank(gValue)){
			//说明是表达式赋值
			sb.append(super.toGroovy());
		}else{
			sb.append(gValue);
		}
		
		return sb.toString();
	}

}
