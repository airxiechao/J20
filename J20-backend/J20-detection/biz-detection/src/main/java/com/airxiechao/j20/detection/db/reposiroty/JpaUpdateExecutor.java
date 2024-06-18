package com.airxiechao.j20.detection.db.reposiroty;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.function.Function;

/**
 * JPA 修改记录的接口
 * @param <T> 记录类型
 */
public interface JpaUpdateExecutor<T> {
    @Transactional
    int update(Function<CriteriaBuilder, CriteriaUpdate<T>> criteriaUpdateFunction);
}
