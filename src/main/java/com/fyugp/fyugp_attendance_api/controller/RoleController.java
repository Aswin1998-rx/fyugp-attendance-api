package com.fyugp.fyugp_attendance_api.controller;


import com.fyugp.fyugp_attendance_api.dto.RoleSearch;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.server.api.RolesApi;
import com.fyugp.fyugp_attendance_api.server.model.*;
import com.fyugp.fyugp_attendance_api.service.role.RoleService;
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
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Role controller.
 */
@RestController
@RequiredArgsConstructor
public class RoleController implements RolesApi {

    private final RoleService roleService;
    private final Message message;

    @Override
    @PreAuthorize("hasAuthority('CREATE_ROLES')")
    public ResponseEntity<RoleResponse> createRole(CreateRoleRequest createRoleRequest) {
        final var role = roleService.createNewRole(createRoleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RoleResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.CREATED.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyCreatedNewRole"))
                        .data(Collections.singletonList(createRoleResponse(role)))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_ROLES')")
    public ResponseEntity<RoleResponse> updateRole(Long id, UpdateRoleRequest updateRoleRequest) {
        final var role = roleService.updateRoleDetailsUsingId(id, updateRoleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                RoleResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyUpdatedRoleDetails"))
                        .data(Collections.singletonList(createRoleResponse(role)))
                        .build()
        );
    }


    @Override
    @PreAuthorize("hasAuthority('LIST_ROLES')")
    public ResponseEntity<GetRolesResponse> getAllRoles(Integer page, Integer size, String sort, String search) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        RoleSearch roleSearch = RoleSearch.builder()
                .search(search)
                .build();
        Page<Role> rolesPage = roleService.getAllRoles(roleSearch, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetRolesResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedRoleDetails"))
                        .data(Collections.singletonList(createRolePageResponse(rolesPage)))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ASSIGN_ROLE_PRIVILEGE')")
    public ResponseEntity<GenericResponse> assignPrivilegesToRole(Long roleId, AssignPrivilegesToRoleRequest assignPrivilegesToRoleRequest) {
        roleService.assignPrivilegesToRole(roleId, assignPrivilegesToRoleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.assignPrivilegesToRole"))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('SOFT_DELETE_ROLE')")
    public ResponseEntity<GenericResponse> softDeleteRole(Long id) {
        roleService.softDeleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullySoftDeletedRole"))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ASSIGN_ROLE_APPLICATION')")
    public ResponseEntity<GenericResponse> addPrivilegesOfApplicationsToRole(Long roleId, AddApplicationsToRoleRequest addApplicationsToRoleRequest) {
        roleService.addPrivilegesOfApplicationToRole(roleId, addApplicationsToRoleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.addPrivilegesOfApplicationToRole"))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('FETCH_ROLE')")
    public ResponseEntity<RoleDetailedResponse> getRoleById(Long roleId) {
        final var role = roleService.getRoleById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(
                RoleDetailedResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedRoleDetails"))
                        .data(Collections.singletonList(createRoleDetailedResponse(role)))
                        .build()
        );
    }


    private RolePage createRolePageResponse(Page<Role> rolesPage) {
        return RolePage.builder()
                .page((long) rolesPage.getNumber())
                .size(rolesPage.getSize())
                .first(rolesPage.isFirst())
                .last(rolesPage.isLast())
                .totalElements(rolesPage.getTotalElements())
                .totalPages((long) rolesPage.getTotalPages())
                .data(rolesPage.getContent().stream()
                        .map(role -> createRoleResponse(role))
                        .collect(Collectors.toList()))
                .build();
    }

    private RoleDto createRoleResponse(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .deleted(role.getDeleted())
                .build();
    }

    private RoleDetailedDto createRoleDetailedResponse(Role role) {
        return RoleDetailedDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .deleted(role.getDeleted())
                .privilege(role.getPrivileges().stream()
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
