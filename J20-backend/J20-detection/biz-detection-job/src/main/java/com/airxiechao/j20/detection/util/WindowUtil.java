package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.detection.api.pojo.constant.ConstTimeUnit;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 窗口辅助类
 */
public class WindowUtil {

    /**
     * 计算窗口时间
     * @param time 窗口大小
     * @param unit 窗口单位
     * @return 窗口时间
     */
    public static Time of(long time, String unit){
        switch (unit.toUpperCase()){
            case ConstTimeUnit.SECOND:
                return Time.seconds(time);
            case ConstTimeUnit.MINUTE:
                return Time.minutes(time);
            case ConstTimeUnit.HOUR:
                return Time.hours(time);
            case ConstTimeUnit.DAY:
                return Time.days(time);
            default:
                throw new RuntimeException(String.format("时间单位[%s]不支持", unit));
        }
    }
}
