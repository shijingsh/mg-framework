package com.mg.groovy.service;

import com.mg.groovy.util.HRMSGroovyResponseBody;

/** 
 * 检查、运行groovy脚本接口 
 * 这个接口将在引擎check之前执行
 * @author: liukefu
 * @date: 2015年4月16日 上午9:45:01  
 */
public interface GroovyCheckService {

	/** 
	 * 检查代码，返回检查结果
	 * 返回
	 * 	<li>错误标志</li>
	 * 	<li>运行结果</li>
	 * 	<li>编译结果</li>
	 * @author liukefu
	 * @param groovyBean 代码
	 * @return
	 */
	public HRMSGroovyResponseBody check(GroovyBean groovyBean);
}
