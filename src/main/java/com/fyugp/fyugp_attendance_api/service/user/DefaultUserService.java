package com.fyugp.fyugp_attendance_api.service.user;


import com.fyugp.fyugp_attendance_api.dto.UserCreateDto;
import com.fyugp.fyugp_attendance_api.dto.UserSearch;
import com.fyugp.fyugp_attendance_api.dto.UserUpdateDto;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.exceptions.BadRequestException;
import com.fyugp.fyugp_attendance_api.exceptions.HttpStatusException;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.repositories.UserRepository;
import com.fyugp.fyugp_attendance_api.server.model.AssignRolesToUserRequest;
import com.fyugp.fyugp_attendance_api.service.role.RoleService;
import com.fyugp.fyugp_attendance_api.utils.EmailUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * User service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    @Lazy
    private final RoleService roleService;



    @Override
    public User createUser(UserCreateDto dto) {
        log.debug("adding new user {}", dto.getEmail());
        String userName = EmailUtil.extractUserName(dto.getEmail());
        if (userName == null) {
            throw new BadRequestException("errors.invalidEmail");
        }

        if (checkIfUserAlreadyExists(dto.getEpn(), dto.getEmail(), userName)) {
            throw new BadRequestException("errors.userExists");
        }

        try {
            User user = new User();
            user.setUserName(userName);
            user.setName(dto.getName());
            user.setDesignation(dto.getDesignation());
            user.setEpn(dto.getEpn());
            user.setEmail(dto.getEmail());
            user.setMobileNumber(dto.getMobileNumber());
            user.setEnabled(true);
            user.setAccountLocked(dto.isAccountLocked());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("errors.failedToSaveUser", e);
        }
    }


    @Override
    public User getUserDetailsById(Long id) {
        log.debug("fetching user details using id {} ", id);
        Optional<User> user;
        try {
            user = userRepository.findById(id);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchUser", e);
        }
        return user.orElseThrow(() -> new EntityNotFoundException("errors.userNotFound"));
    }

    @Override
    public User getByEmail(String email) {
        log.debug("fetching user by email {}", email);
        Optional<User> user;
        try {
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchUser", e);
        }
        return user.orElseThrow(() -> new EntityNotFoundException("errors.userNotFound"));
    }


    @Override
    public User getByUsername(String username) {
        log.debug("fetching user by username {}", username);
        Optional<User> user;
        try {
            user = userRepository.findByUserName(username);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchUser", e);
        }
        return user.orElseThrow(() -> new EntityNotFoundException("errors.userNotFound"));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable, UserSearch userSearch) {
        log.debug("getting all users");
        try {
            return userRepository.findAll(userSearch, pageable);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchUsersList", e);
        }
    }

    @Override
    public User getLoggedInUserDetailsById() {
        log.debug("getting logged in user details");
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.getUserDetailsById(loggedInUser.getId());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loading user by username {}", username);
        try {
            User users = userRepository.findByUserName(username)
                    .orElseThrow(() -> new EntityNotFoundException("errors.userNotFound"));
            return new User(users.getUsername(), users.getPassword());
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("errors.userNotFound" + e);
        }
    }

    @Override
    public void softDeleteUser(Long id) {
        log.debug("soft deleting user having id {} ", id);
        User user = this.getUserDetailsById(id);
        user.setDeleted(Boolean.TRUE);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("errors.failedToSoftDeletedUser", e);
        }
    }

    @Override
    public User updateUser(Long id, UserUpdateDto dto) {
        log.debug("updating user details of id {} ", id);
        User user = this.getUserDetailsById(id);
        return this.updateUser(user, dto);
    }

    @Override
    public User updateUser(User user, UserUpdateDto updateDto) {
        log.debug("updating user entity {}", user.getId());
        if (updateDto.getEpn() != null && checkIfUserAlreadyPresentByEpn(updateDto.getEpn())) {
            throw new BadRequestException("errors.userExists");
        }
        user.setName(Optional.ofNullable(updateDto.getName()).orElse(user.getName()));
        user.setMobileNumber(Optional.ofNullable(updateDto.getMobileNumber()).orElse(user.getMobileNumber()));
        user.setDesignation(Optional.ofNullable(updateDto.getDesignation()).orElse(user.getDesignation()));
        user.setEpn(Optional.ofNullable(updateDto.getEpn()).orElse(user.getEpn()));
        user.setFailedLoginAttempt(Optional.ofNullable(updateDto.getFailedLoginAttempt()).orElse(user.getFailedLoginAttempt()));
        final var lockUser = Optional.ofNullable(updateDto.getAccountLocked());
        user.setAccountLocked(Optional.ofNullable(updateDto.getAccountLocked()).orElse(user.getAccountLocked()));
        user.setEnabled(Optional.ofNullable(updateDto.getEnabled()).orElse(user.getEnabled()));
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("errors.failedToUpdateUser", e);
        }
    }


    @Override
    public void assignRolesToUser(Long userId, AssignRolesToUserRequest request) {
        log.debug("assigning roles to user having id {} ", userId);
        User user = this.getUserDetailsById(userId);
        List<Role> rolesToAdd = roleService.findAllRolesByIds(request.getRoleIds());
        boolean hasDeletedRole = rolesToAdd.stream()
                .anyMatch(role -> role.getDeleted());
        if (hasDeletedRole) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "errors.roleNotFound");
        }
        try {
            user.setRoles(rolesToAdd);
            userRepository.save(user);
        } catch (Exception ex) {
            throw new AppException("errors.failedToAssignRolesToUser", ex);
        }
    }

    private boolean checkIfUserAlreadyExists(
            String epn,
            String email,
            String username
    ) {
        log.debug("checking if user with epn - {}, email - {}, username - {} exists", epn, email, username);
        try {
            return userRepository.existsByUserNameOrEmailOrEpn(username, email, epn);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchUser", e);
        }
    }

    private boolean checkIfUserAlreadyPresentByEpn(String epn) {
        log.debug("checking if {} exists", epn);
        Optional<User> user;
        try {
            user = userRepository.findByEpn(epn);
        } catch (Exception ex) {
            throw new AppException("errors.failedToFetchUser", ex);
        }
        return user.isPresent();
    }
}
