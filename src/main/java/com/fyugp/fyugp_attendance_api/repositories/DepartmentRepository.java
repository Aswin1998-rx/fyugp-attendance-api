package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Department repository.
 */
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    Optional<Department> findByName(String name);
}
