package com.mg.groovy.lib.domain;

import java.lang.reflect.Method;

/**
 * 与调用参数匹配的函数定义
 * @author liukefu
 *
 */
public class GMatchedMethod {
	
	/**
	 * 绝对匹配
	 */
	public static int MATCHED_ABSOLUTE = 0;
	
	/**
	 * 相对匹配
	 */
	public static int MATCHED_RELATIVE = 1;
	
	boolean matched = false;
	
	int matchedType = MATCHED_ABSOLUTE;
	
	Method method;

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public int getMatchedType() {
		return matchedType;
	}

	public void setMatchedType(int matchedType) {
		this.matchedType = matchedType;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	
}
