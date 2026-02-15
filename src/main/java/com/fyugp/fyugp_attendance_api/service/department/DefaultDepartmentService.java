package com.fyugp.fyugp_attendance_api.service.department;

import com.fyugp.fyugp_attendance_api.dto.DepartmentSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.repositories.DepartmentRepository;
import com.fyugp.fyugp_attendance_api.server.model.CreateDepartmentRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateDepartmentRequest;
import com.fyugp.fyugp_attendance_api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultDepartmentService implements DepartmentService{

    private final DepartmentRepository departmentRepository;
    private final UserService userService;
    @Override
    public Department getDepartment(Long id) {
        log.info("getDepartment");
        Optional<Department> department;
        try {
            department = departmentRepository.findById(id);
        }catch (Exception e){
            throw new AppException("errors.failedToFetchDepartment");
        }
        return department.orElseThrow(() -> new AppException("errors.departmentNotFound"));
    }

    @Override
    public Department createDepartment(CreateDepartmentRequest department) {
        log.info("createDepartment");
        User user= null;
        if (department.getHeadOfDepartment()!=null) {
            user = userService.getUserDetailsById(department.getHeadOfDepartment());
        }
        Department department1 = Department.builder()
                .name(department.getName())
                .description(department.getDescription())
                .headOfDepartment(user)
                .build();
        try {
        return  departmentRepository.save(department1);
        }catch (Exception e){
            throw new AppException("errors.failedToCreateDepartment");
        }

    }

    @Override
    public Department updateDepartment(UpdateDepartmentRequest request, Long id) {
        log.info("update department by id:{}", id);
        var department = getDepartment(id);
        department.setName(request.getName());
        if (request.getHeadOfDepartment()!=null){
            department.setHeadOfDepartment(userService.getUserDetailsById(request.getHeadOfDepartment()));
        }
        department.setDescription(request.getDescription());
        try {
            return departmentRepository.save(department);
        }catch (Exception e){
            throw new AppException("errors.failedToUpdateDepartment");
        }
    }

    @Override
    public void deleteDepartment(Long id) {
    log.info("delete department");
    var department = getDepartment(id);
    department.setDeleted(true);
    try {
        departmentRepository.save(department);
    }catch (Exception e){
        throw new AppException("errors.failedToDeleteDepartment");
    }
    }

    @Override
    public Page<Department> listAllDepartments(Pageable pageable, DepartmentSearch subjectSearch) {
        log.info("list all departments");
        Specification<Department> spec = Specification.where(subjectSearch);
        try {
            return departmentRepository.findAll(spec, pageable);
        }catch (Exception e){
            throw new AppException("errors.failedToListDepartments");
        }
    }
}
