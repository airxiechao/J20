package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.statistics.Group;
import com.airxiechao.j20.common.api.pojo.statistics.Histogram;
import com.airxiechao.j20.common.api.pojo.vo.TimeRangeVo;
import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.detection.api.rest.IEventStatisticsController;
import com.airxiechao.j20.detection.api.rest.param.EventCountParam;
import com.airxiechao.j20.detection.api.rest.param.EventGroupByParam;
import com.airxiechao.j20.detection.api.rest.param.EventHistogramParam;
import com.airxiechao.j20.detection.api.rest.param.EventSumParam;
import com.airxiechao.j20.detection.api.service.IEventStatisticsService;
import com.airxiechao.j20.detection.cache.ServiceCacheFactory;
import com.airxiechao.j20.detection.service.EventStatisticsService;
import com.airxiechao.j20.detection.util.EventUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestScope
public class EventStatisticsController implements IEventStatisticsController {

    @Override
    public Resp<Long> count(EventCountParam param) {
        log.info("统计事件总数：{}", param);

        try {
            TimeRangeVo range = TimeUtil.rangeOfMonth(param.getBeginTime(), param.getEndTime());

            Long count;
            if (StringUtils.isBlank(param.getEventTypeId())) {
                count = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .count(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventIndex(range.getBegin(), range.getEnd()));
            } else {
                count = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .count(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()));
            }

            return new Resp<>(ConstRespCode.OK, null, count);
        } catch (Exception e) {
            log.error("统计事件总数发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<Double> sum(EventSumParam param) {
        log.info("统计事件求和：{}", param);

        try {
            TimeRangeVo range = TimeUtil.rangeOfMonth(param.getBeginTime(), param.getEndTime());

            Double sum;
            if (StringUtils.isBlank(param.getEventTypeId())) {
                sum = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .sum(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventIndex(range.getBegin(), range.getEnd()), param.getSumField());
            } else {
                sum = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .sum(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()), param.getSumField());
            }

            return new Resp<>(ConstRespCode.OK, null, sum);
        } catch (Exception e) {
            log.error("统计事件求和发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<List<Histogram>> histogram(EventHistogramParam param) {
        log.info("统计事件直方图：{}", param);

        try {
            TimeRangeVo range = TimeUtil.rangeOfMonth(param.getBeginTime(), param.getEndTime());

            List<Histogram> hist;
            if(StringUtils.isBlank(param.getEventTypeId())){
                hist = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .histogram(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventIndex(range.getBegin(), range.getEnd()),
                                param.getInterval(), null, null);
            }else if (StringUtils.isBlank(param.getSumField())) {
                hist = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .histogram(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()),
                                param.getInterval(), null, null);
            } else {
                hist = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .histogramAndSum(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()),
                                param.getInterval(), null, null, param.getSumField());
            }

            return new Resp<>(ConstRespCode.OK, null, hist);
        } catch (Exception e) {
            log.error("统计事件直方图发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<List<Group>> groupBy(EventGroupByParam param) {
        log.info("统计事件分组：{}", param);

        try {
            TimeRangeVo range = TimeUtil.rangeOfMonth(param.getBeginTime(), param.getEndTime());

            List<Group>  counts;
            if(StringUtils.isBlank(param.getEventTypeId())){
                counts = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .groupBy(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventIndex(range.getBegin(), range.getEnd()), param.getGroupByField());
            } else if(StringUtils.isBlank(param.getSumField())){
                counts = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .groupBy(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()),
                                param.getGroupByField());
            }else{
                counts = ServiceCacheFactory.getInstance().get((IEventStatisticsService) new EventStatisticsService())
                        .groupByAndSum(range.getBegin(), range.getEnd(), param.getLevel(),
                                EventUtil.buildSearchEventPropertyIndex(range.getBegin(), range.getEnd(), param.getEventTypeId()),
                                param.getGroupByField(), param.getSumField());
            }

            return new Resp<>(ConstRespCode.OK, null, counts);
        } catch (Exception e) {
            log.error("统计事件分组发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
