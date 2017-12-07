package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GSentenceIf;
import com.mg.groovy.define.bean.GSentenceSimple;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;
import org.apache.commons.lang3.StringUtils;

/** 
 * IfCompiler 
 * 判断语句编译器
 * @author: liukefu
 * @date: 2015年4月9日 下午12:38:09  
 */
public class IfCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode,KeyWords.kw_if,0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		char afterIfChar = CompilerUtil.getOneOperateChar(groovyCode,startIndex+KeyWords.kw_if.length());
		if(!StringUtils.equals(GroovyConstants.gc_brackets_small, String.valueOf(afterIfChar))){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_small,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}else{
			startIndex = CompilerUtil.indexOf(groovyCode,GroovyConstants.gc_brackets_small,0)+GroovyConstants.gc_brackets_small.length();
		}
		int matchCharIndex = CompilerUtil.getMatchedChar(groovyCode,GroovyConstants.gc_brackets_small,GroovyConstants.gc_brackets_small_end,0);
		if(matchCharIndex==-1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_small_end,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		
		char afterConditionChar = CompilerUtil.getOneOperateChar(groovyCode,matchCharIndex+GroovyConstants.gc_brackets_small_end.length());
		int ifBodyStartIndex = CompilerUtil.indexOf(groovyCode,GroovyConstants.gc_brackets_big,0);
		if(!StringUtils.equals(GroovyConstants.gc_brackets_big, String.valueOf(afterConditionChar))){
			ifBodyStartIndex = matchCharIndex + GroovyConstants.gc_brackets_small_end.length();
		}else{
			ifBodyStartIndex = CompilerUtil.indexOf(groovyCode, GroovyConstants.gc_brackets_big, 0)+GroovyConstants.gc_brackets_big.length();			
		}
		int endIndex = CompilerUtil.getMatchedChar(groovyCode,GroovyConstants.gc_brackets_big,GroovyConstants.gc_brackets_big_end,0);
		if(endIndex==-1){
			endIndex = CompilerUtil.getSentenceEndIndex(groovyCode,true);
		}
		if(endIndex==-1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_big_end,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		
		String conditionCode = CompilerUtil.substring(groovyCode, startIndex, matchCharIndex);
		String bodyCode = CompilerUtil.substring(groovyCode, ifBodyStartIndex, endIndex);	
		endIndex = endIndex + GroovyConstants.gc_brackets_big_end.length();
		//编译条件部分
		CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));
		GSentenceIf gSentenceIf = new GSentenceIf();
		if(proxy != null){
			//条件
			GCompilerResult result =  proxy.compile(new GSentenceSimple(""), conditionCode);
			if(result.getgSentence()!=null){
				gSentenceIf.setCondition(result.getgSentence());
			}	/*
			gSentenceIf.setCondition(new GSentenceSimple(conditionCode));*/
			//条件语句块
			result =  proxy.compile(gSentenceIf, bodyCode);
			if(result.getgSentence()!=null){
				//gSentenceIf.addGSentence(result.getgSentence());
			}	
		}
		sentence.addGSentence(gSentenceIf);
		gSentenceIf.setParentSentence(sentence);
		return new GCompilerResult(gSentenceIf,endIndex);
	}
}
