package com.mg.groovy.define.interfaces;


import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;

/**
 *  编译器代理接口
 * 
 * @author: liukefu
 * @date: 2015年2月5日 下午2:42:26  
 */
public interface CompilerProxy {

	/** 
	 * 编译代码
	 * @author liukefu
	 * @param gSentenceBase	上层语句
	 * @param groovyCode	代码块	
	 * @return				编译完成的位置
	 * @throws CompilerGroovyException
	 */
	GCompilerResult compile(GSentenceBase gSentenceBase, String groovyCode)throws CompilerGroovyException;
}
