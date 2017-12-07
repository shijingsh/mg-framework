package com.mg.groovy.compiler;


import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.v1.GroovyCompilerImpl;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.interfaces.Compiler;
/**
 * Groovy 编译器
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午6:31:52  
 */
public class GroovyCompiler implements Compiler{

	public GCompilerResult compile(String groovyCode) throws CompilerGroovyException {
		
		GroovyCompilerImpl complier = new GroovyCompilerImpl();
		
		return complier.compile(groovyCode);
	}

}
