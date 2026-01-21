package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is the default implementation for {@link CustomQueryExecutor}.
 *
 * @param <T> the model on which this repository is applied
 */
@Slf4j
@AllArgsConstructor
@NoRepositoryBean
public class CustomQueryExecutorImpl<T extends BaseEntity> implements CustomQueryExecutor<T> {

    private EntityManager manager;
    private JpaEntityInformation<T, ?> entityInformation;

    @Override
    public Page<T> queryPage(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<T>> consumer,
                             TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<Long>> count, Pageable pageable) {
        log.trace("executing paginated custom query on root");
        var wrapper = new AtomicReference<CriteriaQuery<T>>();
        List<T> result = getTypedQuery((root, criteriaBuilder, criteriaQuery) -> {
            consumer.accept(root, criteriaBuilder, criteriaQuery);
            wrapper.set(criteriaQuery);
        }, pageable).getResultList();
        List<Long> countList = getCountQuery(wrapper.get(), count).getResultList();
        return PageableExecutionUtils.getPage(result, pageable, () -> countList.size() > 1 ? countList.size() : countList.get(0));
    }

//    @Override
//    public <R> Page<R> queryPage(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<R>> query,
//                                 TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<Long>> count,
//                                 Class<R> returnType,
//                                 Pageable pageable) {
//        log.trace("executing paginated custom query");
//        val wrapper = new AtomicReference<CriteriaQuery<R>>();
//        List<R> result = this.getTypedQuery((root, cb, cq) -> {
//            query.accept(root, cb, cq);
//            wrapper.set(cq);
//        }, returnType, pageable).getResultList();
//        List<Long> countList = getCountQuery(wrapper.get(), count).getResultList();
//        return PageableExecutionUtils.getPage(result, pageable, () -> countList.size() > 1 ? countList.size() : countList.get(0));
//    }

    private <R> TypedQuery<R> getTypedQuery(
            TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<R>> consumer,
            Class<R> returnType,
            Pageable pageable
    ) {
        return getTypedQuery((root, cb, cq) -> {
            consumer.accept(root, cb, cq);
            setUpSort(pageable, cb, cq, root);
        }, returnType, new Limit((int) pageable.getOffset(), pageable.getPageSize()));
    }

    private <R> TypedQuery<R> getTypedQuery(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<R>> consumer, Class<R> returnType, Limit limit) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<R> criteriaQuery = criteriaBuilder.createQuery(returnType);
        var root = criteriaQuery.from(this.getEntityClass());
        consumer.accept(root, criteriaBuilder, criteriaQuery);
        return createQuery(limit, criteriaQuery);
    }

    private TypedQuery<T> getTypedQuery(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<T>> consumer, Limit limit) {
        var cb = manager.getCriteriaBuilder();
        var cq = cb.createQuery(this.getEntityClass());
        var root = cq.from(this.getEntityClass());
        consumer.accept(root, cb, cq);
        return createQuery(limit, cq);
    }

    private TypedQuery<T> getTypedQuery(TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<T>> consumer, Pageable pageable) {
        return getTypedQuery((root, cb, cq) -> {
            consumer.accept(root, cb, cq);
            setUpSort(pageable, cb, cq, root);
        }, new Limit((int) pageable.getOffset(), pageable.getPageSize()));
    }

    private <R> TypedQuery<R> createQuery(Limit limit, CriteriaQuery<R> criteriaQuery) {
        var query = manager.createQuery(criteriaQuery);
        if (limit != null) {
            query.setFirstResult(limit.offset());
            query.setMaxResults(limit.limit());
        }
        return query;
    }

    private <R> void setUpSort(Pageable pageable, CriteriaBuilder cb, CriteriaQuery<R> cq, Root<?> root) {
        val orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
        orders.addAll(cq.getOrderList());
        cq.orderBy(orders);
    }

    private <R> TypedQuery<Long> getCountQuery(CriteriaQuery<R> query, TriConsumer<Root<T>, CriteriaBuilder, CriteriaQuery<Long>> consumer) {
        log.trace("executing page count query");
        return getTypedQuery((countRoot, cb, cq) -> {
            consumer.accept(countRoot, cb, cq);
            if (query.isDistinct()) {
                cq.select(cb.countDistinct(countRoot));
            } else {
                cq.select(cb.count(countRoot));
            }
        }, Long.class, (Limit) null);
    }

    private Class<T> getEntityClass() {
        return this.entityInformation.getJavaType();
    }
}
