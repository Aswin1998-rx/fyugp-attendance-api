package com.fyugp.fyugp_attendance_api.controller;

import com.fyugp.fyugp_attendance_api.dto.SubjectSearch;
import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.dto.UserSearch;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.api.SubjectApi;
import com.fyugp.fyugp_attendance_api.server.model.*;
import com.fyugp.fyugp_attendance_api.service.subject.SubjectService;
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
public class SubjectController implements SubjectApi {

    private final SubjectService subjectService;
    private final Message message;

    @Override
    public ResponseEntity<SubjectResponse> createSubject(CreateSubjectRequest request) {
        final var subjectType = subjectService.createNewSubject(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(SubjectResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message.getMessage("messages.successfullyCreatedNewSubjectType"))
                .status(true)
                .code(HttpStatus.CREATED.value())
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType)))
                .build());
    }

    @Override
    public ResponseEntity<GetSubjectResponse> getAllSubjects(Integer page, Integer size, String sort, String search) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        SubjectSearch userSearch = SubjectSearch.builder()
                .search(search)
                .build();
        Page<Subject> usersPage = subjectService.listAllSubjects(pageable, userSearch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetSubjectResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("message.successfullyListedSubjectType"))
                        .data(Collections.singletonList(createUserPageDto(usersPage)))
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> softDeleteSubject(Long id) {
        subjectService.deleteSubjectById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("message.successfullySoftDeletedSubjectType"))
                        .build()
        );
    }

    @Override
    public ResponseEntity<SubjectResponse> updateSubject(Long id, UpdateSubjectRequest request) {
        var subjectType = subjectService.updateSubjectById(request,id);
        return ResponseEntity.status(HttpStatus.OK).body(SubjectResponse.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.OK.value())
                .message(message.getMessage("messages.successfullyUpdatedSubjectType"))
                .status(true)
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType))
                ).build());
    }

    @Override
    public ResponseEntity<SubjectResponse> getSubjectById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                SubjectResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("message.successfullyFetchedSubjectType"))
                        .status(true)
                        .data(Collections.singletonList(createSubjectTypeResponse(subjectService.getSubjectById(id))))
                        .build()
        );
    }

    private SubjectDto createSubjectTypeResponse(Subject subjectType){
        return SubjectDto.builder()
                .id(subjectType.getId())
                .name(subjectType.getName())
                .subjectType(createSubjectTypeResponse(subjectType.getSubjectType()))
                .build();
    }

    private SubjectTypeDto createSubjectTypeResponse(SubjectType subjectType){
        return SubjectTypeDto.builder()
                .id(subjectType.getId())
                .name(subjectType.getName())
                .build();
    }
    private SubjectPage createUserPageDto(Page<Subject> usersPage) {
        return SubjectPage.builder()
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
