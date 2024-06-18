package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.EventTypeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 事件类型记录接口
 */
public interface IEventTypeRepository extends CrudRepository<EventTypeRecord, String>, JpaSpecificationExecutor<EventTypeRecord> {
    Page<EventTypeRecord> findByNameContainingIgnoreCase(String name, Pageable page);
    List<EventTypeRecord> findByNameContainingIgnoreCase(String name);
    List<EventTypeRecord> findByParentId(String parentId);
    boolean existsByNameIgnoreCase(String name);
    @Transactional
    void deleteByIdStartingWith(String id);
}
