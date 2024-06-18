package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.statistics.Group;
import com.airxiechao.j20.common.api.pojo.statistics.Histogram;
import com.airxiechao.j20.common.es.*;
import com.airxiechao.j20.detection.api.service.IEventStatisticsService;
import com.airxiechao.j20.detection.es.EsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@Slf4j
public class EventStatisticsService implements IEventStatisticsService {

    @Override
    public Long count(Date beginTime, Date endTime, String level, String index) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        try {
            Long count = EsManager.getInstance().getClient().count(index, term, match, range, prefix);
            return count;
        }catch (Exception e){
            log.error("计数统计发生错误", e);
            return null;
        }
    }

    @Override
    public Double sum(Date beginTime, Date endTime, String level, String index, String sumField) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        EsAggregation aggregation;
        try {
            aggregation = EsManager.getInstance().getClient().aggregate(
                    index, term, match, range, prefix,
                    new EsAggField[]{
                            new EsAggSumField(sumField)
                    }
            );
        }catch (Exception e){
            log.error("求和统计发生错误", e);
            return null;
        }

        Double sum = aggregation.getValue();

        return sum;
    }

    @Override
    public List<Group> groupBy(Date beginTime, Date endTime, String level, String index, String groupByField) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        EsAggregation aggregation;
        try {
            aggregation = EsManager.getInstance().getClient().aggregate(
                    index, term, match, range, prefix,
                    new EsAggField[]{
                            new EsAggGroupByField(groupByField, 1000)
                    }
            );
        }catch (Exception e){
            log.error("分组统计发生错误", e);
            return new ArrayList<>();
        }

        List<Group> counts = parseCountAggregation(aggregation);

        return counts;
    }

    @Override
    public List<Group> groupByAndSum(Date beginTime, Date endTime, String level, String index, String groupByField, String sumField) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        EsAggregation aggregation;
        try {
            aggregation = EsManager.getInstance().getClient().aggregate(
                    index, term, match, range, prefix,
                    new EsAggField[]{
                            new EsAggGroupByField(groupByField, 1000),
                            new EsAggSumField(sumField)
                    }
            );
        }catch (Exception e){
            log.error("分组统计发生错误", e);
            return new ArrayList<>();
        }

        List<Group> sums = parseSumAggregation(aggregation);

        return sums;
    }

    @Override
    public List<Histogram> histogram(Date beginTime, Date endTime, String level, String index, String interval, Long boundMin, Long boundMax) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        EsAggregation aggregation;
        try {
            aggregation = EsManager.getInstance().getClient().aggregate(
                    index, term, match, range, prefix,
                    new EsAggField[]{
                            new EsAggHistogramField("timestamp", interval, boundMin, boundMax)
                    }
            );
        }catch (Exception e){
            log.error("直方图统计发生错误", e);
            return new ArrayList<>();
        }

        List<Histogram> hist = parseHistogramAggregation(aggregation);

        return hist;
    }

    @Override
    public List<Histogram> histogramAndSum(Date beginTime, Date endTime, String level, String index, String interval, Long boundMin, Long boundMax, String sumField) {
        Map<String, Object> term = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, Object> prefix = new HashMap<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        range.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(level)) {
            term.put("level.keyword", level);
        }

        EsAggregation aggregation;
        try {
            aggregation = EsManager.getInstance().getClient().aggregate(
                    index, term, match, range, prefix,
                    new EsAggField[]{
                            new EsAggHistogramField("timestamp", interval, boundMin, boundMax),
                            new EsAggSumField(sumField)
                    }
            );
        }catch (Exception e){
            log.error("直方图统计发生错误", e);
            return new ArrayList<>();
        }

        List<Histogram> hist = parseHistogramSumAggregation(aggregation);

        return hist;
    }

    private List<Group> parseCountAggregation(EsAggregation aggregation){
        List<Group> counts = new ArrayList<>();

        aggregation.getBuckets().forEach(e -> {
            Group group = new Group();
            group.setKey(e.getKey());
            group.setValue(Double.valueOf(e.getCount()));
            counts.add(group);
        });

        return counts;
    }

    private List<Group> parseSumAggregation(EsAggregation aggregation){
        List<Group> sums = new ArrayList<>();

        aggregation.getBuckets().forEach(e -> {
            Double value = e.getAggregation().getValue();
            if(null != value){
                Group group = new Group();
                group.setKey(e.getKey());
                group.setValue(value);

                sums.add(group);
            }
        });

        return sums;
    }

    private List<Histogram> parseHistogramAggregation(EsAggregation aggregation){
        List<Histogram> histograms = new ArrayList<>();

        aggregation.getBuckets().forEach(e -> {
            Histogram histogram = new Histogram();
            histogram.setKey(Long.valueOf(e.getKey()));
            histogram.setValue(Double.valueOf(e.getCount()));

            histograms.add(histogram);
        });

        return histograms;
    }

    private List<Histogram> parseHistogramSumAggregation(EsAggregation aggregation){
        List<Histogram> histograms = new ArrayList<>();

        aggregation.getBuckets().forEach(e -> {
            Histogram histogram = new Histogram();
            histogram.setKey(Long.valueOf(e.getKey()));

            Double value = e.getAggregation().getValue();
            if(null != value){
                histogram.setValue(value);
            }

            histograms.add(histogram);
        });

        return histograms;
    }
}
