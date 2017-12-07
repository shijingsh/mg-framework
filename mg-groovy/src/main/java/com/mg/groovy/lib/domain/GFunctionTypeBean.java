package com.mg.groovy.lib.domain;

import java.util.ArrayList;
import java.util.List;

/** 
 * 函数分类实体bean 
 * 用于返回前端
 * @author: liukefu
 * @date: 2015年4月15日 下午6:47:43  
 */
public class GFunctionTypeBean {

	/**
	 * 函数分类名称
	 */
	private String typeName;

	/**
	 * 函数列表
	 */
	List<GFunctionBean> list = new ArrayList<>();
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public GFunctionTypeBean(String typeName) {
		super();
		this.typeName = typeName;
	}

	public List<GFunctionBean> getList() {
		return list;
	}

	public void setList(List<GFunctionBean> list) {
		this.list = list;
	}

	public GFunctionTypeBean(String typeName, List<GFunctionBean> list) {
		super();
		this.typeName = typeName;
		this.list = list;
	}

	
	
}
