package com.mg.groovy.service.impl;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.compiler.impl.v1.GroovyCompilerImpl;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.service.GroovyBean;
import com.mg.groovy.service.GroovyCheckService;
import com.mg.groovy.util.HRMSGroovyResponseBody;
import com.mg.groovy.util.ScriptEngineUtil;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.Map;

/** 
 * groovy 引擎check 
 * 
 * @author: liukefu
 * @date: 2015年4月16日 上午9:48:33  
 */
@Service
public class GroovyMainCheckServiceImpl implements GroovyCheckService {

	/** 
	 * 检查脚本、试运行脚本
	 * @author liukefu
	 * @param groovyBean
	 * @return 
	 */
	@Override
	public HRMSGroovyResponseBody check(GroovyBean groovyBean) {
		HRMSGroovyResponseBody body = new HRMSGroovyResponseBody();
		GCompilerResult result = null;
		try{
			result = new GroovyCompilerImpl().compile(groovyBean.getGroovyCode());
		}catch(CompilerGroovyException ce){
			body.setErrorText(ce.getMessage());
			body.setErrorCode(HRMSGroovyResponseBody.ERROR);
			return body;
		}
		
		if(result!=null && result.getgSentence()!=null){
			String groovyCode = groovyBean.getGroovyCode();
			
	    	Object data = null;
			try {
				if(groovyBean.isNeedRunning()){
					Map<String,Object> param = groovyBean.getBindings();
					data = ScriptEngineUtil.execGroovyScript(groovyCode, param);
				}
			} catch (ScriptException e) {
				e.printStackTrace();
				body.setErrorText("运行脚本发生错误");//e.getMessage()
				body.setErrorCode(HRMSGroovyResponseBody.ERROR);
				return body;
			}
			body.setErrorCode(HRMSGroovyResponseBody.SUCCESS);
			body.setData(data);
			body.setGroovyCode(groovyCode);
			return body;
		}
		
		body.setErrorCode(HRMSGroovyResponseBody.SUCCESS);
		body.setGroovyCode(groovyBean.getGroovyCode());		
		return body;
	}
}
