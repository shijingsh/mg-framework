package com.mg.groovy.compiler.impl.v1;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GSentencePoint;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.util.CompilerUtil;
import com.mg.groovy.util.ExpressionUtil;
import org.apache.commons.lang3.StringUtils;

/** 
 * “.”取值、取属性编译器 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:43:21  
 */
public class PointCompiler implements CompilerProxy{

	@Override
	public GCompilerResult compile(GSentenceBase sentence, String groovyCode) throws CompilerGroovyException{
		
		char [] cArr = groovyCode.toCharArray();
		//一个点出现的位置
		int firstPoint = CompilerUtil.indexOf(groovyCode, GroovyConstants.gc_fetch_point, 0);
		if(firstPoint==-1){
			return new GCompilerResult(null);
		}
		int startIndex = 0;//记录编译的位置
		int endIndex = -1;
		for (int i=0;i<cArr.length;i++){
			char c = cArr[i];
			if(i<startIndex){
				continue;
			}
						
			if(GroovyConstants.isKeyWord(String.valueOf(c)) ){
				//寻找点操作语句的结束位置
				if(!GroovyConstants.gc_fetch_point.equals(String.valueOf(c)) && i> firstPoint){
					endIndex = i;
					break;
				}
				//寻找点操作语句的开始位置
				if(!GroovyConstants.gc_fetch_point.equals(String.valueOf(c)) && startIndex ==0 && i< firstPoint){
					startIndex = i;
				}				
			}
		}
		if(endIndex==-1){
			endIndex=groovyCode.length();
		}
		char afterMethodChar = CompilerUtil.getOneOperateChar(groovyCode,endIndex);
		if(ExpressionUtil.isOperate(String.valueOf(afterMethodChar))
				//下一个操作符比当前操作符优先级高
				&& GroovyConstants.isPriority(GroovyConstants.gc_fetch_point,String.valueOf(afterMethodChar))){
			//函数后面是个四则运算符的话，则不作为函数处理
			return new GCompilerResult(null,endIndex,false);
		}
		String code = CompilerUtil.substring(groovyCode, startIndex, endIndex);
		String arr[] = CompilerUtil.split(code, GroovyConstants.gc_fetch_point);
		
		GSentencePoint point = new GSentencePoint();
		for(String str:arr){
			if(StringUtils.isNotBlank(str)){
				//添加操作数
				str = str.trim();
				
				point.addOperate(new GSentencePoint(str));
			}
		}
		sentence.addGSentence(point);
		point.setParentSentence(sentence);
		
		return new GCompilerResult(point,endIndex);
	}

}
