package com.fyugp.fyugp_attendance_api.service.student;

import com.fyugp.fyugp_attendance_api.dto.StudentSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.models.student.Student;
import com.fyugp.fyugp_attendance_api.repositories.StudentRepository;
import com.fyugp.fyugp_attendance_api.server.model.CreateStudentRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateStudentRequest;
import com.fyugp.fyugp_attendance_api.service.department.DepartmentService;
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
public class DefaultStudentService implements StudentService{

    private final StudentRepository studentRepository;
    private final DepartmentService departmentService;


    @Override
    public Student getStudent(Long id) {
        log.info("fetching student by id :{}", id);
        Optional<Student> student ;
        try {
            student = studentRepository.findById(id);
        }catch (Exception e){
            throw new AppException("errors.failedToFetchStudent");
        }
        return student.orElseThrow(() -> new AppException("errors.studentNotFound"));
    }

    @Override
    public Student createStudent(CreateStudentRequest request) {
        log.info("creating new student");
        Student student = new Student();
        student.setName(request.getName());
        student.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getDepartmentId()!=null){
            student.setDepartment(departmentService.getDepartment(request.getDepartmentId()));
        }
        try {
            return studentRepository.save(student);
        }catch (Exception e){
            throw new AppException("errors.failedToCreateStudent");
        }


    }

    @Override
    public Student updateStudent(UpdateStudentRequest request, Long id) {
        log.info("updating student");
        Student student = new Student();
        student.setName(request.getName());
        student.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getDepartmentId()!=null){
            student.setDepartment(departmentService.getDepartment(request.getDepartmentId()));
        }
        try {
            return studentRepository.save(student);
        }catch (Exception e){
            throw new AppException("errors.failedToUpdateStudent");
        }
    }

    @Override
    public void deleteStudent(Long id) {
    log.info("deleting student by id:{}",id);
    Student student = getStudent(id);
    try {
        studentRepository.deleteById(id);
    }catch (Exception e){
        throw new AppException("errors.failedToDeleteStudent");
    }
    }

    @Override
    public Page<Student> listAllStudents(Pageable pageable, StudentSearch studentSearch) {
        log.info("list all students");
        Specification<Student> specification = Specification.where(studentSearch);
        try {
            return studentRepository.findAll(specification,pageable);
        }catch (Exception e){
            throw new AppException("errors.failedToListStudents");
        }
    }
}
