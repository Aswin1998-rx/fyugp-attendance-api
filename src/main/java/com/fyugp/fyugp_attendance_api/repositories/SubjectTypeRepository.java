package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Role repository.
 */
public interface SubjectTypeRepository extends JpaRepository<SubjectType, Long>, JpaSpecificationExecutor<SubjectType> {
    Optional<SubjectType> findByName(String name);
}
