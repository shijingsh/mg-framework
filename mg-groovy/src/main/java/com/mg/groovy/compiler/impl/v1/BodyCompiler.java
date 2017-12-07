package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GVariable;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import com.mg.groovy.util.CompilerUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;

/** 
 * 主编译器 
 * 根据
 * 	1.关键字
 *  2.特殊字符
 *  调用不同的编译器
 * @author: liukefu
 * @date: 2015年3月13日 上午10:40:30  
 */
public class BodyCompiler implements CompilerProxy {
	
	public GCompilerResult compile(GSentenceBase gSentence,String groovyCode) throws ServiceException {
		
		char [] cArr = groovyCode.toCharArray();
		
		int startIndex = 0;		//记录循环的位置
		int compileEndIndex = 0;//编译完成的位置
		GCompilerResult result = null;//编译结果
		StringBuilder operate = new StringBuilder();//操作数		
		for (int i=0;i<cArr.length;i++){
			char c = cArr[i];
			if(i<=compileEndIndex - 1){
				i = compileEndIndex -1;
				continue;
			}
			if(i<=startIndex - 1){
				i = startIndex -1;
				continue;
			}			
			if(GroovyConstants.isKeyWord(String.valueOf(c))){
				StringBuilder op = new StringBuilder();//操作符
				op.append(String.valueOf(c));
				CompilerProxy constantsProxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(op.toString()));
				
				if(constantsProxy!=null){
					//判断是否存在双目运算符
					char nextChar = CompilerUtil.getOneOperateChar(groovyCode, i + 1);
					if(GroovyConstants.isKeyWord(String.valueOf(nextChar)) && StringUtils.isNotBlank(String.valueOf(nextChar))){
						//双元操作符
						op.append(String.valueOf(nextChar));
						startIndex = startIndex + 1;
						CompilerProxy doubleCompilerProx = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(op.toString()));
						if(doubleCompilerProx != null){
							constantsProxy = doubleCompilerProx;
						}
					}
					//如果定义了常量编译器 则调用常量编译器
					String code = CompilerUtil.substring(groovyCode, compileEndIndex, groovyCode.length());

					result = constantsProxy.compile(gSentence, code);
					if(result.getEndIndex() > 0){
						startIndex += result.getEndIndex();
						operate.append(CompilerUtil.substring(code, i, startIndex));
					}
					//清空操作数
					if(result.isCompile()){
						//编译位置前移
						if(result.getEndIndex() > 0){
							compileEndIndex += result.getEndIndex();
						}
						if(startIndex>compileEndIndex){
							startIndex = compileEndIndex;
						}
						operate.delete(0, operate.length());
					}
				}
			}else{
				operate.append(c);
			}
			
			if(KeyWords.isKeyWord(operate.toString().trim())){
				String code = CompilerUtil.substring(groovyCode, compileEndIndex, groovyCode.length());
				
				result = compileKeyWord(gSentence,operate.toString(),code);
				if(result.getEndIndex() > 0){
					startIndex += result.getEndIndex();
					operate.append(CompilerUtil.substring(code, i, startIndex));
				}
				//清空操作数
				if(result.isCompile()){
					//编译位置前移
					if(result.getEndIndex() > 0){
						compileEndIndex += result.getEndIndex();
					}
					operate.delete(0, operate.length());
				}				
			}
		}
		
		if(result==null && operate.length()>0){
			//未解析的部分生成一个临时变量
			GVariable gVariableTmp = new GVariable(CompilerUtil.randomVarName());
			gVariableTmp.setVarValue(operate.toString());
			
			return new GCompilerResult(gVariableTmp,groovyCode.length());
		}
		return result;
	}
	
	public GCompilerResult compileKeyWord(GSentenceBase gSentence,String keyWord,String groovyCode) throws ServiceException {
		CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(keyWord.trim()));
		
		if(proxy!=null){
			return proxy.compile(gSentence, groovyCode);
		}
				
		return new GCompilerResult(null,0);
	}
}
