package com.fyugp.fyugp_attendance_api.controller;


import com.fyugp.fyugp_attendance_api.dto.PrivilegeSearch;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.server.api.PrivilegesApi;
import com.fyugp.fyugp_attendance_api.server.model.GetPrivilegesResponse;
import com.fyugp.fyugp_attendance_api.server.model.PrivilegeDetailedDto;
import com.fyugp.fyugp_attendance_api.server.model.PrivilegePage;
import com.fyugp.fyugp_attendance_api.service.privilege.PrivilegeService;
import com.fyugp.fyugp_attendance_api.utils.Message;
import com.fyugp.fyugp_attendance_api.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Privilege controller.
 */
@RestController
@RequiredArgsConstructor
public class PrivilegeController implements PrivilegesApi {

    private final PrivilegeService privilegeService;
    private final Message message;

    @Override
    @PreAuthorize("hasAuthority('LIST_PRIVILEGES')")
    public ResponseEntity<GetPrivilegesResponse> getAllPrivileges(Integer page, Integer size, String sort, String search) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        PrivilegeSearch privilegeSearch = PrivilegeSearch.builder()
                .search(search)
                .build();
        final var privilegePage = privilegeService.getAllPrivileges(privilegeSearch, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetPrivilegesResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedPrivileges"))
                        .data(Collections.singletonList(createPrivilegePageResponse(privilegePage)))
                        .build()
        );
    }

    private PrivilegePage createPrivilegePageResponse(Page<Privilege> privilegePage) {
        return PrivilegePage.builder()
                .page((long) privilegePage.getNumber())
                .size(privilegePage.getSize())
                .first(privilegePage.isFirst())
                .last(privilegePage.isLast())
                .totalElements(privilegePage.getTotalElements())
                .totalPages((long) privilegePage.getTotalPages())
                .data(privilegePage.getContent().stream()
                        .map(privilege -> createPrivilegeResponse(privilege))
                        .collect(Collectors.toList()))
                .build();
    }

    private PrivilegeDetailedDto createPrivilegeResponse(Privilege privilege) {


        return PrivilegeDetailedDto.builder()
                .id(privilege.getId())
                .name(privilege.getName())
                .description(privilege.getDescription())
                .build();
    }

}
