package com.fyugp.fyugp_attendance_api.controller;

import com.fyugp.fyugp_attendance_api.dto.DepartmentSearch;
import com.fyugp.fyugp_attendance_api.dto.StudentSearch;
import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.student.Student;
import com.fyugp.fyugp_attendance_api.server.api.StudentApi;
import com.fyugp.fyugp_attendance_api.server.model.*;
import com.fyugp.fyugp_attendance_api.service.student.StudentService;
import com.fyugp.fyugp_attendance_api.utils.Message;
import com.fyugp.fyugp_attendance_api.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StudentController  implements StudentApi {

    private final StudentService studentService;
    private final Message message;


    @Override
    public ResponseEntity<StudentResponse> createStudent(CreateStudentRequest request) {
        final var subjectType = studentService.createStudent(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(StudentResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message.getMessage("messages.successfullyCreatedNewStudent"))
                .status(true)
                .code(HttpStatus.CREATED.value())
                .data(Collections.singletonList(createStudentResponse(subjectType)))
                .build());
    }


    @Override
    public ResponseEntity<GetStudentDtoResponse> getAllStudents(Integer page, Integer size, String sort, String search,Long departmentId) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        StudentSearch userSearch = StudentSearch.builder()
                .departmentId(departmentId)
                .search(search)
                .build();
        Page<Student> usersPage = studentService.listAllStudents(pageable, userSearch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetStudentDtoResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedStudent"))
                        .data(Collections.singletonList(createUserPageDto(usersPage)))
                        .build());
    }

    @Override
    public ResponseEntity<StudentResponse> getStudentById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                StudentResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("messages.successfullyFetchedStudent"))
                        .status(true)
                        .data(Collections.singletonList(createStudentResponse(studentService.getStudent(id))))
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> softDeleteStudent(Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullySoftDeletedStudent"))
                        .build()
        );
    }

    @Override
    public ResponseEntity<StudentResponse> updateStudentById(Long id, UpdateStudentRequest request) {
        var subjectType = studentService.updateStudent(request,id);
        return ResponseEntity.status(HttpStatus.OK).body(StudentResponse.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.OK.value())
                .message(message.getMessage("messages.successfullyUpdatedStudent"))
                .status(true)
                .data(Collections.singletonList(createStudentResponse(subjectType))
                ).build());
    }

    private StudentDto createStudentResponse(Student student){
        return StudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .registrationNumber(student.getRegistrationNumber())
                .department(createDepartmentDto(student.getDepartment()))
                .build();
    }
    private StudentDtoPage createUserPageDto(Page<Student> usersPage) {
        return StudentDtoPage.builder()
                .page((long) usersPage.getNumber())
                .size(usersPage.getSize())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .totalElements(usersPage.getTotalElements())
                .totalPages((long) usersPage.getTotalPages())
                .data(usersPage.getContent().stream()
                        .map(user -> createStudentResponse(user))
                        .collect(Collectors.toList()))
                .build();
    }

    private DepartmentShortDto createDepartmentDto(Department department){
        if (department==null){
            return null;
        }
        return DepartmentShortDto.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
