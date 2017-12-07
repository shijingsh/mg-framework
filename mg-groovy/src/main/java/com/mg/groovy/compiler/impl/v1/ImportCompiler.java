package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GSentenceImport;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;

/** 
 * 导入类库编译器 
 * 
 * @author: liukefu
 * @date: 2015年4月17日 上午9:22:02  
 */
public class ImportCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode,KeyWords.kw_import,0);
		if(startIndex==-1){
			return new GCompilerResult(null,0);
		}
		startIndex = startIndex + KeyWords.kw_import.length();
		int endIndex = CompilerUtil.getSentenceEndIndex(groovyCode);
		if(endIndex==-1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_semicolon,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		
		String code = CompilerUtil.substring(groovyCode, startIndex, endIndex);

		GSentenceImport gSentenceImport = new GSentenceImport();
		gSentenceImport.setgSentence(code);
		sentence.addGSentence(gSentenceImport);
		
		return new GCompilerResult(gSentenceImport,endIndex);
	}
}
