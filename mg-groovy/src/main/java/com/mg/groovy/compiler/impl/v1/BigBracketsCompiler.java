package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;


/** 
 * GroovyConstants.gc_brackets_big 编译器  
 * 闭合大括号编译器
 * @author: liukefu
 * @date: 2015年4月27日 下午4:33:06  
 */
public class BigBracketsCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode,GroovyConstants.gc_brackets_big,0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		int matchIndex = CompilerUtil.getMatchedChar(groovyCode, GroovyConstants.gc_brackets_big, GroovyConstants.gc_brackets_big_end, 0);
		if(matchIndex==-1){
			CompilerUtil.compilerException("语法错误，缺少匹配的字符"+GroovyConstants.gc_brackets_big+"！",groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));
		String code = CompilerUtil.substring(groovyCode, startIndex+GroovyConstants.gc_brackets_big.length(), matchIndex);
		if(proxy!=null){
			GCompilerResult result = proxy.compile(sentence, code);
			result.setEndIndex(CompilerUtil.getBlockEndIndex(groovyCode,true));
			return result;
		}
		
		return new GCompilerResult(null,GroovyConstants.gc_brackets_big.length());
	}
}
