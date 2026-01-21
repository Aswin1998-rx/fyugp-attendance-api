package com.fyugp.fyugp_attendance_api.controller;


import com.fyugp.fyugp_attendance_api.service.auth.AuthService;
import com.fyugp.fyugp_attendance_api.utils.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import  com.fyugp.fyugp_attendance_api.server.api.AuthApi;
import com.fyugp.fyugp_attendance_api.server.model.GenericResponse;
import com.fyugp.fyugp_attendance_api.server.model.LoginApiResponse;
import com.fyugp.fyugp_attendance_api.server.model.LoginRequest;

import java.time.OffsetDateTime;
import java.util.Collections;

/**
 * Controller for authentication apis.
 */
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final Message message;


    @Override
    public ResponseEntity<LoginApiResponse> passwordLogin(LoginRequest loginRequest) {
        final var tokenResponse = authService.passwordLogin(loginRequest);
        return ResponseEntity.ok(
                LoginApiResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.loginSuccessful"))
                        .data(Collections.singletonList(tokenResponse))
                        .build()
        );
    }


    @Override
    @PreAuthorize("hasAuthority('REFRESH_TOKEN')")
    public ResponseEntity<LoginApiResponse> refreshToken() {
        final var tokenResponse = authService.refreshToken();
        return ResponseEntity.ok(
                LoginApiResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.OK.value())
                        .status(true)
                        .message(message.getMessage("messages.successfullyRefreshedToken"))
                        .data(Collections.singletonList(tokenResponse))
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> userLogout(){
       authService.logOut();
       return ResponseEntity.status(HttpStatus.OK).body(
               GenericResponse.builder()
                       .timestamp(OffsetDateTime.now())
                       .code(HttpStatus.OK.value())
                       .status(true)
                       .message(message.getMessage("messages.successfullyLoggedOut"))
                       .build()
       );
   }


}
