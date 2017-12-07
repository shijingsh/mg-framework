package com.mg.groovy.lib;

import com.mg.framework.log.ContextLookup;
import com.mg.groovy.service.GroovyCheckService;

import java.util.*;

/**
 *  groovy check 扩展入口
 */
public class GroovyChecks {

	/**
	 * 定义 groovy check list
	 */
	public static List<GroovyCheckService> checkServices = new ArrayList<>();

	//安装check 接口
	static{
		GroovyChecks.install("groovySalaryCheckServiceImpl");
		GroovyChecks.install("groovyPerformanceCheckServiceImpl");
	}
	public static void install(Class checkClass){
		GroovyCheckService checkService = (GroovyCheckService) ContextLookup.getBean(checkClass);
		checkServices.add(checkService);
	}

	public static void install(String checkClassName){
		try {
			GroovyCheckService checkService = (GroovyCheckService) ContextLookup.getBean(checkClassName);
			if(checkService!=null) {
				checkServices.add(checkService);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
