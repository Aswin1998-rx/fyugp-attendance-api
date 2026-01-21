package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * This interface holds the spec for a repository that helps to create
 * custom queries using JPA ({@link CriteriaBuilder}, {@link CriteriaQuery}) ,
 * that allows custom selects, sub queries, custom specifications, custom pagination etc.
 *
 * @param <T> the model type to which this repository is applied
 */
@NoRepositoryBean
public interface CustomQueryExecutor<T extends BaseEntity> {

    Page<T> queryPage(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<T>> consumer,
                      TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<Long>> count,
                      Pageable pageable);

    record Limit(
            int offset,
            int limit
    ) {
    }
}
