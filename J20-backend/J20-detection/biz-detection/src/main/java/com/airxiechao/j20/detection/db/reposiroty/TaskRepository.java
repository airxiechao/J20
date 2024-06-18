package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.TaskRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 任务记录接口
 */
public interface TaskRepository extends CrudRepository<TaskRecord, String>, JpaSpecificationExecutor<TaskRecord>, JpaUpdateExecutor<TaskRecord> {
    boolean existsByNameIgnoreCase(String name);
    Optional<TaskRecord> findByNameIgnoreCase(String name);
    List<TaskRecord> findAllByNameIgnoreCase(String name);
}
