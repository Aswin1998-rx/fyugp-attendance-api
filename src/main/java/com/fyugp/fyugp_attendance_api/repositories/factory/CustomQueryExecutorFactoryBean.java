package com.fyugp.fyugp_attendance_api.repositories.factory;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.repositories.CustomQueryExecutor;
import com.fyugp.fyugp_attendance_api.repositories.CustomQueryExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;

import java.io.Serializable;

/**
 * This class overrides spring-data repository factory to provide entity information to
 * <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations">repository fragments.</a>.
 *
 * @see JpaRepositoryFactoryBean
 */
public class CustomQueryExecutorFactoryBean<R extends JpaRepository<T, Serializable>, T extends BaseEntity>
        extends JpaRepositoryFactoryBean<R, T, Serializable> {

    public CustomQueryExecutorFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new CustomQueryExecutorFactory<T>(entityManager);
    }

    private static class CustomQueryExecutorFactory<T extends BaseEntity> extends JpaRepositoryFactory {
        private final EntityManager manager;

        public CustomQueryExecutorFactory(EntityManager entityManager) {
            super(entityManager);
            this.manager = entityManager;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
            var fragments = super.getRepositoryFragments(metadata);
            if (CustomQueryExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                var info = this.<T, Serializable>getEntityInformation((Class<T>) metadata.getDomainType());
                fragments = fragments.append(RepositoryFragment.implemented(CustomQueryExecutor.class, new CustomQueryExecutorImpl<>(manager, info)));
            }
            return fragments;
        }
    }
}
