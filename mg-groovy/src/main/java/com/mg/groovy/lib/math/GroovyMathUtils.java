package com.mg.groovy.lib.math;

import com.mg.groovy.lib.domain.interfaces.GBaseFunction;
import com.mg.groovy.lib.domain.interfaces.GFunction;

import java.math.BigDecimal;

/** 
 * groovy 数学函数 
 * 注意：Groovy 编译引擎，已经把数据number 类型的，统一转化成了BigDecimal
 * 所有 groovy 里只有一种数字类型 BigDecimal(为了防止要写各种重载函数)
 * @author: liukefu
 * @date: 2015年4月23日 下午12:49:38  
 */
public class GroovyMathUtils extends GBaseFunction {

	public String funTypeName = "数学函数";

	@GFunction(notesUrl="/public/groovy/api/math/sum.html",isDynamicParam=true)
	public BigDecimal sum(BigDecimal... number){
		BigDecimal totalNum = new BigDecimal(0);
		if(number==null || number.length==1){
			return number[0];
		}
		for(BigDecimal num:number){
			if(num!=null){
				totalNum = totalNum.add(num);
			}
		}
		
		return totalNum;		
	}

	@GFunction(notesUrl="/public/groovy/api/math/max.html",isDynamicParam=true)
	public BigDecimal max(BigDecimal... number){
		if(number==null || number.length==1){
			return number[0];
		}
		BigDecimal maxNum = new BigDecimal(0);
		for(BigDecimal num:number){
			if(num==null)continue;
			if(maxNum.doubleValue()<num.doubleValue()){
				maxNum = num;
			}			
		}
		
		return maxNum;
	}	
	@GFunction(notesUrl="/public/groovy/api/math/min.html",isDynamicParam=true)
	public BigDecimal min(BigDecimal... number){
		if(number==null || number.length==1){
			return number[0];
		}
		BigDecimal minNum = new BigDecimal(Integer.MAX_VALUE);
		for(BigDecimal num:number){
			if(num==null)continue;
			if(minNum.doubleValue()>num.doubleValue()){
				minNum = num;
			}	
		}
		
		return minNum;
	}
	
	@GFunction(notesUrl="/public/groovy/api/math/round.html")
	public Number round(BigDecimal number,BigDecimal newScale){
	
		return round(number,newScale,new BigDecimal(BigDecimal.ROUND_HALF_UP));
	}
	@GFunction(notesUrl="/public/groovy/api/math/roundUp.html")
	public BigDecimal roundUp(BigDecimal number,BigDecimal newScale){
		if(number!=null){
			number = number.setScale(newScale.intValue(), BigDecimal.ROUND_UP);
		}
		
		return number;
	}	
	@GFunction(notesUrl="/public/groovy/api/math/roundDown.html")
	public BigDecimal roundDown(BigDecimal number,BigDecimal newScale){
		if(number!=null){
			number = number.setScale(newScale.intValue(), BigDecimal.ROUND_DOWN);
		}
		
		return number;
	}
	@GFunction(notesUrl="/public/groovy/api/math/round.html")
	public BigDecimal round(BigDecimal number,BigDecimal newScale, BigDecimal roundingMode){
		if(number!=null){
			number = number.setScale(newScale.intValue(), roundingMode.intValue());
		}
		
		return number;
	}	
	
	@GFunction(notesUrl="/public/groovy/api/math/abs.html")
	public BigDecimal abs(BigDecimal number){
		if(number!=null){
			double d = number.doubleValue();
			if(d>0){
				return number;
			}else{
				//符号位取反
				return number.multiply(new BigDecimal(-1));
			}
		}
		return number;
	}	

	public String getFunTypeName() {
		return funTypeName;
	}

	public void setFunTypeName(String funTypeName) {
		this.funTypeName = funTypeName;
	}

	public static void main(String args[]){
		GroovyMathUtils mathUtils = new GroovyMathUtils();
		System.out.println(Math.round(23.545));
		System.out.println(mathUtils.sum(new BigDecimal(23.545),new BigDecimal(23.545)).doubleValue());
		System.out.println(mathUtils.round(new BigDecimal(23.545),new BigDecimal(1),new BigDecimal(BigDecimal.ROUND_HALF_UP)).doubleValue());
		
	}
}
