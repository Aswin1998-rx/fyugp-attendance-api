package com.fyugp.fyugp_attendance_api.service.department;


import com.fyugp.fyugp_attendance_api.dto.DepartmentSearch;
import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.server.model.CreateDepartmentRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateDepartmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    Department getDepartment(Long id);

    Department createDepartment(CreateDepartmentRequest department);

    Department updateDepartment(UpdateDepartmentRequest department, Long id);

    void deleteDepartment(Long id);

    Page<Department> listAllDepartments(Pageable pageable, DepartmentSearch subjectSearch);

}
