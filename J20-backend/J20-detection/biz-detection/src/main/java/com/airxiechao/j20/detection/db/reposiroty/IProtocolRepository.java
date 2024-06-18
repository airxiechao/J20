package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.ProtocolRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 协议记录接口
 */
public interface IProtocolRepository extends CrudRepository<ProtocolRecord, String>, JpaSpecificationExecutor<ProtocolRecord> {
    Page<ProtocolRecord> findByCodeContainingIgnoreCase(String code, Pageable page);
    List<ProtocolRecord> findByCodeContainingIgnoreCase(String code, Sort sort);
    Page<ProtocolRecord> findAll(Pageable page);
    List<ProtocolRecord> findAll(Sort sort);
    boolean existsByCodeIgnoreCase(String code);
}
