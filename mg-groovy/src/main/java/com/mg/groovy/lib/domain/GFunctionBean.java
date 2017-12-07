package com.mg.groovy.lib.domain;

import java.util.ArrayList;
import java.util.List;

/** 
 * 函数实体
 * 用于返回前端
 * @author: liukefu
 * @date: 2015年4月15日 下午3:14:30  
 */
public class GFunctionBean implements java.io.Serializable{
	
	private static final long serialVersionUID = 5885742836590591246L;
	/**
	 * 函数分类名称
	 */
	String typeName;
	/**
	 * 函数名称
	 */
	String methodName;
	/**
	 * 函数api说明文件
	 */
	String notesUrl;
	/**
	 * 所有的函数形式
	 * （包含函数的各种重载）
	 */
	List<GCallMethod> methodList = new ArrayList<>();
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<GCallMethod> getMethodList() {
		return methodList;
	}
	public void setMethodList(List<GCallMethod> methodList) {
		this.methodList = methodList;
	}
	public GFunctionBean() {
		
	}
	public GFunctionBean(String typeName, String methodName,
			GCallMethod method) {
		super();
		this.typeName = typeName;
		this.methodName = methodName;
		this.methodList.add(method);
	}
	public String getNotesUrl() {
		return notesUrl;
	}
	public void setNotesUrl(String notesUrl) {
		this.notesUrl = notesUrl;
	}
	public GFunctionBean(String typeName, String methodName, String notesUrl,
			GCallMethod method) {
		super();
		this.typeName = typeName;
		this.methodName = methodName;
		this.notesUrl = notesUrl;
		this.methodList.add(method);
	}
	
	
}
