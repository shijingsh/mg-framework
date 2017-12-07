package com.mg.groovy.service;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

/** 
 * GroovyBean 
 * 
 * @author: liukefu
 * @date: 2015年4月16日 上午11:33:29  
 */
public class GroovyBean implements java.io.Serializable{

	private static final long serialVersionUID = -6684822046782189031L;

	/**
	 * 是否需要运行脚本
	 * 主编译器使用的参数，true 则运行脚本
	 */
	public boolean needRunning = false;
	
	/**
	 * groovy 编译前的代码
	 */
	public String groovyCode ;

	/**
	 * binding 的参数
	 */
	@JSONField(serialize = false, deserialize = false)
	public Map<String,Object> bindings = new HashMap<>();
	
	public boolean isNeedRunning() {
		return needRunning;
	}

	public void setNeedRunning(boolean needRunning) {
		this.needRunning = needRunning;
	}

	public String getGroovyCode() {
		return groovyCode;
	}

	public void setGroovyCode(String groovyCode) {
		this.groovyCode = groovyCode;
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, Object> bindings) {
		this.bindings = bindings;
	}
	
	public void addBindings(Map<String, Object> bindings) {
		this.bindings.putAll(bindings);
	}
}
