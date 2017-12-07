package com.mg.groovy.lib;

import com.mg.groovy.define.bean.GClass;

import java.util.HashMap;
import java.util.Map;

public class GroovyLib {

	/**
	 * 定义 groovy 可以引用默认类库
	 */
	public static final String clz_string = "str";
		
	/**
	 * 定义 groovy 可以引用类库
	 */
	public static Map<Object,GClass> libMap = new HashMap<>();
	
	static{
		libMap.put(clz_string, new GClass());
		libMap.put(clz_string, new GClass());
		libMap.put(clz_string, new GClass());
		libMap.put(clz_string, new GClass());
		libMap.put(clz_string, new GClass());
	}
	
	
}
