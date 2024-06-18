package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.detection.es.EsConfigFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 事件辅助类
 */
public class EventUtil {

    /**
     * 构造事件查询索引
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 索引
     */
    public static String buildSearchEventIndex(Date beginTime, Date endTime) {
        try {
            String eventIndexPrefix = EsConfigFactory.getInstance().get().getEventIndexPrefix();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
            Date begin = simpleDateFormat.parse(simpleDateFormat.format(beginTime));
            Date end = simpleDateFormat.parse(simpleDateFormat.format(endTime));

            List<String> list = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(begin);

            while (!cal.getTime().after(end)) {
                list.add(String.format("%s-%s*", eventIndexPrefix, simpleDateFormat.format(cal.getTime())));
                cal.add(Calendar.MONTH, 1);
            }
            return String.join(",", list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造事件属性查询索引
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param eventTypeId 事件类型
     * @return 索引
     */
    public static String buildSearchEventPropertyIndex(Date beginTime, Date endTime, String eventTypeId) {
        try {
            String eventPropertyIndexPrefix = EsConfigFactory.getInstance().get().getEventPropertyIndexPrefix();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
            Date begin = simpleDateFormat.parse(simpleDateFormat.format(beginTime));
            Date end = simpleDateFormat.parse(simpleDateFormat.format(endTime));

            List<String> list = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(begin);

            while (!cal.getTime().after(end)) {
                list.add(String.format("%s-%s-%s*", eventPropertyIndexPrefix, eventTypeId, simpleDateFormat.format(cal.getTime())));
                cal.add(Calendar.MONTH, 1);
            }
            return String.join(",", list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
