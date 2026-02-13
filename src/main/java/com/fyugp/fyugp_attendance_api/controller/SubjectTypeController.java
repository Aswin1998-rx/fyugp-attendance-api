package com.fyugp.fyugp_attendance_api.controller;

import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.dto.UserCreateDto;
import com.fyugp.fyugp_attendance_api.dto.UserSearch;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.api.SubjectTypeApi;

import com.fyugp.fyugp_attendance_api.server.model.*;
import com.fyugp.fyugp_attendance_api.service.subjecttype.SubjectTypeService;
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
public class SubjectTypeController implements SubjectTypeApi {

    private final SubjectTypeService subjectTypeService;
    private final Message message;

    @Override
    public ResponseEntity<SubjectTypeResponse> createSubjectType(CreateSubjectTypeRequest createSubjectTypeRequest) {
        final var subjectType = subjectTypeService.createNewSubjectType(createSubjectTypeRequest);


        return ResponseEntity.status(HttpStatus.CREATED).body(SubjectTypeResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message.getMessage("messages.successfullyCreatedNewSubjectType"))
                .status(true)
                .code(HttpStatus.CREATED.value())
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType)))
                .build());
    }

    @Override
    public ResponseEntity<GetSubjectTypeResponse> getAllSubjectType(Integer page, Integer size, String sort, String search) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        SubjectTypeSearch userSearch = SubjectTypeSearch.builder()
                .search(search)
                .build();
        Page<SubjectType> usersPage = subjectTypeService.listAllSubjectTypes(pageable, userSearch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetSubjectTypeResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("message.successfullyListedSubjectType"))
                        .data(Collections.singletonList(createUserPageDto(usersPage)))
                        .build()
        );
    }

    @Override
    public ResponseEntity<SubjectTypeResponse> getSubjectTypeById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                SubjectTypeResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("message.successfullyFetchedSubjectType"))
                        .status(true)
                        .data(Collections.singletonList(createSubjectTypeResponse(subjectTypeService.getSubjectTypeById(id))))
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> softDeleteSubjectType(Long id) {
        subjectTypeService.deleteSubjectTypeById(id);
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
    public ResponseEntity<SubjectTypeResponse> updateSubjectType(Long id, UpdateSubjectTypeRequest updateSubjectTypeRequest) {
        var subjectType = subjectTypeService.updateSubjectTypeById(updateSubjectTypeRequest,id);
        return ResponseEntity.status(HttpStatus.OK).body(SubjectTypeResponse.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.OK.value())
                .message(message.getMessage("messages.successfullyUpdatedSubjectType"))
                .status(true)
                .data(Collections.singletonList(createSubjectTypeResponse(subjectType))
                ).build());
    }

    private SubjectTypeDto createSubjectTypeResponse(SubjectType subjectType){
        return SubjectTypeDto.builder()
                .id(subjectType.getId())
                .name(subjectType.getName())
                .build();
    }

    private SubjectTypePage createUserPageDto(Page<SubjectType> usersPage) {
        return SubjectTypePage.builder()
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
