package com.mg.groovy.util;

import com.mg.groovy.compiler.impl.v1.GroovyCompilerImpl;
import com.mg.groovy.define.bean.GCompilerResult;
import com.mg.groovy.extended.BigDecimalMetaClass;
import com.mg.groovy.extended.IntegerMetaClass;
import com.mg.groovy.extended.NullObjectMetaClass;
import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.lib.domain.GCallMethod;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

public class ScriptEngineUtil {
	
	 private static Logger logger = LoggerFactory.getLogger(ScriptEngineUtil.class);
	
	 public static Object execGroovyScript(String script,Map<String,Object> param) throws ScriptException{
		
         ScriptEngineManager factory = new ScriptEngineManager();
         ScriptEngine scriptEngine = factory.getEngineByName("groovy");
         Bindings bindings = scriptEngine.createBindings();
         if(param!=null){
        	 //绑定参数
        	 bindingParams(bindings, param);
        	 //绑定内置函数库	
        	 bindingFunParams(bindings, GroovyFun.funMap);
         }
         try {
			 logger.debug("groovy compile code : {}", script);
			 GroovyCompilerImpl complier = new GroovyCompilerImpl();
			 GCompilerResult compilerResult = complier.compile(script);
			 String groovyCode = compilerResult.getgSentence().toGroovy();
        	 logger.debug("groovy code exec: {}", groovyCode);
        	 setMetaClass();
        	 Object result = scriptEngine.eval(groovyCode, bindings);
             logger.debug("run script result: {}", result);
             return result;
         } catch (ScriptException e) {
             logger.error(e.getMessage(), e);
             throw e;
         }         
	 }
	 
	 private static void bindingFunParams(Bindings bindings,Map<String,GCallMethod> param){
		 if(param==null || param.isEmpty())return;
		 
    	 Iterator<String> iterator =  param.keySet().iterator();
    	 while(iterator.hasNext()){
    		 String key = iterator.next();
    		 GCallMethod gcall = param.get(key);
    		 logger.debug("binding funtion:{}", key);
    		 if(StringUtils.isNotBlank(key)){
    			 //注意：映射为完整函数名	防止重名
    			 bindings.put(gcall.getMethodFullName(), gcall);
    		 }        		 
    	 }
	 }
	 
	 private static void setMetaClass(){
    	 //全局处理NullObject NullObject的任何操作都返回null
		 NullObjectMetaClass metaClass = new NullObjectMetaClass(NullObject.class);
		 metaClass.initialize();
		 InvokerHelper.metaRegistry.setMetaClass(NullObject.class,metaClass)  ;		 
    	 //全局处理BigDecimal
		 BigDecimalMetaClass bigDecimalMetaClass = new BigDecimalMetaClass(BigDecimal.class);
		 bigDecimalMetaClass.initialize();
		 InvokerHelper.metaRegistry.setMetaClass(BigDecimal.class,bigDecimalMetaClass);
		//全局处理Integer
		 IntegerMetaClass intergerMetaClass = new IntegerMetaClass(Integer.class);
		 intergerMetaClass.initialize();
		 InvokerHelper.metaRegistry.setMetaClass(Integer.class,intergerMetaClass);
	 }
	 
	 private static void bindingParams(Bindings bindings,Map<String,?> param){
		 if(param==null || param.isEmpty())return;
		 
    	 Iterator<String> iterator =  param.keySet().iterator();
    	 while(iterator.hasNext()){
    		 String key = iterator.next();
    		 Object value = param.get(key);
    		 if(StringUtils.isNotBlank(key)){
    			 bindings.put(key, value);
    		 }        		 
    	 }
	 }
}
