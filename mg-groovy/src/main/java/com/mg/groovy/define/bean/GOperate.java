package com.mg.groovy.define.bean;


/** 
 * Groovy四则混合运算  操作数 
 * 
 * @author: liukefu
 * @date: 2015年3月11日 上午11:19:35  
 */
public class GOperate extends GSentenceCalculation {

	/**
	 * 优先级
	 */
	public int priority = 0;
	/**
	 * 普通操作数 、操作数是语句
	 */
	public String operate;
	/**
	 * 单目运算
	 */
	private boolean isSimple = false;
	/**
	 * 单目运算，操作符在操作数之前
	 */
	private boolean isBefore = false;
	
	public GOperate(GSentenceBase parentSentence,String opVariable) {
		super();
		this.parentSentence = parentSentence;
		this.operate = opVariable;
		this.addOpVariable(opVariable);
	}
	
	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isBefore() {
		return isBefore;
	}

	public void setBefore(boolean isBefore) {
		this.isBefore = isBefore;
	}
	
	public boolean isSimple() {
		return isSimple;
	}

	public void setSimple(boolean isSimple) {
		this.isSimple = isSimple;
	}

	@Override
	public String toGroovy() {
		
		return super.toGroovy();
	}
}
