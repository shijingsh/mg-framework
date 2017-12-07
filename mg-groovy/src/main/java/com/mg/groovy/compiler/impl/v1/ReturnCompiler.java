package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.*;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;
import org.apache.commons.lang3.StringUtils;

/** 
 * 返回语句编译器 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:43:50  
 */
public class ReturnCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode,KeyWords.kw_return,0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		int endIndex = CompilerUtil.getSentenceEndIndex(groovyCode);
		if(endIndex==-1){
			endIndex = groovyCode.length();
		}
		String code = CompilerUtil.substring(groovyCode, startIndex+KeyWords.kw_return.length(), endIndex);
		GSentenceReturn gSentenceReturn = new GSentenceReturn();
		gSentenceReturn.setParentSentence(sentence);
		sentence.addGSentence(gSentenceReturn);
		if(StringUtils.isNotBlank(code)){
			if(CompilerUtil.isExpress(code)){
				//是一个表达式,编译return 右侧表达式
				CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));			
				if(proxy != null){
					GCompilerResult result =  proxy.compile(new GSentenceSimple(null), code);
					if(result.getgSentence()!=null){
						gSentenceReturn.setgSentenceBase(result.getgSentence());
					}					
				}
			}else if(CompilerUtil.isConstant(code)){
				//常量
				GSentenceSimple simpple = new GSentenceSimple( code );
				gSentenceReturn.setgSentenceBase(simpple);
			}else{
				GVariable gVar = CompilerUtil.getGVariable(sentence, code);
				if(gVar != null){
					GSentenceSimple simpple = new GSentenceSimple( gVar.getVarName() );
					gSentenceReturn.setgSentenceBase(simpple);
				}else{
					//变量未定义返回null
					GSentenceSimple simpple = new GSentenceSimple( " null " );
					gSentenceReturn.setgSentenceBase(simpple);
				}
			}
		}else{
			//变量未定义返回null
			GSentenceSimple simpple = new GSentenceSimple( " null " );
			gSentenceReturn.setgSentenceBase(simpple);
		}
		
		return new GCompilerResult(gSentenceReturn,CompilerUtil.getSentenceEndIndex(groovyCode,false));
	}
}
