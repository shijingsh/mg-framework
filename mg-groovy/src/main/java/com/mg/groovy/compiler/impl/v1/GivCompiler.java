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
 * 赋值编译器 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:42:57  
 */
public class GivCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int endIndex = CompilerUtil.getSentenceEndIndex(groovyCode);
		if(endIndex<=0){
			CompilerUtil.compilerException("语句未正常结束！",groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		int indexGiv = CompilerUtil.indexOf(groovyCode, GroovyConstants.gc_giv,0);
		GCompilerResult result = null;
		if(indexGiv != -1){			
			String codeLeft = CompilerUtil.substring(groovyCode, 0, indexGiv);
			String codeRight = CompilerUtil.substring(groovyCode, indexGiv+GroovyConstants.gc_giv.length(), endIndex);
			result = parseGiv(sentence,codeLeft,codeRight,endIndex);
		}else{
			//没有赋值语句
			String codeRight = CompilerUtil.substring(groovyCode, 0, endIndex);
			result = parseGiv(sentence,codeRight,null,endIndex);
		}
		result.setEndIndex(CompilerUtil.getSentenceEndIndex(groovyCode,false));
		return result;
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
			
			if(StringUtils.isNotBlank(varType)){
				GClass varGClass = CompilerUtil.getGClass(sentence, varType);
				if(varGClass == null){
					CompilerUtil.compilerException("类型："+varType+"未定义！");
				}
				var.setNew(true);
				var.setVarType(varGClass);
			}else{
				GVariable varHas = CompilerUtil.getGVariable(sentence,varName);
				if(varHas == null){
					CompilerUtil.compilerException("变量："+varName+"未定义！");
				}
			}
			
			sentence.addGVariable(var);
			return new GCompilerResult(var,endIndex);
		}else{
			if(varType==null){
				//说明不是对象类型声明，那右侧可能是个语句
				if(StringUtils.isNotBlank(codeRight)){
					CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));
					//是一个表达式
					if(proxy != null){
						GVariable var = new GVariable(varName);
						sentence.addGVariable(var);
						var.setParentSentence(sentence);
						proxy.compile(var, codeRight);
						/*
						   GCompilerResult result = proxy.compile(var, codeRight);
						   if(result.getgSentence()!=null){
							GSentenceBase childSentence = result.getgSentence();
							var.addGSentence(childSentence);
							childSentence.setParentSentence(var);
						}*/
						return new GCompilerResult(var,endIndex);
					}						
				}
			}else{
				//赋值语句返回对象类型
				if(StringUtils.isNotBlank(codeRight)){
					CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));
					if(proxy != null){
						GVariable var = new GVariable(varName);
						var.setVarType(new GClass(varType));
						sentence.addGVariable(var);
						var.setParentSentence(sentence);
						proxy.compile(var, codeRight);

						return new GCompilerResult(var,endIndex);
					}						
				}
			}
		}
		
		return new GCompilerResult(null);
	}
}
