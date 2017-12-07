package com.mg.groovy.service;

import com.mg.groovy.util.HRMSGroovyResponseBody;

public interface GroovyService {
	/** 
	 * groovy检查语法入口
	 * 只读事物
	 * @author liukefu
	 * @param groovyBean
	 * @return 
	 */
	public HRMSGroovyResponseBody mainCheck(GroovyBean groovyBean);
	/** 
	 * 检查并执行脚本
	 * @author liukefu
	 * @param groovyBean
	 * @return 
	 */
	public HRMSGroovyResponseBody execGroovy(GroovyBean groovyBean) ;
}
