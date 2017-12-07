package com.mg.groovy.lib.domain.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({METHOD})
@Retention(RUNTIME)
public @interface GFunction {
	/** 
	 * 函数分类名称
	 * @author liukefu
	 * @return
	 */
	String typeName() default "";
	/** 
	 * 是否隐藏函数
	 * @author liukefu
	 * @return
	 */
	boolean isVisible() default true;
	/** 
	 * 函数api说明文件路径
	 * @author liukefu
	 * @return
	 */
	String notesUrl() default "/public/groovy/api/index.html";
	/** 
	 * 是否可变参数个数
	 * @author liukefu
	 * @return
	 */
	boolean isDynamicParam() default false;
}
