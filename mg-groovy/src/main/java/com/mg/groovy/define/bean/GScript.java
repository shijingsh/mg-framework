package com.mg.groovy.define.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * groovy 脚本 
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午2:49:08  
 */
public class GScript extends GSentenceBase{

	private List<GSentenceBase> imports = new ArrayList<>();
	
	public GScript() {
		super();
/*		imports.add(new GSentenceSimple("import java.math.*;") );
		imports.add(new GSentenceSimple("import java.util.*;") );
		imports.add(new GSentenceSimple("import com.mg.common.*;") );
		imports.add(new GSentenceSimple("import com.mg.util.*;") );*/
	}
	
	public void addImports(GSentenceBase lib) {
		imports.add(lib);
	}

	public List<GSentenceBase> getImports() {
		
		return imports;
	}
	
	/** 
	 * 获取最后一个非GSentenceEnd的语句
	 * 用于作为默认添加return语句
	 * @author liukefu
	 * @return
	 */
	public GSentenceBase getLastGSentence(){
		List<GSentenceBase> gSentenceList = super.getgSentenceList();
		
		for(int i=gSentenceList.size()-1;i>=0;i--){
			GSentenceBase gSentence = gSentenceList.get(i);
			if(gSentence instanceof GSentenceEnd || gSentence instanceof GSentenceSimple){
				continue;
			}
			return gSentence;
		}
		return null;
	}
	
	public int getLastGSentenceIndex(){
		List<GSentenceBase> gSentenceList = super.getgSentenceList();
		
		for(int i=gSentenceList.size()-1;i>=0;i--){
			GSentenceBase gSentence = gSentenceList.get(i);
			if(gSentence instanceof GSentenceEnd || gSentence instanceof GSentenceSimple){
				continue;
			}
			return i;
		}
		return -1;
	}
	
	/** 
	 * 获取return 语句
	 * @author liukefu
	 * @return
	 */
	public GSentenceBase getReturnGSentence(){
		List<GSentenceBase> gSentenceList = super.getgSentenceList();
		
		for(GSentenceBase gSentenceBase: gSentenceList){			
			if(gSentenceBase instanceof GSentenceReturn){
				return gSentenceBase;
			}			
		}
		
		return null;
	}
	
	@Override
	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		for(GSentenceBase imp:imports){
			sb.append(imp.toGroovy());
		}
		sb.append(super.toGroovy());
		
		return sb.toString();
	}
}
