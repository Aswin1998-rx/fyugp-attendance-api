package com.fyugp.fyugp_attendance_api.controller;


import com.fyugp.fyugp_attendance_api.dto.DepartmentSearch;
import com.fyugp.fyugp_attendance_api.dto.SubjectSearch;
import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.api.DepartmentsApi;
import com.fyugp.fyugp_attendance_api.server.model.*;
import com.fyugp.fyugp_attendance_api.service.department.DepartmentService;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DepartmentController implements DepartmentsApi {

    private final DepartmentService departmentService;
    private final Message message;

    @Override
    public ResponseEntity<DepartmentResponse> createDepartments(CreateDepartmentRequest request) {
        final var subjectType = departmentService.createDepartment(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message.getMessage("messages.successfullyCreatedNewDepartment"))
                .status(true)
                .code(HttpStatus.CREATED.value())
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType)))
                .build());
    }

    @Override
    public ResponseEntity<GetDepartmentResponse> getAllDepartments(Integer page, Integer size, String sort, String search) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        DepartmentSearch userSearch = DepartmentSearch.builder()
                .search(search)
                .build();
        Page<Department> usersPage = departmentService.listAllDepartments(pageable, userSearch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetDepartmentResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedDepartment"))
                        .data(Collections.singletonList(createUserPageDto(usersPage)))
                        .build());
    }

    @Override
    public ResponseEntity<DepartmentResponse> getDepartmentById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                DepartmentResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("messages.successfullyFetchedDepartment"))
                        .status(true)
                        .data(Collections.singletonList(createSubjectTypeResponse(departmentService.getDepartment(id))))
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> softDeleteDepartment(Long id) {

        departmentService.deleteDepartment(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullySoftDeletedDepartment"))
                        .build()
        );
    }

    @Override
    public ResponseEntity<DepartmentResponse> updateDepartmentsById(Long id, UpdateDepartmentRequest request) {
        var subjectType = departmentService.updateDepartment(request,id);
        return ResponseEntity.status(HttpStatus.OK).body(DepartmentResponse.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.OK.value())
                .message(message.getMessage("messages.successfullyUpdatedDepartment"))
                .status(true)
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType))
                ).build());
    }



    private DepartmentDto createSubjectTypeResponse(Department subjectType){
        return DepartmentDto.builder()
                .id(subjectType.getId())
                .name(subjectType.getName())
                .description(subjectType.getDescription())
                .headOfDepartment(createUserDtoResponse(subjectType.getHeadOfDepartment()))
                .build();
    }

    private UserDto createUserDtoResponse(User user){
        if (user==null){
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
    private DepartmentPage createUserPageDto(Page<Department> usersPage) {
        return DepartmentPage.builder()
                .page((long) usersPage.getNumber())
                .size(usersPage.getSize())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .totalElements(usersPage.getTotalElements())
                .totalPages((long) usersPage.getTotalPages())
                .data(usersPage.getContent().stream()
                        .map(user -> createSubjectTypeResponse(user))
                        .collect(Collectors.toList()))
                .build();
    }
}
