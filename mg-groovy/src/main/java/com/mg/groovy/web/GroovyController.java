package com.mg.groovy.web;

import com.alibaba.fastjson.JSON;

import com.mg.framework.utils.JsonResponse;
import com.mg.framework.utils.WebUtil;
import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.lib.domain.GFunctionBean;
import com.mg.groovy.lib.domain.GFunctionTypeBean;
import com.mg.groovy.service.GroovyBean;
import com.mg.groovy.service.GroovyService;
import com.mg.groovy.util.HRMSGroovyResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 
 * GroovyController 
 * 
 * @author: liukefu
 * @date: 2015年4月15日 上午10:05:36  
 */
@Controller
@RequestMapping(produces = "application/json; charset=UTF-8")
public class GroovyController {
	protected Logger logger = LoggerFactory.getLogger(GroovyController.class);
	@Autowired
	private GroovyService groovyService;
	/** 
	 * 获取引擎内置函数表
	 * @author liukefu
	 * @return
	 */
	@RequestMapping(value="/groovy/functions")
	@ResponseBody
	public String getFunctions(){
		//函数分类
		Map<String,List<GFunctionBean>> funTypeMap = GroovyFun.funTypeMap;
		
		List<GFunctionTypeBean> list = new ArrayList<>();
		
		Iterator<String> iterator = funTypeMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			List<GFunctionBean> funList = funTypeMap.get(key);
			//过滤不可见的部分
			List<GFunctionBean> visibleFunList = GroovyFun.getVisibleFunctionBeanList(funList);
			if(visibleFunList.size()>0){
				GFunctionTypeBean type = new GFunctionTypeBean(key,visibleFunList);
				list.add(type);
			}
		}
		
        return JsonResponse.success(list, null);
	}
	
	/** 
	 * check 函数表达式
	 * @author liukefu
	 * @return
	 */
	@RequestMapping(value="/groovy/check")
	@ResponseBody
	public String checkGroovy(HttpServletRequest req){
        String jsonString = WebUtil.getJsonBody(req);
        GroovyBean groovyBean = JSON.parseObject(jsonString, GroovyBean.class);
		
		HRMSGroovyResponseBody body = groovyService.mainCheck(groovyBean);
		
		return JsonResponse.entityToJson(body);
	}
	
	/** 
	 * 执行groovy脚本接口
	 * @author liukefu
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/groovy/exec")
	@ResponseBody
	public String execGroovy(HttpServletRequest req){
        String jsonString = WebUtil.getJsonBody(req);
        GroovyBean groovyBean = JSON.parseObject(jsonString, GroovyBean.class);

		HRMSGroovyResponseBody body = groovyService.execGroovy(groovyBean);
		
		return JsonResponse.entityToJson(body);
	}
}
