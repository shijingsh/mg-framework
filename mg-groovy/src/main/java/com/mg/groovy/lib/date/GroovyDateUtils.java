package com.mg.groovy.lib.date;

import com.mg.groovy.lib.domain.interfaces.GBaseFunction;
import com.mg.groovy.lib.domain.interfaces.GFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class GroovyDateUtils extends GBaseFunction {

	public String funTypeName = "日期函数";
	@GFunction(notesUrl="/public/groovy/api/date/today.html")
	public Date today(){
		Calendar c = Calendar.getInstance();
		
		return c.getTime();
	}
	@GFunction(notesUrl="/public/groovy/api/date/today.html")
	public Date today(int addDays){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, addDays);
		return c.getTime();
	}
	@GFunction(notesUrl="/public/groovy/api/date/todate.html")
	public Date todate(String date){
		Date myDate = new Date();
		try {
			myDate = DateUtils.parseDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(myDate);
		return c.getTime();
	}
	@GFunction(notesUrl="/public/groovy/api/date/year.html")
	public int year(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	@GFunction(notesUrl="/public/groovy/api/date/year.html")
	public int year(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}
	@GFunction(notesUrl="/public/groovy/api/date/month.html")
	public int month(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH)+1;
	}
	@GFunction(notesUrl="/public/groovy/api/date/month.html")
	public int month(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MONTH)+1;
	}
	@GFunction(notesUrl="/public/groovy/api/date/nextMonth.html")
	public int nextMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		return c.get(Calendar.MONTH)+1;
	}
	@GFunction(notesUrl="/public/groovy/api/date/nextMonth.html")
	public int nextMonth(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		return c.get(Calendar.MONTH)+1;
	}
	@GFunction(notesUrl="/public/groovy/api/date/day.html")
	public int day(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DATE);
	}
	@GFunction(notesUrl="/public/groovy/api/date/day.html")
	public int day(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DATE);
	}
	@GFunction(notesUrl="/public/groovy/api/date/yearAndMonth.html")
	public String yearAndMonth(Date date){
		int year = year(date);
		int month = month(date);
		if(month<10){
			return year+"0"+month;
		}
		return year+""+month;
	}
	@GFunction(notesUrl="/public/groovy/api/date/yearAndMonth.html")
	public String yearAndMonth(){
		int year = year();
		int month = month();
		if(month<10){
			return year+"0"+month;
		}
		return year+""+month;
	}
	@GFunction(notesUrl="/public/groovy/api/date/monthAndDay.html")
	public String monthAndDay(Date date){		
		int month = month(date);
		int day = day(date);
		String sMonth = month<10?"0"+month:month+"";
		String sDay = day<10?"0"+day:day+"";
		
		return sMonth+""+sDay;
	}
	@GFunction(notesUrl="/public/groovy/api/date/monthAndDay.html")
	public String monthAndDay(){		
		int month = month();
		int day = day();
		
		String sMonth = month<10?"0"+month:month+"";
		String sDay = day<10?"0"+day:day+"";
		
		return sMonth+""+sDay;
	}
	@GFunction
	public Integer anniversary(Date date){
		Date now = new Date();
		Integer calYear = yearsBetween(date,now);
		if(afterTheDay(date,now)){
			calYear = calYear -1;
		}
		return calYear;
	}
	@GFunction
	public Integer 周年数(Date date){
		if(date==null)return null;
		Date now = new Date();
		Integer calYear = yearsBetween(date,now);
		if(afterTheDay(date,now)){
			calYear = calYear -1;
		}
		if(calYear<0){
			calYear = 0;
		}
		return calYear;
	}
	@GFunction
	public Integer 自然年数(Date date){
		if(date==null)return null;
		Date now = new Date();
		Integer calYear = yearsBetween(date,now);

		if(calYear<0){
			calYear = 0;
		}
		return calYear;
	}
	@GFunction
	public Integer 周年数(String date){
		Date myDate = new Date();
		if(StringUtils.isBlank(date))return null;
		try {
			myDate = DateUtils.parseDate(date,"YYYY-MM-DD");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date now = new Date();
		Integer calYear = yearsBetween(myDate,now);
		if(afterTheDay(myDate,now)){
			calYear = calYear -1;
		}
		return calYear;
	}
	@GFunction
	public Integer 间隔月数(Date date){
		if(date==null)return null;
		Date now = new Date();

		Days days = Days.daysBetween(new DateTime(date), new DateTime(now));

		return days.dividedBy(30).getDays();
	}
	@GFunction
	public Integer 季度(){

		int quarter = 1;

		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		switch (month) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
			case Calendar.MARCH:
				quarter = 1;
				break;
			case Calendar.APRIL:
			case Calendar.MAY:
			case Calendar.JUNE:
				quarter = 2;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.SEPTEMBER:
				quarter = 3;
				break;
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
			case Calendar.DECEMBER:
				quarter = 4;
				break;
			default:
				break;
		}
		return quarter;
	}
	/**
	 * 计算两个日期之间相差的年数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public  int yearsBetween(Date date1,Date date2)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		int year1 = cal.get(Calendar.YEAR);
		cal.setTime(date2);
		int year2 = cal.get(Calendar.YEAR);
		if(year2 < year1) { //小于当前时间
			return 0;
		}

		return Math.abs(year1-year2);
	}
	/**
	 * 排除年份后比较一个日期是否在另一个日期之后
	 * @param date1
	 * @param date2
	 * @return
	 */
	public boolean afterTheDay(Date date1,Date date2){
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		int month1 = c.get(Calendar.MONDAY);
		int days1 = c.get(Calendar.DATE);

		c.setTime(date2);
		int month2 = c.get(Calendar.MONDAY);
		int days2 = c.get(Calendar.DATE);

		if(month1>month2){
			return true;
		}else if(month1==month2){
			if(days1>=days2){
				return true;
			}
		}

		return false;
	}
	public String getFunTypeName() {
		return funTypeName;
	}
	public void setFunTypeName(String funTypeName) {
		this.funTypeName = funTypeName;
	}
	public static void main(String args[]){
		GroovyDateUtils dayUtils = new GroovyDateUtils();
		System.out.println(dayUtils.today());
		System.out.println(dayUtils.month(dayUtils.today()));
		System.out.println(dayUtils.day(dayUtils.today(-1)));
		System.out.println(dayUtils.yearAndMonth(dayUtils.today()));
		System.out.println(dayUtils.monthAndDay(dayUtils.today()));
		System.out.println(dayUtils.getFunTypeName());
	}
}
