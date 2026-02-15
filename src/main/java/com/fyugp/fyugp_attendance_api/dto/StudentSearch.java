package com.fyugp.fyugp_attendance_api.dto;


import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.department.Department_;
import com.fyugp.fyugp_attendance_api.models.student.Student;
import com.fyugp.fyugp_attendance_api.models.student.Student_;
import com.fyugp.fyugp_attendance_api.utils.SpecificationUtils;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Student search.
 */
@Builder
public class StudentSearch implements Specification<Student> {

    private String search;
    private Long departmentId;


    @Override
    public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            Predicate predicate = SpecificationUtils.search(Student.class, search.trim())
                    .toPredicate(root, query, criteriaBuilder);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        if (departmentId != null) {
            predicates.add(criteriaBuilder.equal(root.get(Student_.DEPARTMENT).get(Department_.ID), departmentId));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
