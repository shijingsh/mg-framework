package com.mg.groovy.lib.domain.interfaces;

import java.lang.reflect.InvocationTargetException;

public interface GCall {

	public Object call(Object... obj) throws NoSuchMethodException, SecurityException, 
											InstantiationException, IllegalAccessException, 
											IllegalArgumentException, InvocationTargetException;
	
	public Object call() throws NoSuchMethodException, SecurityException, 
								InstantiationException, IllegalAccessException, 
								IllegalArgumentException, InvocationTargetException;	
}
