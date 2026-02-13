package com.fyugp.fyugp_attendance_api.dto;


import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.Subject_;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.models.user.User_;
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
 * Subject search.
 */
@Builder
public class SubjectSearch implements Specification<Subject> {

    private String search;
    private List<Long> ids;

    @Override
    public Predicate toPredicate(Root<Subject> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            Predicate predicate = SpecificationUtils.search(Subject.class, search.trim())
                    .toPredicate(root, query, criteriaBuilder);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        if (ids != null && !ids.isEmpty()) {

            predicates.add(root.get(Subject_.ID).in(ids));

        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
