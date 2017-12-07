package com.mg.groovy.define.interfaces;

import com.mg.groovy.define.bean.GSentenceBase;

import java.util.List;

/** 
 * 导入包 接口
 * 
 * @author: liukefu
 * @date: 2015年3月13日 上午10:59:40  
 */
public interface GImport extends Groovy{

	/** 
	 * 获取groovy 脚本依赖的包
	 * @author liukefu
	 * @return
	 */
	List<GSentenceBase> getImports();
	
	/** 
	 * 添加groovy 脚本依赖的包
	 * @author liukefu
	 * @param lib
	 */
	void addImports(GSentenceBase lib);
}
