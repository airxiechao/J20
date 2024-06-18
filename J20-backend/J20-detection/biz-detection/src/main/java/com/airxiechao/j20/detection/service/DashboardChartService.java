package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.api.pojo.dashboard.DashboardChart;
import com.airxiechao.j20.detection.api.service.IDashboardChartService;
import com.airxiechao.j20.detection.db.record.DashboardChartRecord;
import com.airxiechao.j20.detection.db.reposiroty.IDashboardChartRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardChartService implements IDashboardChartService {

    private IDashboardChartRepository dashboardChartRepository = ApplicationContextUtil.getContext().getBean(IDashboardChartRepository.class);

    @Override
    public List<DashboardChart> list() {
        List<DashboardChartRecord> records = dashboardChartRepository.findAll(Sort.by("id").ascending());

        List<DashboardChart> list = records.stream().map(this::buildDashboardChart).collect(Collectors.toList());

        return list;
    }

    @Override
    public void update(List<DashboardChart> charts) {
        dashboardChartRepository.deleteAll();

        for (int i = 0; i < charts.size(); i++) {
            DashboardChart chart = charts.get(i);
            chart.setId(i + 1L);

            DashboardChartRecord record = buildDashboardChartRecord(chart);
            dashboardChartRepository.save(record);
        }
    }

    private DashboardChart buildDashboardChart(DashboardChartRecord record){
        DashboardChart dashboardChart = new DashboardChart();
        dashboardChart.setId(record.getId());
        dashboardChart.setName(record.getName());
        dashboardChart.setEventTypeId(record.getEventTypeId());
        dashboardChart.setAggregateType(record.getAggregateType());
        dashboardChart.setGroupByField(record.getGroupByField());
        dashboardChart.setAggregateField(record.getAggregateField());

        return dashboardChart;
    }

    private DashboardChartRecord buildDashboardChartRecord(DashboardChart dashboardChart){
        DashboardChartRecord record = new DashboardChartRecord();
        record.setId(dashboardChart.getId());
        record.setName(dashboardChart.getName());
        record.setEventTypeId(dashboardChart.getEventTypeId());
        record.setAggregateType(dashboardChart.getAggregateType());
        record.setGroupByField(dashboardChart.getGroupByField());
        record.setAggregateField(dashboardChart.getAggregateField());

        return record;
    }
}
