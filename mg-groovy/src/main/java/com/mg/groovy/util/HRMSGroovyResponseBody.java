package com.mg.groovy.util;



public class HRMSGroovyResponseBody<T>{
	public static int SUCCESS = 0;
	public static int ERROR = 1;
	//错误代码
	private int errorCode;
	//错误提示
	private String errorText;

	//成功时的提示
	private String successText;

	//返回对象
	private T data;
	/**
	 * groovy 编译后代码
	 */
	public String groovyCode;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getSuccessText() {
		return successText;
	}

	public void setSuccessText(String successText) {
		this.successText = successText;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getGroovyCode() {
		return groovyCode;
	}

	public void setGroovyCode(String groovyCode) {
		this.groovyCode = groovyCode;
	}

}
