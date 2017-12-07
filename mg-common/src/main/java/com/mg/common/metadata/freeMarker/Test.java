package com.mg.common.metadata.freeMarker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liukefu on 2015/9/28.
 */
public class Test {

    public static void main(String[] args) {
        //<td( .*?)?>.*?</td>   (?<=<td>).*(?=</td>)
/*		Pattern p = Pattern.compile("<td( .*?)?>(.*?)</td>");
        StringBuffer sb = new StringBuffer();
		String str ="<td class>10分钟</td><td>20分钟</td>";
		Matcher m = p.matcher(str);
		while(m.find()) {
            System.out.println("group1:"+m.group(1));
            System.out.println("group2:"+m.group(2));
            System.out.println("start:" + m.start(2) + "----end:" + m.end(2));

            String group1 = "<td "+m.group(1)+">";
            m.appendReplacement(sb, group1 + "1111</td>");

        }
        m.appendTail(sb);
        System.out.println(sb.toString());*/
        ExecutorService pool = Executors.newFixedThreadPool(10);

        int size = 569;
        int poolsize = 10;
        int threadSize = size / poolsize;
        for(int i=0;i<poolsize-1;i++){
            int start = i * threadSize;
            int end = (i+1) * threadSize;

            System.out.println("start="+start+";end="+end);
        }
        int start = (poolsize-1) * threadSize;
        int end = size;
        System.out.println("start="+start+";end="+end);
    }
}
