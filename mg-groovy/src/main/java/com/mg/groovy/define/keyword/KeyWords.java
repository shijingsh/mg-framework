package com.mg.groovy.define.keyword;

import com.mg.groovy.compiler.impl.v1.*;

import java.util.HashMap;
import java.util.Map;

/** 
 * KeyWords 
 * 
 * @author: liukefu
 * @date: 2015年2月6日 上午9:58:23  
 */
public class KeyWords {

	/**
	 * 定义 groovy class关键字
	 */
	public static final String kw_class = "类型";
	/**
	 * 定义 groovy if 条件判断语句关键字
	 */
	public static final String kw_if = "if";
	/**
	 * 定义 groovy for 循环语句关键字
	 */
	public static final String kw_for = "循环";
	/**
	 * 定义 groovy 变量关键字
	 */
	public static final String kw_def = "定义";	
	/**
	 * 定义 groovy 不编译的代码段
	 */
	public static final String kw_asm = "__asm";
	/**
	 * 定义 groovy import 导入类库关键字
	 */
	public static final String kw_import = "import";
	/**
	 * 定义 groovy 返回
	 */
	public static final String kw_return = "return";
	/**
	 * 定义 groovy 关键字集合
	 */
	public static Map<Object,String> keyMap = new HashMap<>();
	
	static{
		keyMap.put(kw_class, kw_class);
		keyMap.put(kw_if, kw_if);
		keyMap.put(kw_for, kw_for);
		keyMap.put(kw_def, kw_def);
		keyMap.put(kw_asm, kw_asm);
		keyMap.put(kw_import, kw_import);
		keyMap.put(kw_return, kw_return);
	}
	
	/** 
	 * 判断字符是不是关键字
	 * @author liukefu
	 * @param key
	 * @return
	 */
	public static boolean isKeyWord(String key){
		
		return keyMap.get(key)!=null;
	}
	
	public static final String compiler_body = "body";
	
	public static final String compiler_express = "express";
	/**
	 * 定义 groovy 关键字集合
	 */
	public static Map<String,Class<?>> keyCompilerMap = new HashMap<>();
	
	static{
		//主编译器	编译主循环
		keyCompilerMap.put(compiler_body, BodyCompiler.class);
		//表达式编译器
		keyCompilerMap.put(compiler_express, ExpressCompiler.class);		
		//关键字编译器
		keyCompilerMap.put(kw_class,  DefCompiler.class);
		keyCompilerMap.put(kw_if, IfCompiler.class);
		keyCompilerMap.put(kw_for, DefCompiler.class);
		keyCompilerMap.put(kw_def, DefCompiler.class);
		keyCompilerMap.put(kw_asm, AsmCompiler.class);
		keyCompilerMap.put(kw_import, ImportCompiler.class);
		keyCompilerMap.put(kw_return, ReturnCompiler.class);
		//常量编译器
		//遇到gc_giv 触发赋值编译器
		keyCompilerMap.put(GroovyConstants.gc_giv, GivCompiler.class); 			
		//遇到gc_brackets_small 触发函数编译器
		keyCompilerMap.put(GroovyConstants.gc_brackets_small, MethodCallCompiler.class); 
		//四则混合运算 编译器
		keyCompilerMap.put(GroovyConstants.gc_cal_add, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_subtract, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_multiply, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_divide, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_model, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_double_add, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_cal_double_subtract, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_semicolon, EndSentenceCompiler.class); 
		//取属性运算
		keyCompilerMap.put(GroovyConstants.gc_fetch_point, ExpressCompiler.class); 
		//关系运算符
		keyCompilerMap.put(GroovyConstants.gc_gt, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_lt, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_ge, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_le, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_eq, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_ne, ExpressCompiler.class); 
		//逻辑运算
		keyCompilerMap.put(GroovyConstants.gc_and,ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_or, ExpressCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_not, ExpressCompiler.class); 
		//语法检查类
		keyCompilerMap.put(GroovyConstants.gc_brackets_small_end, EndSmallBracketsCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_brackets_big_end, EndBigBracketsCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_brackets_mid_end, EndMidBracketsCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_brackets_big, BigBracketsCompiler.class); 
		keyCompilerMap.put(GroovyConstants.gc_brackets_mid, MidBracketsCompiler.class); 
	}
	
	
	/**
	 * 定义 groovy 编译器的优先级
	 */
	public static Map<String,Integer> priorityMap = new HashMap<>();
	/**
	 * 数值越小优先级越高
	 */
	static{		

		
		priorityMap.put(GivCompiler.class.getName(), 1);
		
		priorityMap.put(ExpressCompiler.class.getName(), 5);
				
		priorityMap.put(MethodCallCompiler.class.getName(), 10);
		
		priorityMap.put(EndSentenceCompiler.class.getName(), 15);		
		
		priorityMap.put(EndSmallBracketsCompiler.class.getName(), 20);
		priorityMap.put(EndBigBracketsCompiler.class.getName(), 20);
		priorityMap.put(EndMidBracketsCompiler.class.getName(), 20);
		priorityMap.put(MidBracketsCompiler.class.getName(), 20);
		priorityMap.put(BigBracketsCompiler.class.getName(), 20);
	}
	
	/** 
	 * 获取编译器的优先级
	 * @author liukefu
	 * @param key
	 * @return
	 */
	public static Integer getCompilerPriority(String key){
		
		return priorityMap.get(key)==null?-1:priorityMap.get(key);
	}

	/**
	 * 比较target编译器 是否比src优先级高
	 * @param src
	 * @param target
	 * @return
	 */
	public static boolean isPriority(String src,String target){
		Class<?> srcProx = keyCompilerMap.get(src);
		Class<?> targetProx = keyCompilerMap.get(target);
		int srcPriority = getCompilerPriority(srcProx.getName());
		int targetPriority = getCompilerPriority(targetProx.getName());
		
		return targetPriority<srcPriority;
	}
}
