package com.mg.groovy.compiler.exception;

import org.apache.commons.lang3.StringUtils;

/** 
 * Groovy编译异常 
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:38:46  
 */
public class CompilerGroovyException extends RuntimeException {

	private static final long serialVersionUID = -9061520702856293648L;

	private String message;
	
	public CompilerGroovyException(){
		super();
	}
	
	/** 
	 * 编译异常信息
	 * @param message 
	 */
	public CompilerGroovyException(String message) {
		super(message);
		setMessage(message);
	}
	
	/** 
	 * 编译异常，包含编译语句上下文
	 * @param message
	 * @param context 
	 */
	public CompilerGroovyException(String message,String context) {
		StringBuilder sb = new StringBuilder();
		sb.append(message);

		if(StringUtils.isNotBlank(context)){
			sb.append("\n\r");
			sb.append("脚本[");
			if(context.length()>50){
				context = context.substring(0,50);
			}
			sb.append(context);
			sb.append("]");
		}

		setMessage(sb.toString());
	}
	
	public CompilerGroovyException(String message, Throwable cause) {
		super(message, cause);
		setMessage(message);
	}
	
	public CompilerGroovyException(Throwable cause){
		super(cause);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
