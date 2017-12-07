package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.*;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.lib.domain.GCallMethod;
import com.mg.groovy.lib.domain.GFunctionBean;
import com.mg.groovy.util.CompilerUtil;
import com.mg.groovy.util.ExpressionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/** 
 * 函数编译器 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:43:10  
 */
public class MethodCallCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		int startIndex = CompilerUtil.indexOf(groovyCode, GroovyConstants.gc_brackets_small,0);
		if(startIndex == -1){
			return new GCompilerResult(null);
		}
		String gMethodName = CompilerUtil.substring(groovyCode, 0, startIndex);
		if(StringUtils.isBlank(gMethodName) || StringUtils.isBlank(gMethodName.trim())){
			CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_express));
			if(proxy!=null){
				return proxy.compile(sentence, groovyCode);
			}
			return new GCompilerResult(null);
		}
		int endIndex = CompilerUtil.getMatchedChar(groovyCode, GroovyConstants.gc_brackets_small, GroovyConstants.gc_brackets_small_end, 0);
		if(endIndex == -1){
			CompilerUtil.compilerMissException(GroovyConstants.gc_brackets_small_end,groovyCode,CompilerUtil.ERROR_SYSTEM);
		}
		char afterMethodChar = CompilerUtil.getOneOperateChar(groovyCode,endIndex+GroovyConstants.gc_brackets_small_end.length());
		if(ExpressionUtil.isOperate(String.valueOf(afterMethodChar)) 
				//下一个操作符的编译器比当前操作符编译器优先级高
				&& KeyWords.isPriority(GroovyConstants.gc_brackets_small,String.valueOf(afterMethodChar))){
			//函数后面是个四则运算符的话，则不作为函数处理
			return new GCompilerResult(null,CompilerUtil.getMethodEndIndex(groovyCode,true),false);
		}
		int matchCharIndex = CompilerUtil.getMatchedChar(groovyCode,GroovyConstants.gc_brackets_small,GroovyConstants.gc_brackets_small_end,0);
		String code = CompilerUtil.substring(groovyCode, startIndex+GroovyConstants.gc_brackets_small.length(), matchCharIndex);
		int indexComma = CompilerUtil.indexOf(code, GroovyConstants.gc_comma,0);
		
		GMethod gMethod = CompilerUtil.getGMethod(sentence, gMethodName.trim());
		if(gMethod ==null) {
			CompilerUtil.compilerException("函数："+gMethodName+"未定义！",groovyCode,CompilerUtil.ERROR_SYSTEM);
			return new GCompilerResult(null);
		}
		if(sentence!=null 
				&& sentence.getgSentenceList()!=null 
				&& sentence.getgSentenceList().size()>0){
			List<GSentenceBase> sentenceList = sentence.getgSentenceList();
			GSentenceBase lastSentence = sentenceList.get(sentenceList.size()-1);
			// 排除 max(1) max(2) 类型的语句，中间必须有操作符
			if(lastSentence instanceof GSentenceCall && !(sentence instanceof GSentenceCall)){
				CompilerUtil.compilerMissException(GroovyConstants.gc_semicolon,groovyCode,CompilerUtil.ERROR_SYSTEM);
				return new GCompilerResult(null);
			}
		}

		GSentenceCall gSentenceCall = new GSentenceCall();
		gSentenceCall.setgMethodName(gMethodName);
		sentence.addGSentence(gSentenceCall);
		gSentenceCall.setParentSentence(sentence);
		
		if(indexComma != -1){
			String paramArr [] = CompilerUtil.splitFunctionParam(code, GroovyConstants.gc_comma);
			for(String param:paramArr){
				if(StringUtils.isNotBlank(param)){
					//处理单个参数
					GVariable gVarParm = compileParam(gSentenceCall,param);
					if(gVarParm !=null){
						gSentenceCall.addGParam(gVarParm);
					}
				}
			}
		}else{
			//单个参数
			if(StringUtils.isNotBlank(code)){
				GVariable gVarParm = compileParam(gSentenceCall,code);
				if(gVarParm !=null){
					gSentenceCall.addGParam(gVarParm);
				}
			}
		}
		if(gMethod !=null) {
			//检查参数个数
			List<GVariable> paramList = gSentenceCall.getParamList();
			int numParam = 0;
			if(paramList!=null&&paramList.size()>0){
				numParam = paramList.size();
			}
			GFunctionBean gFunBean = GroovyFun.getFunctionBean(gMethodName);
			List<GCallMethod> gMethodList = gFunBean.getMethodList(); 
			boolean matchedParams = false;
			for(GCallMethod method: gMethodList){
				if(method.isDynamicParam() && numParam>0){
					matchedParams = true;
					break;
				}
				if(method.getParameterTypes()!=null && method.getParameterTypes().length==numParam){
					matchedParams = true;
				}
			}
			if(!matchedParams){
				CompilerUtil.compilerException("函数："+gMethodName+"参数个数错误！",groovyCode,CompilerUtil.ERROR_SYSTEM);
				return new GCompilerResult(null);
			}
		}
		return new GCompilerResult(gSentenceCall,CompilerUtil.getMethodEndIndex(groovyCode,true));
	}

	
	public GVariable compileParam(GSentenceCall gSentenceCall, String groovyCode){
		
		if(CompilerUtil.isConstant(groovyCode)){
			//如果是常量 直接增加一个参数变量
			GVariable gVariable = new GVariable(CompilerUtil.randomVarName(),groovyCode);
			
			return gVariable;
		}else{
			GVariable gVariable = CompilerUtil.getGVariable(gSentenceCall, groovyCode) ;
			
			if(gVariable!=null){
				//如果是已经存在的一个变量
				//直接添加
				return gVariable;
			}else{
				if(!CompilerUtil.isExpress(groovyCode)){
					CompilerUtil.compilerException("变量："+groovyCode+"未定义！");
					
					//变量未定义，定义一个变量
					GVariable gVar = new GVariable(groovyCode,null);
					return gVar;
				}
				//是一个表达式
				CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));
				
				if(proxy != null){
					//表达式生成一个临时变量
					GCompilerResult result =  proxy.compile(gSentenceCall, groovyCode);
					if(result.getgSentence()!=null){
						GVariable gVariableTmp = new GVariable(CompilerUtil.randomVarName());
						gVariableTmp.addGSentence(result.getgSentence());
						return gVariableTmp;
					}					
				}
			}
		}
		return null;
	}
	
	
}
