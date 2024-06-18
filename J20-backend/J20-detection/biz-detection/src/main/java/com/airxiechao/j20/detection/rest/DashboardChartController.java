package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.dashboard.DashboardChart;
import com.airxiechao.j20.detection.api.rest.IDashboardChartController;
import com.airxiechao.j20.detection.api.rest.param.DashboardChartUpdateParam;
import com.airxiechao.j20.detection.api.service.IDashboardChartService;
import com.airxiechao.j20.detection.service.DashboardChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Slf4j
@RestController
@RequestScope
public class DashboardChartController implements IDashboardChartController {

    private IDashboardChartService dashboardChartService = new DashboardChartService();

    @Override
    public Resp<List<DashboardChart>> list() {
        log.info("查询仪表盘图表列表");

        try {
            List<DashboardChart> charts = dashboardChartService.list();
            return new Resp<>(ConstRespCode.OK, null, charts);
        }catch (Exception e){
            log.error("查询仪表盘图表列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp update(DashboardChartUpdateParam param) {
        log.info("修改仪表盘图表：{}", param);

        try {
            dashboardChartService.update(param.getCharts());
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("修改仪表盘图表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
