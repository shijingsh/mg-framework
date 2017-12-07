package com.mg.groovy.lib.domain.interfaces;

/** 
 * GBaseFunction 
 * groovy 函数基类
 * @author: liukefu
 * @date: 2015年4月15日 下午6:42:36  
 */
public abstract class GBaseFunction {
	
	public String funTypeName = "系统函数";

	public String getFunTypeName() {
		return funTypeName;
	}

	public void setFunTypeName(String funTypeName) {
		this.funTypeName = funTypeName;
	}

}
