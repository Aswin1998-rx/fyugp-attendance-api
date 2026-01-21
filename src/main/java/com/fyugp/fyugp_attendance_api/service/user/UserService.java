package com.fyugp.fyugp_attendance_api.service.user;


import com.fyugp.fyugp_attendance_api.dto.UserCreateDto;
import com.fyugp.fyugp_attendance_api.dto.UserSearch;
import com.fyugp.fyugp_attendance_api.dto.UserUpdateDto;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.model.AssignRolesToUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * User service.
 */
public interface UserService extends UserDetailsService {
    User createUser(UserCreateDto dto);

    User getUserDetailsById(Long id);

    User getByEmail(String email);

    User getByUsername(String email);

    User updateUser(Long id, UserUpdateDto updateDto);

    User updateUser(User user, UserUpdateDto updateDto);

    Page<User> getAllUsers(Pageable pageable, UserSearch userSearch);

    void assignRolesToUser(Long userId, AssignRolesToUserRequest assignRolesToUserRequest);

    User getLoggedInUserDetailsById();

    void softDeleteUser(Long id);

}
