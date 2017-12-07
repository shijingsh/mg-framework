package com.mg.groovy.util;

import com.mg.groovy.compiler.impl.GroovyCompilerFactory;
import com.mg.groovy.define.bean.*;
import com.mg.groovy.define.interfaces.CompilerProxy;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.define.keyword.KeyWords;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ExpressionUtil {
	
	public static void main(String arg[]) {
        //String s = "(ab+aa)*ac-ad/ae";
		
		//String b = "(1+min(2,3+4*5)*max(abs(6),7))*8+9";
		//String b = "(a1+(b1+a2))*(b3*c+a3)+(a4+b4*c2)";
		//String b = "3+4*5";
		//String b = "min(2,3+4*5)*max(abs(6),7)+1*8+9";
		String b = " (7200 * 1.06 * 146.3135) / 100 ";
        GVariable gVariable = new GVariable(CompilerUtil.randomVarName());
        System.out.println("---------------toSuffixExpression-------------------");
        List<GOperate> postfix = toSuffixExpression(gVariable,b);
        for (int i = 0, len = postfix.size(); i < len; i++) {
        	//System.out.println(postfix.get(i).getType());
            //System.out.println(postfix.get(i).getOperate());
            System.out.println(postfix.get(i).toGroovy());
        }
        System.out.println("----------------toMiddleExpression------------------");
        
        GOperate middleOperate = toMiddleExpression(gVariable,postfix);
        System.out.println(middleOperate.getOperate());
        System.out.println(middleOperate.toGroovy());
    }
	/** 
	 * 判断是否函数调用语句
	 * @author liukefu
	 * @param op 操作数
	 * @param c	 操作符号
	 * @return
	 */
	public static boolean isMethodCall(String op,char c){
		if(StringUtils.isNotBlank(op.trim()) && GroovyConstants.gc_brackets_small.equals(String.valueOf(c))){
			return true;
		}
		
		return false;
	}
	
    /** 
     * 将中缀表达式转换成后缀表达式
     * @author liukefu
     * @param gSentenceBase
     * @param prefix
     * @return
     */
    public static List<GOperate> toSuffixExpression(GSentenceBase gSentenceBase,String prefix) {
    	//用字符数组保存前缀表达式
        int len = prefix.length();
        //让前缀表达式以'#'结尾
        prefix=prefix+ GroovyConstants.gc_express_end;
        //保存操作符的栈
        Stack<GOperate> stack = new Stack<GOperate>();
        //首先让'#'入栈
        stack.push(new GOperate(gSentenceBase,GroovyConstants.gc_express_end));
        List<GOperate> postfix = new ArrayList<>();
        //保存后缀表达式的列表,可能是数字，也可能是操作符，之前使用的是ArrayList
        StringBuilder operate = new StringBuilder();
        int bracketsNum = 0;
        for (int i = 0; i < len + 1; i++) {
        	char c = prefix.charAt(i);
        	//判断是否是函数
        	if (isMethodCall(operate.toString(),c)){
        		//找到函数的结尾
        		int matchCharIndex = CompilerUtil.getMatchedChar(prefix,GroovyConstants.gc_brackets_small,GroovyConstants.gc_brackets_small_end,i);
        		if(matchCharIndex!=-1){
            		//整个函数调用语句作为一个操作数
            		operate.append(prefix.subSequence(i, matchCharIndex+1));
            		i = matchCharIndex;
        		}     	 
            }else if (!isOperate(String.valueOf(c)) ){// 当前字符不是运算符
            	operate.append(c);           	

            } else {
            	//当前字符是一个操作符
				StringBuilder op = new StringBuilder();//操作符
				op.append(String.valueOf(c));
				//下一个操作符
        		char nextChar = CompilerUtil.getOneOperateChar(prefix, i+1);
        		if(isOperate(String.valueOf(c) +String.valueOf(nextChar)) 
        				&& StringUtils.isNotBlank(String.valueOf(nextChar)) ){
        			//双字符操作符
        			op.append(nextChar);
        			i++;
        		}
    			if(operate.length()>0 && StringUtils.isNotBlank(operate.toString().trim())){
    				postfix.add(new GOperate(gSentenceBase,operate.toString()));
    			}
                switch (op.toString()) {
                case GroovyConstants.gc_brackets_small:// 如果是开括号
                    stack.push(new GOperate(gSentenceBase,op.toString()));// 开括号只是放入到栈中，不放入到后缀表达式中
                    bracketsNum++;
                    break;
                case GroovyConstants.gc_brackets_small_end:// 如果是闭括号
                    while (!stack.empty() && ! GroovyConstants.gc_brackets_small.equals(stack.peek().getOperate())) {
                    	GOperate gOperate = stack.pop();
                    	if(GroovyConstants.gc_express_end.equals(gOperate.getOperate())){
                    		CompilerUtil.compilerException("语法错误，缺少匹配的字符"+GroovyConstants.gc_brackets_small_end+"！",prefix,CompilerUtil.ERROR_SYSTEM);
                    	}else{
                    		postfix.add(gOperate);// 闭括号是不入栈的
                    	}                        
                    }
                    if(!stack.empty())
                    stack.pop();// 弹出'('
                    bracketsNum--;
                    break;
                default:
                	// 默认情况下:+ - * /
                    while ( !stack.empty() &&! GroovyConstants.gc_express_end.equals(stack.peek().getOperate())
                            && compare(stack.peek(), op.toString())) {
                    	// 不断弹栈，直到当前的操作符的优先级高于栈顶操作符
                        postfix.add(stack.pop());
                    }
                    //如果当前的操作符不是'#'(结束符)，那么入操作符栈  .最后的标识符'#'是不入栈的
                    if (!StringUtils.equals(op.toString(), GroovyConstants.gc_express_end)) {
                    	GOperate gOperate = new GOperate(gSentenceBase,op.toString() );
                    	stack.push(gOperate);
                    	if(GroovyConstants.isSimpleOperateChar(op.toString())){
                    		if(StringUtils.isBlank(operate)){
	                    		//前单目运算
	                    		gOperate.setSimple(true);
	                    		gOperate.setBefore(true);
                    		}else{
                    			//下一个操作数
                             	char nextOperate = CompilerUtil.getOneOperate(prefix, i+1);
                            	if(nextOperate==0){
                            		//后单目运算
                            		gOperate.setSimple(true);
                            		gOperate.setBefore(false);
                            	}
                    		}   
                    	}
                    }
                    break;
                }
                
                operate.delete(0, operate.length());
            }
        }
        
        if(bracketsNum>0){
        	CompilerUtil.compilerException("语法错误，缺少匹配的字符"+GroovyConstants.gc_brackets_small+"！",prefix,CompilerUtil.ERROR_SYSTEM);
        }else if(bracketsNum<0){
        	CompilerUtil.compilerException("语法错误，缺少匹配的字符"+GroovyConstants.gc_brackets_small_end+"！",prefix,CompilerUtil.ERROR_SYSTEM);
        }
        return postfix;
    }

	/** 
	 * 是否为算术运算的操作符
	 * @author liukefu
	 * @param operate
	 * @return
	 */
	public static  boolean isOperate(String operate){
        switch (operate) {  
        case GroovyConstants.gc_cal_add:    
        case GroovyConstants.gc_cal_subtract:    
        case GroovyConstants.gc_cal_model:           	
        case GroovyConstants.gc_cal_multiply:    
        case GroovyConstants.gc_cal_divide: 
        case GroovyConstants.gc_brackets_small:
        case GroovyConstants.gc_brackets_small_end:
        case GroovyConstants.gc_express_end:
        case GroovyConstants.gc_gt:
        case GroovyConstants.gc_lt:	
        case GroovyConstants.gc_ge:
        case GroovyConstants.gc_le:
        case GroovyConstants.gc_eq:
        case GroovyConstants.gc_ne:
        case GroovyConstants.gc_and:
        case GroovyConstants.gc_or:
        case GroovyConstants.gc_not:
        case "&":
        case "|":
        case "=":
        case GroovyConstants.gc_cal_double_add:
        case GroovyConstants.gc_cal_double_subtract:
        //case GroovyConstants.gc_fetch_point:
        	return true;   	
        } 
        
        return false;
	}
    /** 
     * 比较运算符之间的优先级
     * @author liukefu
     * @param ope
     * @param cur
     * @return
     */
    public static boolean compare(GOperate ope, String cur) {
    	//如果是peek优先级高于cur，返回true，默认都是peek优先级要低
    	String peek = ope.getOperate();
/*        if ("*".equals(peek)
                && (cur == '+' || cur == '-' || cur == '/' || cur == '*')) {// 如果cur是'('，那么cur的优先级高,如果是')'，是在上面处理
            return true;
        } else if ( "/".equals(peek) 
                && (cur == '+' || cur == '-' || cur == '*' || cur == '/')) {
            return true;
        } else if ("+".equals(peek) && (cur == '+' || cur == '-')) {
            return true;
        } else if ( "-".equals(peek) && (cur == '+' || cur == '-')) {
            return true;
        } else if (cur == '#') {// 这个很特别，这里说明到了中缀表达式的结尾，那么就要弹出操作符栈中的所有操作符到后缀表达式中
            return true;// 当cur为'#'时，cur的优先级算是最低的
        }*/
    	int peekPririty = GroovyConstants.getOperatePriority(peek);
    	int curPririty = GroovyConstants.getOperatePriority(cur);
    	
    	if(GroovyConstants.gc_brackets_small.equals(peek) || GroovyConstants.gc_brackets_small_end.equals(peek)){
    		return false;
    	}else if(peekPririty <= curPririty && !GroovyConstants.gc_express_end.equals(cur)){
    		return true;
    	}else if(GroovyConstants.gc_express_end.equals(cur)){
    		return true;
    	}
        return false;// 开括号是不用考虑的，它的优先级一定是最小的,cur一定是入栈
    }
    
    
    /** 
     * 后缀表达式转中缀表达式
     * @author liukefu
     * @param gSentenceBase
     * @param list
     * @return
     */
    public static GOperate toMiddleExpression(GSentenceBase gSentenceBase,List<GOperate> list) {
    	StringBuilder sb = new StringBuilder();
        for (GOperate op:list) {
        	sb.append(op.getOperate());
        	
        	List<String> opVarList = op.getOpVariableList();
        	for (int i=0;i<opVarList.size();i++) {
        		String express = opVarList.get(i);
        		//操作数是表达式情况处理、
        		if(StringUtils.isNotBlank(express) 
        				&& !isOperate(express)
        				&& CompilerUtil.isExpress(express) ){
        			CompilerProxy proxy = GroovyCompilerFactory.createCompiler(KeyWords.keyCompilerMap.get(KeyWords.compiler_body));        			
        			if(proxy != null){
        				//表达式生成一个临时变量
        				GCompilerResult result =  proxy.compile(new GSentenceSimple(express), express);
        				if(result.getgSentence()!=null){
        					opVarList.set(i, result.getgSentence().toGroovy());
        				}					
        			}
        		}
        	}       	
        }
        //后缀表达式转中缀表达式
        int maxPriority = 10000;
        //处理单目运算
        list = findSimpleOperate(list,sb.toString(),maxPriority);
        //处理双目运算
    	return findAndExecExpression(list,sb.toString(),maxPriority);
    }
    
    /** 
     * 执行两个操作数的计算
     * @author liukefu
     * @param op1
     * @param op2
     * @param op
     * @return
     */
    public static GOperate addOperation(GOperate op1,GOperate op2,GOperate op,int maxPriority){
    	String operate = "";
    	GOperate opNew = new GOperate(op1.getParentSentence(),null);
    	if(op1.getPriority() >= op2.getPriority()){
    		operate = op1.getOperate() + op.getOperate() + op2.getOperate();
    		opNew.addOpVariableList(op1.getOpVariableList());
    		opNew.addOperateList(op1.getOpList());
    		opNew.addChildCalculationList(op1.getChildCalList());
    		opNew.addChildCalculationOpList(op1.getChildCalOpList());
    		if(op2.getPriority()!=0){
    			//属于两个表达式的组合相加，则放到子表达式里
    			opNew.addChildCalculation(op2);
    			opNew.addChildCalculationOp(op);
    		}else{
        		opNew.addOperate(op);
        		opNew.addOpVariableList(op2.getOpVariableList());
        		opNew.addOperateList(op2.getOpList());
    		}
    	}else{
    		operate = op2.getOperate() + op.getOperate() + op1.getOperate();
    		opNew.addOpVariableList(op2.getOpVariableList());   		
    		opNew.addOperateList(op2.getOpList());
    		opNew.addChildCalculationList(op2.getChildCalList());
    		opNew.addChildCalculationOpList(op2.getChildCalOpList());    		
    		if(op1.getPriority()!=0){
    			//属于两个表达式的组合相加，则放到子表达式里
    			opNew.addChildCalculation(op1);
    			opNew.addChildCalculationOp(op);
    		}else{
        		opNew.addOperate(op);
        		opNew.addOpVariableList(op1.getOpVariableList());
        		opNew.addOperateList(op1.getOpList());
    		}     		
    	}
    	opNew.setOperate(operate);
    	//优先级+1
    	opNew.setPriority(maxPriority);
    	return opNew;
    }
    
    /** 
     * 在list中寻找，单目运算符，并计算整合在一起
     * @author liukefu
     * @param list		操作数操作符集合
     * @param code		  代码上下文
     * @param maxPriority 优先级
     * @return
     */
    public static List<GOperate> findSimpleOperate(List<GOperate> list,String code,int maxPriority) {
    	List<GOperate> tmpList = new ArrayList<>();
    	
    	for(int i=0;i<list.size();i++){
    		GOperate op = list.get(i);
    		String str = op.getOperate();    		
    		if(GroovyConstants.isSimpleOperateChar(str)){
    			if(op.isSimple()){
    				if(op.isBefore()){
    					if(i+1<list.size()){
    						//单目前置操作符,取后面的操作数
    						GOperate op2 = list.get(i+1);
    						//空操作数
    						GOperate op1 = new GOperate(op2.getParentSentence(),"");
    						//前置操作数，相当于  ""+op +op2    						
    						GOperate tag = addOperation(op1,op2,op,maxPriority);
    						tmpList.add(tag);
    						i++;
    						continue;
    					}else{
    						CompilerUtil.compilerException("语法错误,表达式缺少操作数",code,CompilerUtil.ERROR_SYSTEM);
    					}
    				}
    			}
    		}else{
				if(i+1<list.size()){
					GOperate op1 = list.get(i+1);
		    		String newOp = op1.getOperate();    		
		    		if(GroovyConstants.isSimpleOperateChar(newOp)){
		    			//单目后置操作符
		    			if(!op1.isBefore()){		    				
    						//空操作数
    						GOperate op2 = new GOperate(op1.getParentSentence(),"");
    						GOperate tag = addOperation(op,op2,op1,maxPriority);
    						//后置操作数，相当于  op2+op +""
							tmpList.add(tag);
							i++;
							continue;
		    			}else{
    						//单目前置操作符,取后面的操作数
    						//空操作数
    						GOperate op0 = new GOperate(op.getParentSentence(),"");
    						//前置操作数，相当于  ""+op +op2    						
    						GOperate tag = addOperation(op0,op,op1,maxPriority);
    						tmpList.add(tag);
    						i++;
    						continue;
		    			}
		    		}
				}
				
    			tmpList.add(op);
    		}
    	}
    	
    	return tmpList;
    }
    /** 
     * 在list中寻找，两个操作数相邻一个操作符的数据
     * @author liukefu
     * @param list		操作数操作符集合
     * @param code		  代码上下文
     * @param maxPriority 优先级
     * @return
     */
    public static GOperate findAndExecExpression(List<GOperate> list,String code,int maxPriority) {    
    	if(list.size()==1){
    		//只有一个的时候，返回
    		return list.get(0);
    	}
    	GOperate tag = null;	
    	List<GOperate> tmpList = new ArrayList<>();
    	int index = -1;
    	boolean hasOperate = false;
    	for(int i=0;i<list.size();i++){
    		GOperate op = list.get(i);
    		String str = op.getOperate();    		
    		if(isOperate(str)){
    			if(i<=1){
    				CompilerUtil.compilerException("语法错误,表达式缺少操作数",code,CompilerUtil.ERROR_SYSTEM);
    			}
    			GOperate op1 = list.get(i-2);
    			GOperate op2 = list.get(i-1);
    			//两个操作数和一个操作符，执行运算生成一个新的操作数
    			tag = addOperation(op1,op2,op,maxPriority);
    			maxPriority = tag.getPriority()-1;
    			index = i;
    			hasOperate = true;
    			break;
    		}
    	}
    	//判断是否找到了操作符，一次只处理一个操作符
    	if(!hasOperate && list.size()>0){
    		CompilerUtil.compilerException("语法错误,表达式缺少操作符",code,CompilerUtil.ERROR_SYSTEM);
    		return null;
    	}
    	tmpList.clear();
    	//把新生成的操作数加入列表
    	for(int i=0;i<list.size();i++){    		
    		GOperate str = list.get(i);
    		if(i!=index-2){
    			//放入还未转换的操作数
    			tmpList.add(str);
    		}else{
    			//找到了新操作数应该放入的位置，放入新生成的操作数
    			tmpList.add(tag);
    			//跳过已经处理过的两个操作数
    			i = i + 2;
    		}    		
    	}
    	if(tmpList.size()>1){
    		//处理下一个操作符
    		return findAndExecExpression(tmpList,code,maxPriority);
    	}else{
    		//只剩个一个操作数，说明转换结束
    		return tmpList.get(0);
    	}
    }
    
}
