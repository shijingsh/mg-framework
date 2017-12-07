package com.mg.groovy.lib;

import com.mg.groovy.lib.date.GroovyDateUtils;
import com.mg.groovy.lib.domain.GCallMethod;
import com.mg.groovy.lib.domain.GFunctionBean;
import com.mg.groovy.lib.domain.interfaces.GBaseFunction;
import com.mg.groovy.lib.domain.interfaces.GFunction;
import com.mg.groovy.lib.math.GroovyMathUtils;
import com.mg.groovy.util.CloneFilter;
import com.mg.groovy.util.HRMSBeanClone;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class GroovyFun {

	/**
	 * 定义 groovy 可以引用类库
	 */
	public static Map<String,GCallMethod> funMap = new HashMap<>();
	/**
	 * 定义 groovy 可以函数库分类
	 */
	public static Map<String,List<GFunctionBean>> funTypeMap = new HashMap<>();
	static{
		initClassFunctions(GroovyDateUtils.class);
		initClassFunctions(GroovyMathUtils.class);
	}
	
	/** 
	 * 读取类的方法列表，导入库
	 * @author liukefu
	 * @param clazz
	 */
	public static void initClassFunctions(Class<?> clazz){
		Method[] methods = clazz.getMethods();
		String funTypeName = null;
		if(clazz!=null && GBaseFunction.class.isAssignableFrom(clazz)){
			try {
				GBaseFunction gBaseFunction = (GBaseFunction)clazz.newInstance();
				funTypeName = gBaseFunction.getFunTypeName();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		for(Method method:methods){
			GFunction annotation = method.getAnnotation(GFunction.class);
			 if(annotation != null){
				 Class<?> clzCall = clazz;			
				 String methodName = method.getName();
				 Class<?> parameterTypes[] = method.getParameterTypes();
				 Class<?> returnTypeName = method.getReturnType();
				 boolean isDynamicParam = annotation.isDynamicParam();
				 boolean isVisible = annotation.isVisible();
				 //函数分类名称
				 String typeName = funTypeName;
				 if(StringUtils.isNotBlank(annotation.typeName())){
					 //直接注解分类名称的优先
					 typeName =annotation.typeName();
				 }
				 GCallMethod gCall = new GCallMethod(clzCall,typeName,methodName,isDynamicParam,isVisible,parameterTypes,returnTypeName);
				 String notesUrl = annotation.notesUrl();
				 //添加函数类型定义
				 addFuntionType(typeName,methodName,notesUrl,gCall);
				 //添加函数定义
				 addFuntion(gCall,methodName);
			 }
		}
	}
	
	/** 
	 * 函数加入库
	 * @author liukefu
	 * @param gCall
	 * @param methodName
	 */
	private static void addFuntion(GCallMethod gCall,String methodName){
		 //函数名默认是全部小写的
		 funMap.put(methodName, gCall);
		 //转大写 函数名称全部大写、全部小写是等价的
		 funMap.put(methodName.toUpperCase(), gCall);
		 //转大写 函数名称全部大写、全部小写是等价的
		 funMap.put(methodName.toLowerCase(), gCall);
	}

	/**
	 * 添加函数类型定义
	 * @param typeName
	 * @param methodName
	 * @param notesUrl
	 * @param gCallMethod
	 */
	private static void addFuntionType(String typeName,String methodName,String notesUrl, GCallMethod gCallMethod){

		 if(funTypeMap.get(typeName)!=null){
			 GFunctionBean funBean = new GFunctionBean(typeName,methodName,notesUrl,gCallMethod);
			 List<GFunctionBean> list = funTypeMap.get(typeName);
			 if(!isContainFunction(methodName)){
				 list.add(funBean);
			 }else{
				 //方法重载，记录重载参数列表
				 GFunctionBean fBean = getFunctionBean(typeName,methodName);
				 fBean.getMethodList().add(gCallMethod);
			 }
		 }else{
			 List<GFunctionBean> list = new ArrayList<>();
			 if(!isContainFunction(methodName)){
				 GFunctionBean funBean = new GFunctionBean(typeName,methodName,notesUrl,gCallMethod);
				 list.add(funBean);
			 }	
			 funTypeMap.put(typeName, list);
		 }	
	}
	
	/** 
	 * 库中是否包含函数
	 * @author liukefu
	 * @param methodName
	 * @return
	 */
	private static boolean isContainFunction(String methodName){
		if(funMap.get(methodName) != null 
				|| funMap.get(methodName.toUpperCase()) != null
				|| funMap.get(methodName.toLowerCase()) != null){
			
			return true;
		}
		
		return false;
	}
	
	/** 
	 * 查找 对应的GFunctionBean
	 * @author liukefu
	 * @param methodName
	 * @param typeName
	 * @return
	 */
	private static GFunctionBean getFunctionBean(String typeName,String methodName){
		List<GFunctionBean> list = funTypeMap.get(typeName);
		if(list!=null){
			for(GFunctionBean funBean:list){
				if(funBean.getMethodName().equalsIgnoreCase(methodName)){
					return funBean;
				}
			}
		}
		
		return null;
	}
	
	/** 
	 * 根据函数名字查找 对应的GFunctionBean
	 * @author liukefu
	 * @param methodName
	 * @return
	 */
	public static GFunctionBean getFunctionBean(String methodName){
		if(methodName != null){
			methodName = methodName.trim();
		}
		Iterator<String> it = funTypeMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			GFunctionBean funBean = getFunctionBean(key,methodName);
			if(funBean!=null){
				return funBean;
			}
		}
		
		return null;
	}
	
	public static List<GFunctionBean> getVisibleFunctionBeanList(List<GFunctionBean> funList){
		List<GFunctionBean> visibleFunList = new ArrayList<>();
		for(GFunctionBean funBean:funList){
			List<GCallMethod> gCallList = funBean.getMethodList();
			List<GCallMethod> visibleCallList = new ArrayList<>();
			for(GCallMethod gMeth:gCallList){
				if(gMeth.isVisible()){
					visibleCallList.add(gMeth);
				}
			}
			if(visibleCallList.size()>0){
				List<CloneFilter> filterList = new ArrayList<>();
		    	filterList.add(new CloneFilter(GFunctionBean.class,"methodList"));
				try {
					GFunctionBean cloneBean = (GFunctionBean) HRMSBeanClone.deepClone(funBean, filterList);
					cloneBean.setMethodList(visibleCallList);
					visibleFunList.add(cloneBean);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return visibleFunList;
	}
	public static void main(String args[]){
		GCallMethod gCall = funMap.get("today");
		try {
			gCall.call();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
