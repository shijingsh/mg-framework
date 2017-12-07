package com.mg.framework.log;

import com.alibaba.fastjson.serializer.NameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fastjson的过滤方法.
 *
 * 2014-8-29重构，将驼峰命名法单独保留，剩余的过滤方法全部移入 FastjsonSimpleFilter
 * 中。并且以后应用调用可直接使用 FastjsonSimpleFilter。本类仅作为历史保留。
 */
public class FastjsonFilter extends FastjsonSimpleFilter implements NameFilter  {
    private static Logger logger = LoggerFactory.getLogger(FastjsonFilter.class);

    public FastjsonFilter() {
    }

    /**
     * 传入需要过滤的类和属性
     * @param inputFilterPropertyNames class.preoperty或直接propertyName，property看开始，class看末尾，如ItemEntity.belong
     */
    public FastjsonFilter(String inputFilterPropertyNames){
        super(inputFilterPropertyNames);
    }

    public FastjsonFilter(String[] inputFilterPropertyNames) {
        super(inputFilterPropertyNames);
    }

    /**
     *  驼峰格式字符串 转换成 下划线格式字符串
     * @param object 属性所在的对象
     * @param name 属性名，驼峰格式字符串
     * @param value 属性值
     * @return 下划线格式字符串
     */
    @Override
    public String process(Object object, String name, Object value){
    	
    	//因为此方法把map的key的值修改掉，所以提取出来，直接返回
    	if((object.getClass()==HashMap.class) || (object.getClass()==TreeMap.class))
    	{
    		return name;
    	}
    	
        Pattern p = Pattern.compile("[A-Z]");
        if (name == null || name.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(name);
        Matcher mc = p.matcher(name);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i,mc.end() + i,"_" + mc.group().toLowerCase());
            i++;
        }
        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}