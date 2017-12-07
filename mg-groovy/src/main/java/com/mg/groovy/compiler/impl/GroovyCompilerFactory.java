package com.mg.groovy.compiler.impl;


import com.mg.groovy.define.interfaces.CompilerProxy;

/**
 * GroovyCompilerFactory 
 * 编译器统一从这里产生
 * @author: liukefu
 * @date: 2015年3月13日 上午10:44:05  
 */
public class GroovyCompilerFactory {

	public static CompilerProxy createCompiler(Class<?> clz){
		
		if(clz!=null && CompilerProxy.class.isAssignableFrom(clz)){
			try {
				return (CompilerProxy)clz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
