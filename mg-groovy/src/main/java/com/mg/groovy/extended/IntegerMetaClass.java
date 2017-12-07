package com.mg.groovy.extended;

import groovy.lang.DelegatingMetaClass;
import org.codehaus.groovy.runtime.NullObject;


public class IntegerMetaClass extends DelegatingMetaClass{  
	
	public IntegerMetaClass(Class<?> theClass){  
        super(theClass)  ;
    }  
  
	public Object invokeMethod(Object object, String methodName, Object[] arguments){  
		if(object==null)return null;
		if(object instanceof NullObject)return null;
		if("div".equals(methodName) 
				|| "multiply".equals(methodName)
				|| "plus".equals(methodName)
				|| "minus".equals(methodName)
				|| "mod".equals(methodName)){
			if(arguments[0] == null || arguments[0] instanceof NullObject){
				return null;
			}
		}
		return super.invokeMethod(object, methodName, arguments);
    }   
}
