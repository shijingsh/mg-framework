package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.util.CompilerUtil;


/** 
 * GroovyConstants.gc_brackets_mid 编译器  
 * 闭合中括号编译器
 * @author: liukefu
 * @date: 2015年4月27日 下午4:33:06  
 */
public class MidBracketsCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		CompilerUtil.compilerException("语法错误:"+GroovyConstants.gc_brackets_mid+"+和"+GroovyConstants.gc_brackets_mid_end+"！",groovyCode,CompilerUtil.ERROR_SYSTEM);
		
		return new GCompilerResult(null,GroovyConstants.gc_brackets_mid.length());
	}
}
