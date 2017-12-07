package com.mg.report.util;

import com.mg.common.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 报表中日期处理类
 * Created by liukefu on 2015/11/6.
 */
public class ReportDateUtils {

    /**
     * dateType 定义:
     * {id:1,name:"今年"},
     * {id:2,name:"去年"},
     * {id:3,name:"本月"},
     * {id:4,name:"上个月"},
     * {id:5,name:"第一季度"},
     * {id:6,name:"第二季度"},
     * {id:7,name:"第三季度"},
     * {id:8,name:"第四季度"}
     * @param dateType
     * @return
     */
    public static Date getDateScopeBegin(String dateType) {
        Calendar c = Calendar.getInstance();
        switch (dateType) {
            case "1":
                c.set(c.get(Calendar.YEAR), 0, 1);
                return c.getTime();
            case "2":
                c.set(c.get(Calendar.YEAR) - 1, 0, 1);
                return c.getTime();
            case "3":
                c.set(Calendar.DATE, 1);
                return c.getTime();
            case "4":
                c.add(Calendar.MONTH, -1);
                c.set(Calendar.DATE, 1);
                return c.getTime();
            case "5":
                c.set(c.get(Calendar.YEAR), 0, 1);
                return c.getTime();
            case "6":
                c.set(c.get(Calendar.YEAR), 3, 1);
                return c.getTime();
            case "7":
                c.set(c.get(Calendar.YEAR), 6, 1);
                return c.getTime();
            case "8":
                c.set(c.get(Calendar.YEAR), 9, 1);
                return c.getTime();
        }

        return null;
    }
    /**
     * dateType 定义:
     * {id:1,name:"今年"},
     * {id:2,name:"去年"},
     * {id:3,name:"本月"},
     * {id:4,name:"上个月"},
     * {id:5,name:"第一季度"},
     * {id:6,name:"第二季度"},
     * {id:7,name:"第三季度"},
     * {id:8,name:"第四季度"}
     * @param dateType
     * @return
     */
    public static Date getDateScopeEnd(String dateType) {
        Calendar c = Calendar.getInstance();
        int lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        switch (dateType) {
            case "1":
                c.set(Calendar.MONTH, 0);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "2":
                c.add(Calendar.YEAR, -1);
                c.set(Calendar.MONTH, 0);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "3":
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "4":
                c.add(Calendar.MONTH, -1);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "5":
                c.set(Calendar.YEAR,c.get(Calendar.YEAR));
                c.set(Calendar.MONTH, 2);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "6":
                c.set(Calendar.YEAR,c.get(Calendar.YEAR));
                c.set(Calendar.MONTH, 5);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "7":
                c.set(Calendar.YEAR,c.get(Calendar.YEAR));
                c.set(Calendar.MONTH, 8);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
            case "8":
                c.set(Calendar.YEAR,c.get(Calendar.YEAR));
                c.set(Calendar.MONTH, 11);
                lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                c.set(Calendar.DATE, lastDate);
                return c.getTime();
        }

        return null;
    }

    public static void main(String args[]) {

        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("1")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("2")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("3")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("4")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("5")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("6")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("7")));
        System.out.println(DateUtil.convertDateToString(getDateScopeBegin("8")));
        System.out.println("----------------------------------------------------");
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("1")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("2")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("3")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("4")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("5")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("6")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("7")));
        System.out.println(DateUtil.convertDateToString(getDateScopeEnd("8")));
    }
}
