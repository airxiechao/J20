package com.airxiechao.j20.detection.db.reposiroty;

import com.airxiechao.j20.detection.db.record.RuleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * 规则记录接口
 */
public interface IRuleRepository extends CrudRepository<RuleRecord, String>, JpaSpecificationExecutor<RuleRecord> {
    boolean existsByNameIgnoreCase(String name);
    Optional<RuleRecord> findByNameIgnoreCase(String name);
}
