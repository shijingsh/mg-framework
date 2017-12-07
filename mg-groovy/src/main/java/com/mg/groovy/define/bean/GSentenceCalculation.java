package com.mg.groovy.define.bean;

import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.util.CompilerUtil;
import com.mg.groovy.util.ExpressionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/** 
 * 四则运算 表达式  
 * 
 * @author: liukefu
 * @date: 2015年2月4日 下午4:21:51  
 */
public class GSentenceCalculation extends GSentenceBase{
	
    List<GOperate> opList = new ArrayList<>();	
    List<String> opVariableList = new ArrayList<>();
    
    List<GSentenceCalculation> childCalList = new ArrayList<>();
    List<GOperate> childCalOpList = new ArrayList<>();
    
	public void addOperate(GOperate operate){
		if(operate != null){
			opList.add(operate);
		}
	}
    
	public void addOpVariable(String opVariable){
		if(opVariable!=null){
			opVariableList.add(opVariable);
		}
	}
	
	public void addOperateList(List<GOperate> oplist){
		if(oplist!=null && oplist.size()>0){
			this.opList.addAll(oplist);
		}
	}
    
	public void addOpVariableList(List<String> variableList){
		if(variableList!=null && variableList.size()>0){
			opVariableList.addAll(variableList);
		}
	}
	
	public List<GOperate> getOpList() {
		return opList;
	}

	public List<String> getOpVariableList() {
		return opVariableList;
	}
	public void addChildCalculationOp(GOperate op){
		childCalOpList.add(op);
	}
	public void addChildCalculation(GSentenceCalculation calculation){
		childCalList.add(calculation);
	}
	public void addChildCalculationOpList(List<GOperate> childoplist) {
		if(childoplist!=null && childoplist.size()>0){
			this.childCalOpList.addAll(childoplist);
		}
	}
	public void addChildCalculationList(List<GSentenceCalculation> childlist){
		if(childlist!=null && childlist.size()>0){
			this.childCalList.addAll(childlist);
		}
	}
	
	
	public List<GSentenceCalculation> getChildCalList() {
		return childCalList;
	}

	public List<GOperate> getChildCalOpList() {
		return childCalOpList;
	}

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		
		//转换 语句
		String upOperater = null;
		for(int i=0;i<opVariableList.size();i++){
			String opVariable = opVariableList.get(i);
			if(opVariable!=null){
				if(i!=0){
					String op = opList.get(i-1).getOperate();
					sb.append(op);
					upOperater =  op;
				}
				
				sb.append(opVariable);
				String opNext = "";
				if(upOperater!=null && i<opList.size()){
					opNext = opList.get(i).getOperate();
				}
				if(StringUtils.isNotBlank(upOperater) 
						&& isLowerOperate(upOperater,opNext) 
						&& !isInBrackets(sb.toString()) 
						&& i != opVariableList.size()-1){
					sb.insert(0, GroovyConstants.gc_brackets_small);
					sb.append(GroovyConstants.gc_brackets_small_end);
				}				
			}
		}	
		if(StringUtils.isNotBlank(sb.toString()) && !isInBrackets(sb.toString())){
			sb.insert(0, GroovyConstants.gc_brackets_small).append(GroovyConstants.gc_brackets_small_end);
		}
		//子表达式语句
		int childOp = 0;
		StringBuilder sbChild = new StringBuilder();
		for(GSentenceCalculation child:childCalList){
			if(childCalOpList.size()>childOp){
				sbChild.append(childCalOpList.get(childOp).getOperate());
				addOPerate(sbChild,child.toGroovy());
			}else{
				CompilerUtil.compilerException("缺少操作数");
			}
		}
		String childCode = sbChild.toString();

		if(StringUtils.isNotBlank(childCode)){
			char afterMethodChar = CompilerUtil.getOneOperateChar(childCode, 0);
			if(!ExpressionUtil.isOperate(String.valueOf(afterMethodChar)) && !isInBrackets(sbChild.toString())){
				//函数后面是个四则运算符的话，则不作为函数处理
				sbChild.insert(0, GroovyConstants.gc_brackets_small).append(GroovyConstants.gc_brackets_small_end);
			}			
		}
		sb.append(sbChild);
		return sb.toString();
	}
	
	public boolean isLowerOperate(String op,String opNext){
		if(StringUtils.isBlank(op) || StringUtils.isBlank(opNext)){
			return false;
		}
		
		if(GroovyConstants.getOperatePriority(op)>GroovyConstants.getOperatePriority(opNext)){
			return true;
		}

		return false;
	}
	
	/** 
	 * 是否包含在括号中
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public boolean isInBrackets(String code){
		
        if(StringUtils.isBlank(code)){
        	return false;
        }
        
        if(code.startsWith(GroovyConstants.gc_brackets_small) && code.endsWith(GroovyConstants.gc_brackets_small_end)){
        	return true;
        }
        
		return false;
	}
	
	public void addOPerate(StringBuilder sentence,String code){
		
        if(sentence == null || StringUtils.isBlank(code)){
        	return;
        }
        String beforeCode = sentence.toString();
        if(!isInBrackets(beforeCode)){
        	sentence.append(code);
        }
	}
}
