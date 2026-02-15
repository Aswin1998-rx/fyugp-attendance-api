package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Student repository.
 */
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByName(String name);
}
