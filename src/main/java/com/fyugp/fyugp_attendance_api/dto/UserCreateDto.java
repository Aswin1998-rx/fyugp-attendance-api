package com.fyugp.fyugp_attendance_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO class for creating users.
 *
 */
@Getter
@Builder
public class UserCreateDto {
    private String name;

    @Email(message = "{messages.emailShouldBeValid}")
    @NotNull(message = "{messages.emailIsRequired}")
    private String email;

    private String designation;

    @NotNull(message = "{messages.epnIsRequired}")
    private String epn;

    private boolean accountLocked;

    private boolean enabled;

    private String mobileNumber;
}
