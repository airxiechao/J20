package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.DashboardChartRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 仪表盘图表记录接口
 */
public interface IDashboardChartRepository extends CrudRepository<DashboardChartRecord, String>, JpaSpecificationExecutor<DashboardChartRecord> {
    List<DashboardChartRecord> findAll(Sort sort);
}
