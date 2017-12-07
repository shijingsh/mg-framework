package com.mg.groovy.define.interfaces;


import com.mg.groovy.define.bean.GClass;
import com.mg.groovy.define.bean.GMethod;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GVariable;

/**
 * groovy 表达式语言
 * 	包括：
 * 		类型定义
 * 		函数定义
 * 		变量定义
 * 		赋值
 * 		函数调用
 * 		判断
 * 		循环
 * @author: liukefu
 * @date: 2015年2月4日 上午11:30:07  
 */
public interface GLanguage extends Groovy {

	/** 
	 * 添加自定义类型
	 * @author liukefu
	 * @param gClass
	 */
	public void addGClass(GClass gClass);
	
	/** 
	 * 添加自定义函数
	 * @author liukefu
	 * @param gMethod
	 */
	public void addGMethod(GMethod gMethod);
	/** 
	 * 添加语句
	 * @author liukefu
	 * @param gSentenceBase
	 */
	public void addGSentence(GSentenceBase gSentenceBase);
	
	/** 
	 * 添加变量
	 * @author liukefu
	 * @param gVariable
	 */
	public void addGVariable(GVariable gVariable);
	
	/** 
	 * 获取上层语句
	 * @author liukefu
	 * @return
	 */
	public GSentenceBase getParentSentence();
	
	/** 
	 * 设置上层语句
	 * @author liukefu
	 * @return
	 */
	public void setParentSentence(GSentenceBase parentSentence);
}
