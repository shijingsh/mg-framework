package com.mg.groovy.define.bean;


/** 
 * groovy脚本自定义类型 
 * 
 * @author: liukefu
 * @date: 2015年2月4日 上午10:13:43  
 */
public class GClass extends GSentenceBase{

	/**
	 * 自定义类型名称
	 */
	String gGClassName;
	
	public String getgGClassName() {
		return gGClassName;
	}

	public void setgGClassName(String gGClassName) {
		this.gGClassName = gGClassName;
	}

	public GClass(String gGClassName) {
		super();
		this.gGClassName = gGClassName;
	}

	public GClass() {
		super();
	}

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		sb.append(" class ").append(gGClassName).append("{");
		sb.append(super.toGroovy());
		sb.append("}");
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gGClassName == null) ? 0 : gGClassName.hashCode());
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
		if (!(obj instanceof GClass)) {
			return false;
		}
		GClass other = (GClass) obj;
		if (gGClassName == null) {
			if (other.gGClassName != null) {
				return false;
			}
		} else if (!gGClassName.equals(other.gGClassName)) {
			return false;
		}
		return true;
	}


}
