package com.mg.groovy.service.impl;

import com.mg.groovy.lib.GroovyChecks;
import com.mg.groovy.service.GroovyBean;
import com.mg.groovy.service.GroovyCheckService;
import com.mg.groovy.service.GroovyService;
import com.mg.groovy.util.HRMSGroovyResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * GroovyServiceImpl 
 * 
 * @author: liukefu
 * @date: 2015年4月16日 上午11:11:21  
 */
@Service
public class GroovyServiceImpl implements GroovyService {

	@Autowired
	private GroovyMainCheckServiceImpl groovyMainCheckService;
	
	/** 
	 * groovy检查语法入口
	 * 只读事物
	 * @author liukefu
	 * @param groovyBean
	 * @return 
	 */
	@Override
	@Transactional(readOnly=true)
	public HRMSGroovyResponseBody mainCheck(GroovyBean groovyBean) {
		//扩展的check实现类
		for(GroovyCheckService checkService: GroovyChecks.checkServices){
			checkService.check(groovyBean);
		}
		//调用主体实现
		return groovyMainCheckService.check(groovyBean);
	}

	/** 
	 * 检查并执行脚本
	 * @author liukefu
	 * @param groovyBean
	 * @return 
	 */
	@Transactional
	public HRMSGroovyResponseBody execGroovy(GroovyBean groovyBean) {
		//扩展的check实现类
		for(GroovyCheckService checkService:GroovyChecks.checkServices){
			checkService.check(groovyBean);
		}
		//调用主体实现
		HRMSGroovyResponseBody body =  groovyMainCheckService.check(groovyBean);
		
		return body;
	}
}
