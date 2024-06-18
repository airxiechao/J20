package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.detection.api.pojo.dashboard.DashboardChart;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DashboardChartUpdateParam {
    @NotNull
    private List<DashboardChart> charts;
}
