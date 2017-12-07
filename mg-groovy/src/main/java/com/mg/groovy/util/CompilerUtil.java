package com.mg.groovy.util;

import com.mg.groovy.compiler.exception.CompilerGroovyException;
import com.mg.groovy.define.bean.GClass;
import com.mg.groovy.define.bean.GMethod;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GVariable;
import com.mg.groovy.define.keyword.GroovyConstants;
import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.lib.GroovyLib;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilerUtil {
	/**
	 * 系统级别编译错误
	 */
	public static int ERROR_SYSTEM = 0;
	/**
	 * 业务类编译错误
	 */
	public static int ERROR_BUSSINESS = 1;
	/**
	 * 编译错误，报错开关
	 */
	public static boolean ERROR_FOR_COMPILER = false;
	
	/** 
	 * 统一处理编译错误
	 * @author liukefu
	 * @param message
	 * @throws CompilerGroovyException
	 */
	public static void compilerException(String message) throws CompilerGroovyException{
		compilerException(message,null);
	}
	
	/** 
	 * 统一处理编译错误
	 * @author liukefu
	 * @param message
	 * @param context
	 * @throws CompilerGroovyException
	 */
	public static void compilerException(String message,String context) throws CompilerGroovyException {
		compilerException(message,null,ERROR_BUSSINESS);
	}
	
	/** 
	 * 统一处理编译错误
	 * @author liukefu
	 * @param message
	 * @param context
	 * @param errorType
	 * @throws CompilerGroovyException
	 */
	public static void compilerException(String message,String context,int errorType) throws CompilerGroovyException{
		if(ERROR_SYSTEM==errorType){
			throw new CompilerGroovyException(message,context);
		}
		if(ERROR_FOR_COMPILER){
			throw new CompilerGroovyException(message,context);
		}
	}
	/** 
	 * 统一处理缺失关键字错误编译错误
	 * @author liukefu
	 * @param missContent
	 * @param context
	 * @param errorType
	 * @throws CompilerGroovyException
	 */
	public static void compilerMissException(String missContent,String context,int errorType) throws CompilerGroovyException{
		String message = "缺失：“"+missContent+"”";
		if(ERROR_SYSTEM==errorType){
			throw new CompilerGroovyException(message,context);
		}
		if(ERROR_FOR_COMPILER){
			throw new CompilerGroovyException(message,context);
		}
	}
	/** 
	 * 根据字符的hashcode生成一个临时变量名称
	 * @author liukefu
	 * @return
	 */
	public static String randomVarName(){
	 String codeUUID = UUID.randomUUID().toString();
	 int hashCode = codeUUID.hashCode();
	 
	 return "tmp_var_" + hashCode;	
	}
	
	/** 
	 * 判断是否是语句结束字符
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static boolean isSentenceEndChar(String code){
		
		if(GroovyConstants.gc_semicolon.equals(code)){
			return true;
		}
		
		return false;
	}
	/** 
	 * 读取语句结束 位置
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static int getSentenceEndIndex(String code){
		
		int index = indexOf(code,GroovyConstants.gc_semicolon,0);
		
		return index;
	}
	
	/** 
	 * 读取函数结束 位置
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static int getMethodEndIndex(String code){
		
		int index = lastIndexOf(code,GroovyConstants.gc_brackets_small_end,0);
		
		return index;
	}
	
	/** 
	 * 读取块结束 位置
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static int getBlockEndIndex(String code){
		
		int index = indexOf(code,GroovyConstants.gc_brackets_big_end,0);

		return index;
	}
	/** 
	 * 读取语句结束 位置
	 * @author liukefu
	 * @param code
	 * @param include  包含字符本身
	 * @return
	 */
	public static int getSentenceEndIndex(String code,boolean include){
		
		int index = indexOf(code,GroovyConstants.gc_semicolon,0);
		if(index != -1 && include){
			index = index + GroovyConstants.gc_semicolon.length();
		}else if(index==-1){
			index = code.length();
		}
		return index;
	}
	
	/** 
	 * 读取函数结束 位置
	 * @author liukefu
	 * @param code
	 * @param include  包含字符本身
	 * @return
	 */
	public static int getMethodEndIndex(String code,boolean include){
		
		int index = getMatchedChar(code, GroovyConstants.gc_brackets_small, GroovyConstants.gc_brackets_small_end, 0);
		if(index != -1 && include){
			index = index + GroovyConstants.gc_brackets_small_end.length();
		}
		return index;
	}
	
	/** 
	 * 读取块结束 位置
	 * @author liukefu
	 * @param code
	 * @param include  包含字符本身
	 * @return
	 */
	public static int getBlockEndIndex(String code,boolean include){
		
		int index = indexOf(code,GroovyConstants.gc_brackets_big_end,0);
		if(index != -1){
			index = index + GroovyConstants.gc_brackets_big_end.length();
		}
		return index;
	}
	/** 
	 * 字符是否是个常量
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static boolean isConstant(String code){
		if(StringUtils.isNotBlank(code)){
			code = code.trim();
		}
		if(StringUtils.isBlank(code)){
			return true;
		}else if(NumberUtils.isNumber(code)){
			return true;
		}else if(code.startsWith(GroovyConstants.gc_quotes) && code.endsWith(GroovyConstants.gc_quotes)){
			return true;
		}
		
		return false;
	}
	
	/** 
	 * 字符是否是个表达式
	 * @author liukefu
	 * @param code
	 * @return
	 */
	public static boolean isExpress(String code){
		if(StringUtils.isNotBlank(code)){
			code = code.trim();
		}
		if(isConstant(code)){
			return false;
		}else{
			if(indexOf(code, GroovyConstants.gc_cal_add, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_cal_subtract, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_cal_multiply, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_cal_divide, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_cal_model, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_fetch_point, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_brackets_small, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_brackets_big, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_fetch_point, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_brackets_small_end, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_brackets_big_end, 0)!= -1){
				return true;
			}else if(indexOf(code, GroovyConstants.gc_brackets_mid_end, 0)!= -1){
				return true;
			}
		}
		
		return false;
	}
	/** 
	 * 获取两个字符之间的 字符串
	 * indexOf 实现 效率低 将来需修改成正则。
	 * @author liukefu
	 * @param code
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getBetweenStr(String code,String begin,String end){
		if(StringUtils.isBlank(code))return "";
		
		int beginIndex = code.indexOf(begin)+begin.length();
		int endIndex = code.indexOf(end);
		
		return code.substring(beginIndex,endIndex);
	}
	
	/**  
	 * 获取字符 quoteChar 在 line中出现的位置
	 * @author liukefu
	 * @param line
	 * @param quoteChar
	 * @param startFrom
	 * @return
	 */
	public static int indexOf(String line,String quoteChar,int startFrom){
		
		return StringUtils.indexOf(line, quoteChar, startFrom);
	}
	
	/** 
	 * 获取字符 quoteChar 在 line中最后一次出现的位置
	 * @author liukefu
	 * @param line
	 * @param quoteChar
	 * @param startFrom
	 * @return
	 */
	public static int lastIndexOf(String line,String quoteChar,int startFrom){
		StringBuilder str = new StringBuilder(line);
		return str.lastIndexOf(quoteChar);
	}
	
	/** 
	 * 截取字符子串
	 * @author liukefu
	 * @param str
	 * @param start
	 * @param end
	 * @return
	 */
	public static String substring(String str,int start,int end){
		
		return StringUtils.substring(str, start, end);
	}
	
	/** 
	 * 分割子串
	 * @author liukefu
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] split(String str,String separatorChars){
		
		return StringUtils.split(str, separatorChars);
	}
	
	/** 
	 * 根据语句块上下文 类型名称 找到类型定义
	 * @author liukefu
	 * @param sentence
	 * @param gClassName
	 * @return
	 */
	public static GClass getGClass(GSentenceBase sentence,String gClassName){
		if(StringUtils.isNotBlank(gClassName)){
			gClassName = gClassName.trim();
		}		
		GClass gClass = null;
		if(sentence!=null){
			gClass = sentence.getGClassByName(gClassName);
		}
		//上下文找不到定义，找类库
		if(gClass==null){
			//上级找不到 找类函数
			if(GroovyLib.libMap.get(gClassName)!=null){
				return GroovyLib.libMap.get(gClassName);
			}
		}
		
		return gClass;
	}
	
	/** 
	 * 根据语句块上下文  函数名称 找到函数定义
	 * @author liukefu
	 * @param sentence
	 * @param gMethodName
	 * @return
	 */
	public static GMethod getGMethod(GSentenceBase sentence,String gMethodName){
		if(StringUtils.isNotBlank(gMethodName)){
			gMethodName = gMethodName.trim();
		}
		GMethod gMethod = null;
		if(sentence!=null){
			gMethod = sentence.getGMethodByName(gMethodName);
		}
		//上下文找不到定义，找函数库
		if(gMethod==null){
			//上级找不到 找库函数
			if(GroovyFun.funMap.get(gMethodName)!=null){
				return new GMethod();
			}
			
		}
		
		return gMethod;
	}
	
	/** 
	 * 根据语句块上下文  变量名称 找到变量定义
	 * @author liukefu
	 * @param sentence
	 * @param gVariableName
	 * @return
	 */
	public static GVariable getGVariable(GSentenceBase sentence,String gVariableName){
		if(StringUtils.isNotBlank(gVariableName)){
			gVariableName = gVariableName.trim();
		}
		GVariable gVariable = null;
		if(sentence!=null){
			gVariable = sentence.getGVariableByName(gVariableName);
		}
		//上下文找不到定义，找函数库
		if(gVariable==null){
			//上级找不到 找库函数
						
		}
		
		return gVariable;
	}
	
	/** 
	 * 操作符的优先级
	 * @author liukefu
	 * @param operate
	 * @return
	 */
	public static int getOperatePriority(String operate){
        switch (operate) {  
        case "+":    
            return 0; 
        case "-":    
        	return 0;  
        case "%":    
        	return 0;          	
        case "*":    
        	return 1; 
        case "/":    
        	return 1;   
        case "(":    
        	return 2;         	
        } 
        
        return -1;
	}
	
	/** 
	 * 获取匹配的括号
	 * @author liukefu
	 * @param code
	 * @param level
	 * @return 
	 */
	public static Matcher getMatchedContent(String code,int level){
		
		String regex1 = "\\(.*\\)";
		//匹配二级括号
		String regex2 = "\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)";
		//匹配三级括号
		String regex3 = "\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)";
		//匹配四级括号
		String regex4 = "\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)";	
		
       if(level>4){
        	throw new CompilerGroovyException("不支持的字符“()”嵌套深度");
        }
		String regex = null;
        switch (level) {  
        case 1:    
        	regex = regex1; 
        case 2:    
        	regex = regex2; 
        case 3:    
        	regex = regex3;     	
        case 4:    
        	regex = regex4;  	
        } 
 
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(code);
		
		return matcher;
	}
	
	/** 
	 * 在 字符 code 中 寻找 与 “(” 相匹配的 字符 ")" 的位置
	 * @author liukefu
	 * @param code
	 * @param s
	 * @param e
	 * @param startIndex
	 * @return
	 */
	public static int getMatchedChar(String code,String s,String e,int startIndex){
		if(startIndex<0){
			startIndex = 0;
		}
		char [] cArr = code.toCharArray();
		int sNum = 0;
		int eNum = 0;
		for(int i=startIndex;i<cArr.length;i++){
			char c = cArr[i];
			if(String.valueOf(c).equals(s)){
				++sNum;
			}
			if(String.valueOf(c).equals(e)){
				++eNum;
			}	
			
			if(sNum != 0 && sNum == eNum){
				return i;
			}
		}
		
		return -1;
	}
	
	/** 
	 * 在某个位置后寻找第一个非空字符
	 * @author liukefu
	 * @param code
	 * @param startIndex
	 * @return
	 */
	public static char getOneOperateChar(String code,int startIndex){
		if(startIndex<0){
			startIndex = 0;
		}
		char [] cArr = code.toCharArray();
		char c = 0 ;
		for(int i=startIndex;i<cArr.length;i++){
			c = cArr[i];
			if(StringUtils.isNotBlank(String.valueOf(c)) ){
				return c;
			}
		}
		return c;
	}
	/** 
	 * 在某个位置后寻找第一个非空操作数
	 * @author liukefu
	 * @param code
	 * @param startIndex
	 * @return
	 */
	public static char getOneOperate(String code,int startIndex){
		if(startIndex<0){
			startIndex = 0;
		}
		char [] cArr = code.toCharArray();
		char c = 0 ;
		for(int i=startIndex;i<cArr.length;i++){
			c = cArr[i];
			String cStr = String.valueOf(c);
			if(StringUtils.isNotBlank(cStr) && !isExpress(cStr)){
				//不是空字符，不是操作符 返回
				return c;
			}
		}
		return c;
	}
	/** 
	 * 解析函数的参数
	 * @author liukefu
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] splitFunctionParam(String str,String separatorChars){
		int length = StringUtils.split(str, separatorChars).length;
		String params[] = new String[length];
		char [] cArr = str.toCharArray();
		char c = 0 ;
		int bracketsNum = 0;
		int paramIndex = 0;
		StringBuilder param = new StringBuilder();
		for(int i=0;i<cArr.length;i++){
			c = cArr[i];
			if(String.valueOf(c).equals(GroovyConstants.gc_brackets_small)){
				++bracketsNum;
			}
			if(String.valueOf(c).equals(GroovyConstants.gc_brackets_small_end)){
				--bracketsNum;
			}	
			
			if(String.valueOf(c).equals(GroovyConstants.gc_comma)){
				if(bracketsNum==0){
					String p = param.toString();
					params[paramIndex++] = p;
					param.delete(0, p.length());
				}else{
					param.append(c);
				}
			}else{
				param.append(c);
			}
		}
		//最后一个参数
		if(param.length()>0){
			params[paramIndex] = param.toString();
		}
		
		return params;
	}
}
