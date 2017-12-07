package com.mg.groovy.define.bean;

/** 
 * 编译结果 
 * 
 * @author: liukefu
 * @date: 2015年2月6日 下午12:01:37  
 */
public class GCompilerResult {

	/**
	 * 编译产生语句
	 */
	GSentenceBase gSentence;
	
	/**
	 * 编译指针到达的位置
	 */
	int endIndex = 0;
	/**
	 * 是否已经编译
	 */
	boolean isCompile = true;

	public GCompilerResult(GSentenceBase gSentence, int endIndex) {
		super();
		this.gSentence = gSentence;
		this.endIndex = endIndex;
	}

	public GCompilerResult(GSentenceBase gSentence, int endIndex,
			boolean isCompile) {
		super();
		this.gSentence = gSentence;
		this.endIndex = endIndex;
		this.isCompile = isCompile;
	}

	public GCompilerResult(GSentenceBase gSentence) {
		super();
		this.gSentence = gSentence;
	}

	public GSentenceBase getgSentence() {
		return gSentence;
	}

	public void setgSentence(GSentenceBase gSentence) {
		this.gSentence = gSentence;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public boolean isCompile() {
		return isCompile;
	}

	public void setCompile(boolean isCompile) {
		this.isCompile = isCompile;
	}	
}
