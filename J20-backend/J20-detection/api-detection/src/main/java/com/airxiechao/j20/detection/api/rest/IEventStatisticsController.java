package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.statistics.Group;
import com.airxiechao.j20.common.api.pojo.statistics.Histogram;
import com.airxiechao.j20.detection.api.rest.param.EventCountParam;
import com.airxiechao.j20.detection.api.rest.param.EventGroupByParam;
import com.airxiechao.j20.detection.api.rest.param.EventHistogramParam;
import com.airxiechao.j20.detection.api.rest.param.EventSumParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 事件统计请求接口
 */
public interface IEventStatisticsController {
    /**
     * 计数
     * @param param 参数
     * @return 总数
     */
    @PostMapping("/api/detection/event/statistics/count")
    Resp<Long> count(@RequestBody @Valid EventCountParam param);

    /**
     * 求和
     * @param param 参数
     * @return 和
     */
    @PostMapping("/api/detection/event/statistics/sum")
    Resp<Double> sum(@RequestBody @Valid EventSumParam param);

    /**
     * 直方图
     * @param param 参数
     * @return 直方图值列表
     */
    @PostMapping("/api/detection/event/statistics/histogram")
    Resp<List<Histogram>> histogram(@RequestBody @Valid EventHistogramParam param);

    /**
     * 分组
     * @param param 参数
     * @return 分组值列表
     */
    @PostMapping("/api/detection/event/statistics/groupBy")
    Resp<List<Group>> groupBy(@RequestBody @Valid EventGroupByParam param);
}
