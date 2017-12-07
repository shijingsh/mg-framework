package com.mg.groovy.util;

public class CloneFilter {
	Class<?> clazz;
	String fieldName;
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public CloneFilter(Class<?> clazz, String fieldName) {
		super();
		this.clazz = clazz;
		this.fieldName = fieldName;
	}
	
}
