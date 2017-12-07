package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.lib.domain.GCallMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * 函数调用 语句 
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午4:40:36  
 */
public class GSentenceCall extends GSentenceBase{

	/**
	 * 调用函数名称
	 */
	String gMethodName;
	
	/**
	 * 函数参数列表
	 */
	List<GVariable> paramList = new ArrayList<>();
	
	public void addGParam(GVariable gVariable){
		if(!paramList.contains(gVariable)){
			paramList.add(gVariable);
		}
	}
	
	
	public List<GVariable> getParamList() {
		return paramList;
	}


	public void setParamList(List<GVariable> paramList) {
		this.paramList = paramList;
	}


	public String getgMethodName() {
		return gMethodName;
	}


	public void setgMethodName(String gMethodName) {
		this.gMethodName = gMethodName;
	}

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
	
		if(StringUtils.isNotBlank(gMethodName)){
			//导入函数函数调用
			if(GroovyFun.funMap.get(gMethodName.trim())!=null){
				//系统导入函数
				GCallMethod gcall = (GCallMethod)GroovyFun.funMap.get(gMethodName.trim());
				sb.append(" ").append(gcall.getMethodFullName()).append(".call( ");
			}else{
				//标准的groovy 函数
				sb.append(" ").append(gMethodName).append(" (");
			}
			
			for(int i=0;i<paramList.size();i++){
				GVariable gVar = paramList.get(i);
				String varName = gVar.getVarName();
				if(varName.startsWith("tmp_var_")){
					List<GSentenceBase> gSentenceList = gVar.getgSentenceList();
					if(gSentenceList.size()>0){
						for (GSentenceBase gSentence: gSentenceList){
							sb.append(gSentence.toGroovy());
						}
					}else{
						sb.append(" ").append(gVar.getVarValue());
					}
				}else{
					sb.append(" ").append(gVar.getVarName());
				}
				
				if(i!=paramList.size()-1){
					sb.append(GroovyConstants.gc_comma);
				}
			}
			sb.append(")   ");
		}
		
		return sb.toString();
	}

}
