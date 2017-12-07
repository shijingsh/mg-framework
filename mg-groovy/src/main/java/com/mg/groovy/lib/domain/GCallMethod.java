package com.mg.groovy.lib.domain;

import com.mg.groovy.lib.domain.interfaces.GCall;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.NullObject;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCallMethod implements GCall {
	/**
	 * 函数分类名称
	 */
	private String typeName;
	/**
	 * 函数完整名称
	 */
	private String methodFullName ;

	/**
	 * 函数名称
	 */
	private String methodName;
	/**
	 * 函数参数是动态长度
	 */
	private boolean isDynamicParam;
	
	/**
	 * 函数是否可见
	 */
	private boolean isVisible;
	/**
	 * 函数所在类
	 */
	private Class<?> clzCall;
	/**
	 * 函数参数列表
	 */
	private Class<?> parameterTypes[];
	
	/**
	 * 函数返回类型
	 */
	private Class<?> returnTypeName;
		
	public GCallMethod(Class<?> clzCall,String typeName, String methodName,boolean isDynamicParam,boolean isVisible,
			Class<?>[] parameterTypes,Class<?> returnTypeName) {
		super();
		this.clzCall = clzCall;
		this.typeName = typeName;
		this.methodName = methodName;
		this.isDynamicParam = isDynamicParam;
		this.isVisible = isVisible;
		this.parameterTypes = parameterTypes;
		this.returnTypeName = returnTypeName;
		this.methodFullName = this.clzCall.getSimpleName()+'_'+this.methodName;
	}

	@Override
	public Object call(Object... param) throws NoSuchMethodException, SecurityException, 
											InstantiationException, IllegalAccessException, 
											IllegalArgumentException, InvocationTargetException {
		if(param==null){
			param = new Object[1];
		}
		Class<?> paramTypes[] = new Class<?>[param.length];
		for(int i=0;i<param.length;i++){
			Object p = param[i];
			Class<?> pClass = null;
			if(p==null){
				 pClass = NullObject.class;
			}else{
				 pClass = p.getClass();
			}
			if(Number.class.isAssignableFrom(pClass) && !BigDecimal.class.isAssignableFrom(pClass)){				
				Number pNum = (Number)p;
				//转化参数值为 BigDecimal
				param[i] = convertNumberToBigDecimal(pClass,pNum);
				//转化参数类型为 BigDecimal
				pClass = BigDecimal.class;
			}
			paramTypes[i]= pClass;
		}
		
		Object objInstance = clzCall.newInstance();
		GMatchedMethod matchedMethod = getCallMethod(paramTypes);
		Method method = matchedMethod.getMethod();
		if(GMatchedMethod.MATCHED_RELATIVE == matchedMethod.getMatchedType()){
			Class<?> targetParamTypes[] = method.getParameterTypes();
			Object paramObj = Array.newInstance(targetParamTypes[0].getComponentType(),paramTypes.length);
			int i =0;
			for(Object pm:param){
				Array.set(paramObj, i++, pm);
			}
			//不定长度参数调用
			return method.invoke(objInstance, new Object[] {paramObj});
		}
		
		return method.invoke(objInstance, param);
	}

	@Override
	public Object call() throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		
		Method method = clzCall.getMethod(methodName);
		
		Object objInstance = clzCall.newInstance();
		
		return method.invoke(objInstance);
	}
	
	private BigDecimal convertNumberToBigDecimal(Class<?> pClass,Number pNum){
		if(Integer.class.isAssignableFrom(pClass)){
			return new BigDecimal((Integer)pNum);
		}else if(Float.class.isAssignableFrom(pClass)){
			return new BigDecimal((Float)pNum);
		}else if(Double.class.isAssignableFrom(pClass)){
			return new BigDecimal((Double)pNum);
		}else if(Long.class.isAssignableFrom(pClass)){
			return new BigDecimal((Long)pNum);
		}else if(Byte.class.isAssignableFrom(pClass)){
			return new BigDecimal((Byte)pNum);
		}
		
		return null;
	}
	
	/**
	 * 寻找与参数匹配的函数定义
	 * @param paramTypes
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private GMatchedMethod getCallMethod(Class<?> paramTypes[]) throws NoSuchMethodException, SecurityException{
	
		List<Method> list = getNamedMethods();
		GMatchedMethod method =  getMatchedMethod(list,paramTypes);

		return method;
	}
	
	/**
	 * 获取同名的函数列表
	 * @return
	 */
	public List<Method> getNamedMethods(){
		List<Method> list = new ArrayList<>();
		
		Method[] methods = clzCall.getMethods();
		for(Method method:methods){
			if(method.getName().equals(methodName)){
				list.add(method);
			}
		}
		return list;
	}
	
	/**
	 * 寻找与参数匹配的函数定义
	 * @param list
	 * @param paramTypes
	 * @return
	 */
	public GMatchedMethod getMatchedMethod(List<Method> list,Class<?> paramTypes[]){
		GMatchedMethod methodMatched = new GMatchedMethod();
		for(Method method:list){
			Class<?> methodParamTyps[] = method.getParameterTypes();
			if(isMethodMathedDynamicParam(methodParamTyps, paramTypes)){
				//动态长度参数的函数
				methodMatched.setMatched(true);
				methodMatched.setMatchedType(GMatchedMethod.MATCHED_RELATIVE);
				methodMatched.setMethod(method);
				break;
			}else if(isMethodMathedParam(methodParamTyps,paramTypes)){
				//固定长度参数的函数
				methodMatched.setMatched(true);
				methodMatched.setMethod(method);
				break;
			}else if(isMethodNearMathedParam(methodParamTyps, paramTypes)){
				//固定长度参数的函数
				methodMatched.setMatched(true);
				methodMatched.setMethod(method);
				//这里没有 break
				//当这个函数近似匹配时，仍然继续找完全匹配的函数
			}
		}
		
		return methodMatched;
	}
	
	/**
	 * 寻找与参数匹配的函数定义
	 * @param methodParamTyps
	 * @param paramTypes
	 * @return
	 */
	public boolean isMethodMathedParam(Class<?> methodParamTyps[],Class<?> paramTypes[]){
	
		//参数长度相同
		if(paramTypes.length == methodParamTyps.length){
			
			for(int i=0;i<paramTypes.length;i++){
				Class<?> methodParamType = methodParamTyps[i];
				Class<?> paramType = paramTypes[i];
				if(!NullObject.class.isAssignableFrom(paramType) 
						&& !StringUtils.equals(paramType.getName(), methodParamType.getName())){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}

	/**
	 * 寻找与参数近似匹配的函数定义
	 * @param methodParamTyps
	 * @param paramTypes
	 * @return
	 */
	public boolean isMethodNearMathedParam(Class<?> methodParamTyps[],Class<?> paramTypes[]){

		//参数长度相同
		if(paramTypes.length == methodParamTyps.length){

			for(int i=0;i<paramTypes.length;i++){
				Class<?> methodParamType = methodParamTyps[i];
				Class<?> paramType = paramTypes[i];
				if(!NullObject.class.isAssignableFrom(paramType)
						&& !StringUtils.equals(paramType.getName(), methodParamType.getName())){
					//判断是否是父类
					if(!methodParamType.isAssignableFrom(paramType)){
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}
	/**
	 * 是否匹配动态长度参数
	 * @param methodParamTyps
	 * @param paramTypes
	 * @return
	 */
	public boolean isMethodMathedDynamicParam(Class<?> methodParamTyps[],Class<?> paramTypes[]){
		//动态长度的函数参数，都是对象数组，所以长度为1
		if(methodParamTyps.length!=1)return false;
		
		Class<?> methodParamType = methodParamTyps[0];
		if(methodParamType.isArray()){
			Class<?> realType = methodParamType.getComponentType();
			for(Class<?> paramType:paramTypes){
				if(!NullObject.class.isAssignableFrom(paramType)
						&& !StringUtils.equals(paramType.getClass().getName(), realType.getClass().getName())){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public Class<?> getClzCall() {
		return clzCall;
	}

	public void setClzCall(Class<?> clzCall) {
		this.clzCall = clzCall;
	}

	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String getMethodFullName() {
		return methodFullName;
	}

	public void setMethodFullName(String methodFullName) {
		this.methodFullName = methodFullName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Class<?> getReturnTypeName() {
		return returnTypeName;
	}

	public void setReturnTypeName(Class<?> returnTypeName) {
		this.returnTypeName = returnTypeName;
	}

	public boolean isDynamicParam() {
		return isDynamicParam;
	}

	public void setDynamicParam(boolean isDynamicParam) {
		this.isDynamicParam = isDynamicParam;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clzCall == null) ? 0 : clzCall.getName().hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(parameterTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GCallMethod)) {
			return false;
		}
		GCallMethod other = (GCallMethod) obj;
		if (clzCall == null) {
			if (other.clzCall != null) {
				return false;
			}
		} else if (!clzCall.getName().equals(other.clzCall.getName())) {
			return false;
		}
		if (methodName == null) {
			if (other.methodName != null) {
				return false;
			}
		} else if (!methodName.equals(other.methodName)) {
			return false;
		}
		if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
			return false;
		}
		return true;
	}


	
}
