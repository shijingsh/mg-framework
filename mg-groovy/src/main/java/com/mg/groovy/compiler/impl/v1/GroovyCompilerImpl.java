package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.*;
import com.mg.groovy.util.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mg.groovy.define.interfaces.Compiler;
/** 
 * 编译器的实现 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:44:42  
 */
public class GroovyCompilerImpl implements Compiler{

	protected Logger logger = LoggerFactory.getLogger(GroovyCompilerImpl.class);
	@Override
	public GCompilerResult compile(String groovyCode) throws CompilerGroovyException {
				
		GScript script = new GScript();
		//导入依赖包		

		//body编译器
		logger.debug("GroovyCompilerImpl compile script:"+groovyCode);
		BodyCompiler bodyCompiler = new BodyCompiler();
		bodyCompiler.compile(script, groovyCode);
		//默认添加return 语句
		GSentenceBase lastSentence = script.getLastGSentence();
		if(lastSentence!=null 
				&& !(lastSentence instanceof GSentenceReturn)
				&& !(lastSentence instanceof GSentenceIf)
				&& !(lastSentence instanceof GSentenceImport)
				&& !(lastSentence instanceof GSentenceAsm)){
			GSentenceReturn rt = new GSentenceReturn();
			rt.setgSentenceBase(lastSentence);
			int indx = script.getLastGSentenceIndex();
			script.getgSentenceList().set(indx, rt);
		}else{
			GSentenceReturn rt = new GSentenceReturn();
			rt.setgSentenceBase(new GVariable(CompilerUtil.randomVarName()));
			script.getgSentenceList().add(rt);
		}
		
		GCompilerResult result = new GCompilerResult(script);
		
		return result;
	}

}
