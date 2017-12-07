package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.GroovyConstants;
import org.apache.commons.lang3.StringUtils;

/** 
 * groovy 变量定义 
 * 
 * @author: liukefu
 * @date: 2015年2月5日 下午5:06:26  
 */
public class GVariable extends GSentenceBase{

	/**
	 * 自定义类型、一般的变量为null
	 */
	GClass varType;
	/**
	 * 变量名称
	 */
	String varName;
	/**
	 * 变量初始化值
	 */
	String varValue;
	
	/**
	 * 定义一个变量，则isNew = true;
	 */
	boolean isNew = false;
	@Override
	public String toGroovy() {
		
		StringBuilder sb = new StringBuilder();
		if(varName.startsWith("tmp_var_")){
			sb.append(varValue);
			return sb.toString();
		}
		if(isNew){
			sb.append(" def ");
		}
		if(varType!=null){
			sb.append(varType.getgGClassName()).append(" ");
		}		
		sb.append(varName);
		
		if(StringUtils.isNotBlank(varValue)){
			sb.append("=");
			sb.append(varValue).append(" ");
		}else{
			String right = super.toGroovy();
			if(StringUtils.isNotBlank(right)){
				if(right.startsWith(GroovyConstants.gc_semicolon)){
					sb.append("= null");
					sb.append(right);
				}else{
					sb.append("=");
					sb.append(right);
				}
			}
		}
		return sb.toString();
	}

	public GVariable(GClass varType, String varName, String varValue) {
		super();
		this.varType = varType;
		this.varName = varName;
		this.varValue = varValue;
	}

	public GVariable(String varName, String varValue) {
		super();
		this.varName = varName;
		this.varValue = varValue;
	}
	public GVariable(String varName) {
		super();
		this.varName = varName;
	}
		
	public GClass getVarType() {
		return varType;
	}

	public void setVarType(GClass varType) {
		this.varType = varType;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarValue() {
		return varValue;
	}

	public void setVarValue(String varValue) {
		this.varValue = varValue;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((varName == null) ? 0 : varName.hashCode());
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
		if (!(obj instanceof GVariable)) {
			return false;
		}
		GVariable other = (GVariable) obj;
		if (varName == null) {
			if (other.varName != null) {
				return false;
			}
		} else if (!varName.equals(other.varName)) {
			return false;
		}
		return true;
	}
}
