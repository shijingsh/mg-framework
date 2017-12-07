package com.mg.groovy.define.bean;

import com.mg.groovy.define.interfaces.GLanguage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * 表达式 基类
 *  
 * 每种语句 都预置可以嵌套 其他语句
 * @author: liukefu
 * @date: 2015年2月4日 下午5:49:09  
 */
public abstract class GSentenceBase implements GLanguage {

	/**
	 * 上层级语句
	 */
	GSentenceBase parentSentence;
	
	/**
	 * 自定义类型列表
	 */
	List<GClass> clazList = new ArrayList<>();
		
	/**
	 * 自定义函数列表
	 */
	List<GMethod> methodList = new ArrayList<>();
	/**
	 * 语 列表
	 */
	List<GSentenceBase> gSentenceList = new ArrayList<>();
	
	/**
	 * 变量在表达式中  varList 只是为了找变量的定义而用
	 * 输出的时候 是和 GSentenceBase 一样。
	 * 变量列表
	 */
	List<GVariable> varList = new ArrayList<>();
	
	public void addGClass(GClass gClass){
		if(!clazList.contains(gClass)){
			clazList.add(gClass);
		}
	}
	public void addGMethod(GMethod gMethod){
		if(!methodList.contains(gMethod)){
			methodList.add(gMethod);
		}
	}
	public void addGSentence(GSentenceBase gSentenceBase){
		gSentenceList.add(gSentenceBase);
	}
	
	public void addGVariable(GVariable gVariable){
		if(gVariable.isNew){
			//重复定义变量 只有第一个定义 有效
			if(!varList.contains(gVariable)){
				varList.add(gVariable);
				addGSentence(gVariable);
			}
		}else{
			varList.add(gVariable);
			addGSentence(gVariable);
		}
	}
		
	public GSentenceBase getParentSentence() {
		return parentSentence;
	}
	
	public void setParentSentence(GSentenceBase parentSentence) {
		this.parentSentence = parentSentence;
	}
	
	public GClass getGClassByName(String gClassName){
		if(StringUtils.isBlank(gClassName))return null;
		
		for(GClass gClass:clazList){
			if(gClassName.equals(gClass.getgGClassName())){
				return gClass;
			}			
		}
		//找不到 则向级找....
		if(parentSentence != null){
			return parentSentence.getGClassByName(gClassName);
		}
		
		return null;
	}
	public GMethod getGMethodByName(String gMethodName){
		if(StringUtils.isBlank(gMethodName))return null;
		
		for(GMethod gMethod:methodList){
			if(gMethodName.equals(gMethod.getgMethodName())){
				return gMethod;
			}
		}
		//找不到 则向级找....
		if(parentSentence != null){
			return parentSentence.getGMethodByName(gMethodName);
		}

		return null;
	}
	
	public GVariable getGVariableByName(String gVariableName){
		
		if(StringUtils.isBlank(gVariableName))return null;
		
		for(GVariable gVariable:varList){
			if(gVariableName.equals(gVariable.getVarName())){
				return gVariable;
			}
		}
		//找不到 则向级找....
		if(parentSentence != null){
			return parentSentence.getGVariableByName(gVariableName);
		}
		
		return null;		
	}
	
	public List<GClass> getClazList() {
		return clazList;
	}
	public void setClazList(List<GClass> clazList) {
		this.clazList = clazList;
	}
	public List<GMethod> getMethodList() {
		return methodList;
	}
	public void setMethodList(List<GMethod> methodList) {
		this.methodList = methodList;
	}
	public List<GSentenceBase> getgSentenceList() {
		return gSentenceList;
	}
	public void setgSentenceList(List<GSentenceBase> gSentenceList) {
		this.gSentenceList = gSentenceList;
	}
	public List<GVariable> getVarList() {
		return varList;
	}
	public void setVarList(List<GVariable> varList) {
		this.varList = varList;
	}
	@Override
	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		//先转换类型定义
		for(GClass gClass:clazList){
			sb.append(gClass.toGroovy());
		}
		//转换函数定义
		for(GMethod gMethod:methodList){
			sb.append(gMethod.toGroovy());
		}
/*		//转换变量定义    
		for(GVariable gVariable:varList){
			sb.append(gVariable.toGroovy());
		}*/			
		//转换 语句
		for(GSentenceBase gSentenceBase:gSentenceList){
			sb.append(gSentenceBase.toGroovy());
		}	
		
		return sb.toString();
	}

}
