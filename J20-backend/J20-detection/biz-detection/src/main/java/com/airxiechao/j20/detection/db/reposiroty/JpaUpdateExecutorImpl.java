package com.airxiechao.j20.detection.db.reposiroty;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.function.Function;

/**
 * JPA 修改记录的实现
 * @param <T>
 */
@RequiredArgsConstructor
public class JpaUpdateExecutorImpl<T> implements JpaUpdateExecutor<T> {
    private final EntityManager em;

    @Override
    public int update(Function<CriteriaBuilder, CriteriaUpdate<T>> criteriaUpdateFunction) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = criteriaUpdateFunction.apply(cb);
        return em.createQuery(criteriaUpdate).executeUpdate();
    }
}
