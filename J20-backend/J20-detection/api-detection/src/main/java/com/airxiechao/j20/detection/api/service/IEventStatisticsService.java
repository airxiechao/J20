package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.statistics.Group;
import com.airxiechao.j20.common.api.pojo.statistics.Histogram;

import java.util.Date;
import java.util.List;

/**
 * 事件统计服务接口
 */
public interface IEventStatisticsService {
    /**
     * 计数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @return 数量
     */
    Long count(Date beginTime, Date endTime, String level, String index);

    /**
     * 求和
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @param sumField 求和字段
     * @return 求和值
     */
    Double sum(Date beginTime, Date endTime, String level, String index, String sumField);

    /**
     * 分组
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @param groupByField 分组字段
     * @return 分组列表
     */
    List<Group> groupBy(Date beginTime, Date endTime, String level, String index, String groupByField);

    /**
     * 分组求和
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @param groupByField 分组字段
     * @param sumField 求和字段
     * @return 分组求和值列表
     */
    List<Group>  groupByAndSum(Date beginTime, Date endTime, String level, String index, String groupByField, String sumField);

    /**
     * 直方图
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @param interval 时间间隔
     * @param boundMin 时间下界
     * @param boundMax 时间上界
     * @return 直方图值列表
     */
    List<Histogram> histogram(Date beginTime, Date endTime, String level, String index, String interval, Long boundMin, Long boundMax);

    /**
     * 直方图求和
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param level 事件级别
     * @param index 索引
     * @param interval 时间间隔
     * @param boundMin 时间下界
     * @param boundMax 时间上界
     * @param sumField 求和字段
     * @return 直方图求和值列表
     */
    List<Histogram> histogramAndSum(Date beginTime, Date endTime, String level, String index, String interval, Long boundMin, Long boundMax, String sumField);
}
