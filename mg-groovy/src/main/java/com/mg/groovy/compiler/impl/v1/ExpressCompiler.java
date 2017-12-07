package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GOperate;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.util.CompilerUtil;
import com.mg.groovy.util.ExpressionUtil;

import java.util.List;

/** 
 * 解析 四则混合运算 
 * 四则混合运算优先于函数调用 MethodCallCompiler
 * 四则混合运算优先于取值、取属性 PointCompiler
 * @author: liukefu
 * @date: 2015年2月6日 下午3:03:03  
 */
public class ExpressCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase gSentenceBase, String groovyCode)
			throws CompilerGroovyException {
		//寻找表达式结束位置
		int blockEndIndex = CompilerUtil.getSentenceEndIndex(groovyCode);		
		String code = groovyCode;
		if(blockEndIndex != -1){
			code = CompilerUtil.substring(code, 0, blockEndIndex);
		}
		//中缀表达式转为后缀表达式
		List<GOperate> list = ExpressionUtil.toSuffixExpression(gSentenceBase,code);
		//后缀表达式转为中缀表达式
		GOperate middleOperate = ExpressionUtil.toMiddleExpression(gSentenceBase,list);

		middleOperate.setParentSentence(gSentenceBase);
		gSentenceBase.addGSentence(middleOperate);

		return new GCompilerResult(middleOperate,code.length());
	}

}
