package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.dashboard.DashboardChart;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 仪表盘请求接口
 */
public interface IDashboardChartController {
    /**
     * 查询图表列表
     * @return 图表列表
     */
    @PostMapping("/api/dashboard/chart/list")
    Resp<List<DashboardChart>> list();

    /**
     * 修改图表列表
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/dashboard/chart/update")
    Resp update(@RequestBody @Valid DashboardChartUpdateParam param);

}
