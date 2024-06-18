package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.vo.TimeRangeVo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    /**
     * 计算某时间几天后的时间
     * @param time 起始时间
     * @param days 增加天数
     * @return 时间
     */
    public static Date timeOfDays(Date time, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, days);

        return cal.getTime();
    }

    public static TimeRangeVo rangeOfMonth(Date beginTime, Date endTime){
        Date begin, end;
        Calendar cal = Calendar.getInstance();
        if(null != beginTime && null != endTime){
            begin = beginTime;
            end = endTime;
        }else if(null != beginTime){
            begin = beginTime;
            cal.setTime(begin);
            cal.add(Calendar.MONTH, 1);
            end = cal.getTime();
        }else if(null != endTime){
            end = endTime;
            cal.setTime(end);
            cal.add(Calendar.MONTH, -1);
            begin = cal.getTime();
        }else{
            end = new Date();
            cal.setTime(end);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            end = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            begin = cal.getTime();
        }

        return new TimeRangeVo(begin, end);
    }

    /**
     * 获取当前时区相对UTC时区的偏移小时
     * @return 偏移小时
     */
    public static int getTimezoneOffsetHour(){
        return TimeZone.getDefault().getOffset(0) / 1000 / 60 / 60;
    }
}
