package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.KafkaDataSourceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 数据源记录接口
 */
public interface IDataSourceRepository extends CrudRepository<KafkaDataSourceRecord, String>, JpaSpecificationExecutor<KafkaDataSourceRecord> {
    Page<KafkaDataSourceRecord> findByNameContainingIgnoreCase(String name, Pageable page);
    List<KafkaDataSourceRecord> findByNameContainingIgnoreCase(String name, Sort sort);
    Page<KafkaDataSourceRecord> findAll(Pageable page);
    List<KafkaDataSourceRecord> findAll(Sort sort);
    boolean existsByTopic(String topic);
}
