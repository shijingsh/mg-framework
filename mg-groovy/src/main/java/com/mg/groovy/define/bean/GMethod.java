package com.mg.groovy.define.bean;

import java.util.ArrayList;
import java.util.List;

/** 
 * groovy脚本自定义函数
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午2:50:10  
 */
public class GMethod extends GSentenceBase{

	/**
	 * 自定义函数名称
	 */
	String gMethodName;
	/**
	 * 自定义函数参数列表
	 */
	List<GVariable> paramList = new ArrayList<>();
	
	public void addGParam(GVariable gVariable){
		if(!paramList.contains(gVariable)){
			paramList.add(gVariable);
		}
	}
		
	public String getgMethodName() {
		return gMethodName;
	}

	public void setgMethodName(String gMethodName) {
		this.gMethodName = gMethodName;
	}

	@Override
	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		sb.append(" def ").append(gMethodName);
		sb.append("(");
		for(GVariable var:paramList){
			sb.append(var.getVarName());
		}		
		sb.append(")");
		sb.append("{");
		sb.append(super.toGroovy());
		sb.append("}");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gMethodName == null) ? 0 : gMethodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GMethod)) {
			return false;
		}
		GMethod other = (GMethod) obj;
		if (gMethodName == null) {
			if (other.gMethodName != null) {
				return false;
			}
		} else if (!gMethodName.equals(other.gMethodName)) {
			return false;
		}
		return true;
	}
}
