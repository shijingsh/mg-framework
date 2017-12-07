package com.mg.groovy.define.interfaces;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;

/**
 * 编译器 
 * 
 * @author: liukefu
 * @date: 2015年2月5日 下午2:42:12  
 */
public interface Compiler {

	GCompilerResult compile(String groovyCode) throws CompilerGroovyException;
}
