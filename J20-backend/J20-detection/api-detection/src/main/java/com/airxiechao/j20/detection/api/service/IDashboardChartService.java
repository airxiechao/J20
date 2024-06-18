package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.detection.api.pojo.dashboard.DashboardChart;

import java.util.List;

/**
 * 仪表盘服务接口
 */
public interface IDashboardChartService {
    /**
     * 查询所有仪表盘图表
     * @return 所有图表
     */
    List<DashboardChart> list();

    /**
     * 修改所有仪表盘图表
     * @param charts 所有仪表盘图表
     */
    void update(List<DashboardChart> charts);
}
