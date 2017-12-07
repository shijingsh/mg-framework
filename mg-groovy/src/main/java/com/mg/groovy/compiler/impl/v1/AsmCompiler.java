package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceAsm;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;

/** 
 * 不编译的代码段编译器 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:40:00  
 */
public class AsmCompiler implements CompilerProxy {

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException {
		
		int startIndex = CompilerUtil.indexOf(groovyCode, KeyWords.kw_asm, 0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		startIndex = CompilerUtil.indexOf(groovyCode, GroovyConstants.gc_brackets_big,0);
		if(startIndex==-1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_big,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		int endIndex = CompilerUtil.getMatchedChar(groovyCode,GroovyConstants.gc_brackets_big,GroovyConstants.gc_brackets_big_end,0);
		if(endIndex==-1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_big_end,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		
		String code = CompilerUtil.substring(groovyCode, startIndex+GroovyConstants.gc_brackets_big.length(), endIndex);

		GSentenceAsm gSentenceAsm = new GSentenceAsm();
		gSentenceAsm.setgSentence(code);
		sentence.addGSentence(gSentenceAsm);
		
		return new GCompilerResult(gSentenceAsm,CompilerUtil.getBlockEndIndex(groovyCode,true));
	}
}
