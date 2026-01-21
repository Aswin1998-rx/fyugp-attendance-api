package com.fyugp.fyugp_attendance_api.service.auth;


import com.fyugp.fyugp_attendance_api.server.model.LoginRequest;
import com.fyugp.fyugp_attendance_api.server.model.TokenResponse;

/**
 * The abstract specification for authentication service functions.
 */
public interface AuthService {
    TokenResponse passwordLogin(LoginRequest request);

    TokenResponse refreshToken();

    void logOut();

}
