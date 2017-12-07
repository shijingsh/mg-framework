package com.mg.groovy.lib;

import com.mg.groovy.define.bean.GVariable;
import com.mg.groovy.lib.emp.GroovyEmp;
import com.mg.groovy.lib.emp.GroovyInstanceSeqId;

import java.util.HashMap;
import java.util.Map;

/** 
 * 引用变量 
 * 引用的变量 必须 在执行脚本时候
 * 通过  
 *  Bindings bindings = scriptEngine.createBindings();
 *  bindings.put(“empEntity”, emp);
 * 传入
 * @author: liukefu
 * @date: 2015年2月7日 下午6:23:45  
 */
public class GroovyVariable {

	/**
	 * 定义 groovy 可以引用类库
	 */
	public static Map<Object,GVariable> varMap = new HashMap<>();
	
	static{
		varMap.put(GroovyEmp.name, new GroovyEmp());
		varMap.put(GroovyInstanceSeqId.name, new GroovyInstanceSeqId());
	}
	
	public static Map<String,String> varChinaMap = new HashMap<>();
	static{
		varChinaMap.put("员工", GroovyEmp.name);
		varChinaMap.put("实类ID", GroovyInstanceSeqId.name);
		varChinaMap.put("岗位", GroovyInstanceSeqId.name);
		varChinaMap.put("基本工资", GroovyInstanceSeqId.name);
	}
}
