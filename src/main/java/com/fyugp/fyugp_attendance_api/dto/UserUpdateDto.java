package com.fyugp.fyugp_attendance_api.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * This class is used to update a user partially.
 *
 * @see com.alphastarav.identity.services.user.UserService#updateUser
 */
@Getter
@Builder
public class UserUpdateDto {
    private String name;
    private String designation;
    private String epn;

    private String mobileNumber;
    private Long failedLoginAttempt;
    private Boolean accountLocked;
    private Boolean enabled;
}
