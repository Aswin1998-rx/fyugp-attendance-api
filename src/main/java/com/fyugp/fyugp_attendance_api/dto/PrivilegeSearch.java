package com.fyugp.fyugp_attendance_api.dto;


import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
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
 * Privilege search.
 */
@Builder
public class PrivilegeSearch implements Specification<Privilege> {

    private String search;

    @Override
    public Predicate toPredicate(Root<Privilege> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            Predicate predicate = SpecificationUtils.search(Privilege.class, search.trim())
                    .toPredicate(root, query, criteriaBuilder);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
