package com.mg.groovy.test;

import com.mg.groovy.util.ScriptEngineUtil;

import javax.script.ScriptException;
import java.util.HashMap;

/** 
 * groovy 语句写法 实例 
 * 
 * @author: liukefu
 * @date: 2015年4月24日 下午5:20:42  
 */
public class CompilerTest {

    public static void main(String args[]) throws ScriptException {    
    	
		StringBuilder sb = new StringBuilder();
/*
		sb.append(" import com.qihangedu.tms.groovy.test.*;");
		sb.append(" 定义   a = 1;");
		sb.append("g;");
		sb.append("round(a,2); ");
		sb.append("min(员工.基本工资*4)+6;");
		sb.append(" s = round(a+b,a+max(c+b));");
		sb.append(" s =  (a+b)*c+a;");
		sb.append("__asm{内嵌语句实例，这里不会被处理;}");
		sb.append(" (a+员工.基本工资)*c+a;");	
		sb.append(" round(员工.岗位.基本工资,员工.岗位)+max(1,2);");	
		sb.append("min(2,6+min(员工.基本工资+4*5))*max(abs(6),7)+1*8+9;");
		sb.append("1+3*min(2,6+min(员工.基本工资*4));");
		sb.append("return \"你好\";");
		sb.append("return max(1,4) ;");		
			*/
		/**//*
		sb.append("min(1,2);");
		sb.append("max(2)-max(1);");
		sb.append("++a;");
		sb.append("a--+1;");
		sb.append("a>1 && b==2 || c==1;");
		sb.append("if(a>1 && b==2 || c==11){");
		sb.append("a--;");
		sb.append("}; ");
*/
		sb.append(" (7200 * 1.06 * 146.3135) / 100 ;" );
    	String code = sb.toString();

		Object object = ScriptEngineUtil.execGroovyScript(code, new HashMap());
		System.out.print(object);
		/*
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		SalaryDataImporService salaryService = ac.getBean(SalaryDataImporService.class);
		salaryService.checkSalaryItem();*/
    }    
}
