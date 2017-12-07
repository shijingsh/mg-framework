package com.mg.groovy.extended;


import groovy.lang.DelegatingMetaClass;
import org.codehaus.groovy.runtime.NullObject;

public class NullObjectMetaClass extends DelegatingMetaClass {
	
	public NullObjectMetaClass(Class<NullObject> theClass){  
        super(theClass)  ;
    }  
  
	public Object invokeMethod(Object object, String methodName, Object[] arguments){  
		if(object==null)return null;
		if(object instanceof NullObject)return null;
		
		return super.invokeMethod(object, methodName, arguments);
    }   
}
