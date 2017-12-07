package com.mg.groovy.define.keyword;

import java.util.HashMap;
import java.util.Map;

public class GroovyConstants {
	/**
	 * 定义 groovy 赋值关键字
	 */
	public static final String gc_giv = "=";
	
	/**
	 * 定义 groovy " " 空格
	 */
	public static final String gc_blank = " ";
	
	/**
	 * 定义 groovy "," 逗号
	 */
	public static final String gc_comma = ",";
	/**
	 * 定义 groovy “;” 分号
	 */
	public static final String gc_semicolon = ";";
	
	/**
	 * 定义 groovy “"” 引号
	 */
	public static final String gc_quotes = "\"";	
	
	/**
	 * 定义 groovy "(" 小括号
	 */
	public static final String gc_brackets_small = "(";
	
	/**
	 * 定义 groovy "{" 大括号
	 */
	public static final String gc_brackets_big = "{";
	
	/**
	 * 定义 groovy ")" 小括号
	 */
	public static final String gc_brackets_small_end = ")";
	
	/**
	 * 定义 groovy "}" 大括号
	 */
	public static final String gc_brackets_big_end = "}";
	
	/**
	 * 定义 groovy "[" 大括号
	 */
	public static final String gc_brackets_mid = "[";
	/**
	 * 定义 groovy "]" 大括号
	 */
	public static final String gc_brackets_mid_end = "]";
	/**
	 * 定义 groovy "+" 加
	 */
	public static final String gc_cal_add = "+";	
	/**
	 * 定义 groovy "-" 减
	 */
	public static final String gc_cal_subtract = "-";	
	/**
	 * 定义 groovy "*" 乘
	 */
	public static final String gc_cal_multiply = "*";	
	/**
	 * 定义 groovy "/" 除
	 */
	public static final String gc_cal_divide = "/";
	/**
	 * 定义 groovy "%" 求模
	 */
	public static final String gc_cal_model = "%";
	
	/**
	 * 定义 groovy "." 取属性
	 */
	public static final String gc_fetch_point = ".";
	
	/**
	 * 定义 groovy "++" 加加
	 */
	public static final String gc_cal_double_add = "++";	
	/**
	 * 定义 groovy "--" 减减
	 */
	public static final String gc_cal_double_subtract = "--";	
	//---------------------------逻辑运算符---------------------------
	/**
	 * 定义 groovy ">" 大于
	 */
	public static final String gc_gt = ">";
	
	/**
	 * 定义 groovy "<" 小于
	 */
	public static final String gc_lt = "<";
	
	/**
	 * 定义 groovy ">=" 大于等于
	 */
	public static final String gc_ge = ">=";
	
	/**
	 * 定义 groovy "<=" 小于等于
	 */
	public static final String gc_le = "<=";
	
	/**
	 * 定义 groovy "==" 等于
	 */
	public static final String gc_eq = "==";
	/**
	 * 定义 groovy "!=" 等于
	 */
	public static final String gc_ne = "!=";
	/**
	 * 定义 groovy "&&" 与
	 */
	public static final String gc_and = "&&";
	
	/**
	 * 定义 groovy "||" 或
	 */
	public static final String gc_or = "||";
	/**
	 * 定义 groovy "!" 取反
	 */
	public static final String gc_not = "!";
	//---------------------------其他被使用的字符---------------------------
	/**
	 * 定义 groovy 四则混合运算 结束符
	 */
	public static final String gc_express_end = "#";
	
	public static Map<Object,String> keyMap = new HashMap<>();
	
	static{
		keyMap.put(gc_giv, gc_giv);
		keyMap.put(gc_blank, gc_blank);
		keyMap.put(gc_semicolon, gc_semicolon);
		keyMap.put(gc_quotes, gc_quotes);
		keyMap.put(gc_brackets_small,gc_brackets_small);
		keyMap.put(gc_brackets_big, gc_brackets_big);
		keyMap.put(gc_cal_add, gc_cal_add);
		keyMap.put(gc_cal_subtract, gc_cal_subtract);
		keyMap.put(gc_cal_multiply, gc_cal_multiply);
		keyMap.put(gc_cal_divide, gc_cal_divide);
		keyMap.put(gc_cal_model, gc_cal_model);
		keyMap.put(gc_fetch_point, gc_fetch_point);
		
		keyMap.put(gc_gt, gc_gt);
		keyMap.put(gc_lt, gc_lt);
		keyMap.put(gc_ge, gc_ge);
		keyMap.put(gc_le, gc_le);
		keyMap.put(gc_eq, gc_eq);
		keyMap.put(gc_ne, gc_ne);
		keyMap.put(gc_and, gc_and);
		keyMap.put(gc_or, gc_or);
		keyMap.put(gc_not, gc_not);
		
		keyMap.put(gc_brackets_small_end, gc_brackets_small_end);
		keyMap.put(gc_brackets_big_end, gc_brackets_big_end);
		keyMap.put(gc_brackets_mid_end, gc_brackets_mid_end);
		keyMap.put(gc_brackets_big, gc_brackets_big);
		keyMap.put(gc_brackets_mid, gc_brackets_mid);
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
	
	/**
	 * 定义 groovy 操作符的优先级
	 */
	public static Map<Object,Integer> priorityMap = new HashMap<>();
	/**
	 * 数值越小优先级越高
	 */
	static{		
		priorityMap.put(gc_brackets_small, 1);
		priorityMap.put(gc_brackets_small_end, 1);
		
		priorityMap.put(gc_fetch_point, 2);
		
		priorityMap.put(gc_not, 4);
		
		priorityMap.put(gc_cal_multiply, 6);
		priorityMap.put(gc_cal_divide, 6);
		priorityMap.put(gc_cal_model,6);
		
		priorityMap.put(gc_cal_add, 9);
		priorityMap.put(gc_cal_subtract,9);
		
		priorityMap.put(gc_gt, 15);
		priorityMap.put(gc_ge, 15);
		priorityMap.put(gc_lt, 15);
		priorityMap.put(gc_le, 15);
		
		priorityMap.put(gc_eq, 16);
		priorityMap.put(gc_ne, 16);
		
		priorityMap.put(gc_and, 17);
		priorityMap.put(gc_or, 18);	
		
		
	}
	
	/** 
	 * 获取操作符的优先级
	 * @author liukefu
	 * @param key
	 * @return
	 */
	public static Integer getOperatePriority(String key){
		
		return priorityMap.get(key)==null?-1:priorityMap.get(key);
	}
	
	/** 
	 * 比较target 是否比src优先级高
	 * @author liukefu
	 * @param key
	 * @return
	 */
	public static boolean isPriority(String src,String target){
		int srcPriority = getOperatePriority(src);
		int targetPriority = getOperatePriority(target);
		
		return targetPriority<srcPriority;
	}
	
	/** 
	 * 判断是否是单目运算符
	 * @author liukefu
	 * @param key
	 * @return
	 */
	public static boolean isSimpleOperateChar(String op){
		     
		switch (op) {  
        case GroovyConstants.gc_cal_double_add:    
        case GroovyConstants.gc_cal_double_subtract:  
        	return true;   	
        } 
        
        return false;
	}
}
