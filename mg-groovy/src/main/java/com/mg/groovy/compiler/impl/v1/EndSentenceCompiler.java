package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.*;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.util.CompilerUtil;
import org.apache.commons.lang3.StringUtils;

/** 
 * 编译语句结束符号 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:42:33  
 */
public class EndSentenceCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode,GroovyConstants.gc_semicolon,0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		
		String code = CompilerUtil.substring(groovyCode, 0, startIndex);
		if(StringUtils.isBlank(code)){
			GSentenceBase endSentence = new GSentenceEnd();
			endSentence.setParentSentence(sentence);
			sentence.addGSentence(endSentence);
			
			return new GCompilerResult(endSentence,startIndex+GroovyConstants.gc_semicolon.length());
		}else{
			if(CompilerUtil.isExpress(code)){
				CompilerUtil.compilerException("语法错误，未知表达式语句！",groovyCode,CompilerUtil.ERROR_SYSTEM);
			}
			code = code.trim();
			GVariable gVar = CompilerUtil.getGVariable(sentence, code);
			if(gVar == null){
				//变量未定义返回null
				GSentenceSimple simpple = new GSentenceSimple( " null " );
				simpple.addGSentence(new GSentenceEnd());
				simpple.setParentSentence(sentence);
				sentence.addGSentence(simpple);
				return new GCompilerResult(sentence,startIndex+GroovyConstants.gc_semicolon.length());
			}
			
			return new GCompilerResult(null,startIndex+GroovyConstants.gc_semicolon.length());
		}
	}
}
