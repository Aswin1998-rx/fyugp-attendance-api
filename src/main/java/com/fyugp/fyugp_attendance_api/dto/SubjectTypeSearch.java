package com.fyugp.fyugp_attendance_api.dto;


import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType_;
import com.fyugp.fyugp_attendance_api.models.subject.Subject_;
import com.fyugp.fyugp_attendance_api.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * SubjectType search.
 */
@Builder
public class SubjectTypeSearch implements Specification<SubjectType> {

    private String search;
    private List<Long> ids;

    @Override
    public Predicate toPredicate(Root<SubjectType> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            Predicate predicate = SpecificationUtils.search(SubjectType.class, search.trim())
                    .toPredicate(root, query, criteriaBuilder);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        if (ids != null && !ids.isEmpty()) {

            predicates.add(root.get(SubjectType_.ID).in(ids));

        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
