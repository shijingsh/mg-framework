package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.GClass;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GVariable;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;
import org.apache.commons.lang3.StringUtils;

/** 
 * 编译变量定义语句 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:42:02  
 */
public class DefCompiler implements CompilerProxy {

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException {
		
		int startIndex = CompilerUtil.indexOf(groovyCode, KeyWords.kw_def, 0);
		int endIndex = CompilerUtil.getSentenceEndIndex(groovyCode);
		if(endIndex<=0){
			CompilerUtil.compilerException("语句未正常结束!",groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		if(startIndex != -1){
			String code = CompilerUtil.substring(groovyCode, startIndex, endIndex);
			int indexGiv = CompilerUtil.indexOf(code, GroovyConstants.gc_giv,0);
			GCompilerResult result = null;
			if(indexGiv != -1){
				String codeLeft = CompilerUtil.getBetweenStr(code,KeyWords.kw_def,GroovyConstants.gc_giv);
				String codeRight = CompilerUtil.substring(code, indexGiv+GroovyConstants.gc_giv.length(), endIndex);
				result = parseGiv(sentence,codeLeft,codeRight,endIndex);				
			}else{
				//没有赋值语句
				String codeRight = CompilerUtil.substring(groovyCode, startIndex+KeyWords.kw_def.length(), endIndex);
				result = parseGiv(sentence,codeRight,null,endIndex);
			}
			result.setEndIndex(CompilerUtil.getSentenceEndIndex(groovyCode,false));
			return result;
		}
		
		return new GCompilerResult(null);
	}

	public GCompilerResult parseGiv(GSentenceBase sentence,String codeLeft,String codeRight,int endIndex) throws CompilerGroovyException{
		String[] arr = CompilerUtil.split(codeLeft,GroovyConstants.gc_blank);
		
		String varType = null;
		String varName = null;
		if(arr.length>1){
			varType = arr[0].trim();
			varName = arr[1].trim();
		}else{
			varName = arr[0].trim();
		}
		if(CompilerUtil.isConstant(codeRight)){
			GVariable var = new GVariable(varName,codeRight);
			var.setNew(true);
			
			if(StringUtils.isNotBlank(varType)){
				GClass varGClass = CompilerUtil.getGClass(sentence, varType);
				if(varGClass == null){
					CompilerUtil.compilerException("类型："+varType+"未定义！");
				}
				var.setVarType(varGClass);
			}
			
			sentence.addGVariable(var);
			return new GCompilerResult(var,endIndex);
		}else{
			if(varType==null){
				//说明不是对象类型声明，那右侧可能是个语句
				if(StringUtils.isNotBlank(codeRight)){
					//是一个表达式
					CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));

					if(proxy != null){						
						GVariable var = new GVariable(varName);
						sentence.addGVariable(var);
						var.setParentSentence(sentence);
						var.setNew(true);
						proxy.compile(var, codeRight);
						
						return new GCompilerResult(var,endIndex);
					}
				}
			}else{
				//new obj 语句
			}
		}
		
		return new GCompilerResult(null,0);
	}
}
