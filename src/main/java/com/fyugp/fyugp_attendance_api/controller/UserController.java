package com.fyugp.fyugp_attendance_api.controller;

import com.fyugp.fyugp_attendance_api.dto.UserSearch;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.model.GetUsersResponse;
import com.fyugp.fyugp_attendance_api.service.user.UserService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import com.fyugp.fyugp_attendance_api.server.api.UsersApi;
import com.fyugp.fyugp_attendance_api.server.model.AssignRolesToUserRequest;
import com.fyugp.fyugp_attendance_api.server.model.CreateUserRequest;
import com.fyugp.fyugp_attendance_api.server.model.GenericResponse;
import com.fyugp.fyugp_attendance_api.server.model.UserDetailedResponse;
import com.fyugp.fyugp_attendance_api.server.model.UserDetailedDto;
import com.fyugp.fyugp_attendance_api.server.model.UserDto;
import com.fyugp.fyugp_attendance_api.server.model.UserPage;
import com.fyugp.fyugp_attendance_api.server.model.UserProfileDto;
import com.fyugp.fyugp_attendance_api.server.model.UserProfileResponse;
import com.fyugp.fyugp_attendance_api.server.model.UserResponse;
import com.fyugp.fyugp_attendance_api.server.model.UpdateUserRequest;
import com.fyugp.fyugp_attendance_api.dto.UserCreateDto;
import com.fyugp.fyugp_attendance_api.dto.UserUpdateDto;
import com.fyugp.fyugp_attendance_api.server.model.RoleDetailedDto;
import com.fyugp.fyugp_attendance_api.server.model.PrivilegeDetailedDto;


import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * User controller.
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final Message message;

    @Override
    @PreAuthorize("hasAuthority('LIST_USERS')")
    public ResponseEntity<com.fyugp.fyugp_attendance_api.server.model.GetUsersResponse> getAllUsers(Integer page, Integer size, String sort, String search, List<Long> ids) {
        Sort sorting = SortUtil.sortToSortType(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        UserSearch userSearch = UserSearch.builder()
                .search(search)
                .ids(ids)
                .build();
        Page<User> usersPage = userService.getAllUsers(pageable, userSearch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GetUsersResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyListedUsers"))
                        .data(Collections.singletonList(createUserPageDto(usersPage)))
                        .build()
        );
    }

    @Override
    public ResponseEntity<UserDetailedResponse> getInternalUserById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                UserDetailedResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("message.successfullyFetchedUserDetails"))
                        .status(true)
                        .data(Collections.singletonList(createUserDetailedDto(userService.getUserDetailsById(id))))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ASSIGN_USER_ROLE')")
    public ResponseEntity<GenericResponse> assignRolesToUser(Long userId, AssignRolesToUserRequest assignRolesToUserRequest) {
        userService.assignRolesToUser(userId, assignRolesToUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyAssignedRolesToUser"))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        final var userDetails = userService.createUser(
                UserCreateDto.builder()
                        .name(createUserRequest.getName())
                        .designation(createUserRequest.getDesignation())
                        .email(createUserRequest.getEmail())
                        .epn(createUserRequest.getEpn())
                        .mobileNumber(createUserRequest.getMobileNumber())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message.getMessage("messages.successfullyCreatedNewUser"))
                .status(true)
                .code(HttpStatus.CREATED.value())
                .data(Collections.singletonList(createUserDto(userDetails)))
                .build());
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<UserResponse> updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User userDetails = userService.updateUser(
                id,
                UserUpdateDto.builder()
                        .name(updateUserRequest.getName())
                        .designation(updateUserRequest.getDesignation())
                        .mobileNumber(updateUserRequest.getMobileNumber())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.OK).body(UserResponse.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.OK.value())
                .message(message.getMessage("messages.successfullyUpdatedUserDetails"))
                .status(true)
                .data(Collections.singletonList(createUserDto(userDetails))
                ).build());
    }

    @Override
    public ResponseEntity<UserProfileResponse> getLoggedInUserDetails() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var privileges = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                UserProfileResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("message.successfullyFetchedUserDetails"))
                        .status(true)
                        .data(Collections.singletonList(createUserProfileDto(userService.getLoggedInUserDetailsById(), privileges)))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('FETCH_USER')")
    public ResponseEntity<UserDetailedResponse> getUserById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                UserDetailedResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .message(message.getMessage("message.successfullyFetchedUserDetails"))
                        .status(true)
                        .data(Collections.singletonList(createUserDetailedDto(userService.getUserDetailsById(id))))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('SOFT_DELETE_USER')")
    public ResponseEntity<GenericResponse> softDeleteUser(Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("message.successfullySoftDeletedUser"))
                        .build()
        );
    }

    private UserPage createUserPageDto(Page<User> usersPage) {
        return UserPage.builder()
                .page((long) usersPage.getNumber())
                .size(usersPage.getSize())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .totalElements(usersPage.getTotalElements())
                .totalPages((long) usersPage.getTotalPages())
                .data(usersPage.getContent().stream()
                        .map(user -> createUserDto(user))
                        .collect(Collectors.toList()))
                .build();
    }

    private UserDto createUserDto(User userDetails) {
        return UserDto.builder()
                .id(userDetails.getId())
                .name(userDetails.getName())
                .userName(userDetails.getUsername())
                .email(userDetails.getEmail())
                .epn(userDetails.getEpn())
                .designation(userDetails.getDesignation())
                .enabled(userDetails.getEnabled())
                .deleted(userDetails.getDeleted())
                .mobileNumber(userDetails.getMobileNumber())
                .build();
    }

    private UserProfileDto createUserProfileDto(User userDetails, List<String> privileges) {
        return UserProfileDto.builder()
                .id(userDetails.getId())
                .name(userDetails.getName())
                .userName(userDetails.getUsername())
                .email(userDetails.getEmail())
                .epn(userDetails.getEpn())
                .designation(userDetails.getDesignation())
                .enabled(userDetails.getEnabled())
                .deleted(userDetails.getDeleted())
                .privileges(privileges)
                .build();
    }

    private UserDetailedDto createUserDetailedDto(User userDetails) {
        return UserDetailedDto.builder()
                .id(userDetails.getId())
                .name(userDetails.getName())
                .userName(userDetails.getUsername())
                .email(userDetails.getEmail())
                .epn(userDetails.getEpn())
                .mobileNumber(userDetails.getMobileNumber())
                .designation(userDetails.getDesignation())
                .enabled(userDetails.getEnabled())
                .deleted(userDetails.getDeleted())
                .roles(userDetails.getRoles().stream()
                        .map(role -> createRoleResponse(role))
                        .collect(Collectors.toList()))
                .build();
    }

    private RoleDetailedDto createRoleResponse(Role role) {
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
