package com.fyugp.fyugp_attendance_api.utils;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.PluralAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class handles all utility functions related to specifications.
 */
public class SpecificationUtils {
    private static final Map<Class<?>, List<Field>> searchFieldMap = new HashMap<>();

    /**
     * This method allows for creating a specification for searching a
     * * term on all searchable fields of a model.
     * * @param cls    the class of the model to search
     * * @param search the term to search
     * * @param <T>    the type of model
     * * @return a specification that searches a term on  a model
     * * @see com.alphastarav.identity.utils.annotations.Searchable
     */
    public static <T> Specification<T> search(Class<T> cls, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            processClassField(search, root, cb, predicates, cls);
            root.getFetches().forEach(fetch -> processNestedClass(cls, search, root, cb, predicates, (Join<T, ?>) fetch));
            root.getJoins().forEach(join -> processNestedClass(cls, search, root, cb, predicates, join));
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void processNestedClass(
            Class<T> cls,
            String search,
            Root<T> root,
            CriteriaBuilder cb,
            List<Predicate> predicates,
            Join<T, ?> join
    ) {
        final var name = join.getAttribute().getName();
        final var isSearchable = getSearchableFields(cls)
                .stream()
                .anyMatch(field -> field.getName().equals(name) && field.isAnnotationPresent(Searchable.class));
        if (!isSearchable) {
            return;
        }
        final var joinPath = root.get(name);
        processClassField(search, joinPath, cb, predicates, joinPath.getModel().getBindableJavaType());
    }

    private static <T> void processClassField(
            String search,
            Path<T> root,
            CriteriaBuilder cb,
            List<Predicate> predicates,
            Class<?> cls
    ) {
        for (final var field : getSearchableFields(cls)) {
            Path<String> fieldPath = root.get(field.getName());
            if (BaseEntity.class.isAssignableFrom(fieldPath.getModel().getBindableJavaType())) {
                continue;
            }
            predicates.add(cb.like(fieldPath.as(String.class), "%" + search + "%"));
        }
    }

    private static List<Field> getSearchableFields(Class<?> cls) {
        return searchFieldMap.computeIfAbsent(
                cls,
                key -> Arrays.stream(cls.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Searchable.class))
                        .toList()
        );
    }



    @SuppressWarnings({"unchecked"})
    public static <X extends BaseEntity, Y extends BaseEntity> Join<X, Y> join(
            From<? extends BaseEntity, X> root,
            Attribute<X, Y> attribute,
            JoinType joinType
    ) {
        return (Join<X, Y>) getJoin(root, attribute.getName(), joinType);
    }

    @SuppressWarnings({"unchecked"})
    public static <X extends BaseEntity, Y extends BaseEntity> Join<X, Y> join(
            Join<? extends BaseEntity, ? extends Collection<X>> root,
            Attribute<X, Y> attribute,
            JoinType joinType
    ) {
        return (Join<X, Y>) getJoin(root, attribute.getName(), joinType);
    }


    @SuppressWarnings({"unchecked"})
    public static <X extends BaseEntity, Y extends BaseEntity, C extends Collection<Y>> Join<X, C> join(
            From<? extends BaseEntity, X> root,
            PluralAttribute<X, C, Y> attribute,
            JoinType type
    ) {
        return (Join<X, C>) getJoin(root, attribute.getName(), type);
    }

    @SuppressWarnings({"unchecked"})
    public static <X extends BaseEntity, Y extends BaseEntity, C extends Collection<Y>> Join<X, C> join(
            Join<? extends BaseEntity, ? extends Collection<X>> root,
            PluralAttribute<X, C, Y> attribute,
            JoinType type
    ) {
        return (Join<X, C>) getJoin(root, attribute.getName(), type);
    }

    private static <X extends BaseEntity> Join<?, ?> getJoin(From<X, ?> root, String name, JoinType type) {
        var join = root.getJoins()
                .stream()
                .filter(j -> j.getAttribute().getName().equals(name) && j.getJoinType().equals(type))
                .findFirst()
                .orElse(null);
        if (join != null) {
            return join;
        }
        return root.getFetches()
                .stream()
                .filter(fetch -> fetch.getAttribute().getName().equals(name) && fetch.getJoinType().equals(type))
                .findFirst()
                .map(fetch -> (Join<?, ?>) fetch)
                .orElseGet(() -> root.join(name, type));
    }

}
