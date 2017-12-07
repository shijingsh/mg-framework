package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.lib.GroovyVariable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * 员工.姓名  类似语句  
 * 
 * @author: liukefu
 * @date: 2015年2月7日 下午6:44:27  
 */
public class GSentencePoint extends GSentenceBase{

	String name;
	
	List<GSentencePoint> opList = new ArrayList<>();
	
	public void addOperate(GSentencePoint operate){
		if(operate != null){
			opList.add(operate);
		}
	}
    	
	public GSentencePoint(String name) {
		super();
		this.name = name;
	}
	public GSentencePoint() {
		super();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		//点操作 在这里用实际的变量替换掉
		if(StringUtils.isNotBlank(name)){
			sb.append(name);
		}		
		int i = 0;
		for(GSentencePoint op:opList){
			if(i!=0){
				sb.append(GroovyConstants.gc_fetch_point);
			}
			//翻译中文
			String englishName = "";
			if(StringUtils.isNotBlank(op.getName())){
				englishName = GroovyVariable.varChinaMap.get(op.getName());
			}			
			if(StringUtils.isNotBlank(englishName)){
				sb.append(englishName);
			}else{
				sb.append(op.toGroovy());
			}
			i++;
		}
		
		return sb.toString();
	}

}
