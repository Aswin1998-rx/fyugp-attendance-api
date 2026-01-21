package com.fyugp.fyugp_attendance_api.service.auth;


import com.fyugp.fyugp_attendance_api.config.AppProperties;
import com.fyugp.fyugp_attendance_api.dto.UserUpdateDto;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.server.model.LoginRequest;
import com.fyugp.fyugp_attendance_api.server.model.TokenResponse;
import com.fyugp.fyugp_attendance_api.service.jwt.JwtTokenService;
import com.fyugp.fyugp_attendance_api.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Default implementation of {@link AuthService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;


    private final AuthenticationConfiguration authenticationConfiguration;

    private final AppProperties properties;

    private final JwtTokenService jwtTokenService;

    @Lazy
    private final UserService userService;





    @Override
    @Transactional(noRollbackFor = {BadCredentialsException.class, LockedException.class, DisabledException.class})
    public TokenResponse passwordLogin(LoginRequest request) {
        log.info("authenticating user access");
        var user = authenticateUser(request);
        user = updateUserOnLogin(user);
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        final var accessToken = jwtTokenService.createAccessToken(user);
        final var refreshToken = jwtTokenService.createRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refreshToken() {
        log.debug("refreshing token - returning new access token");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = userService.getUserDetailsById(user.getId());
        if (!loggedInUser.isEnabled()) {
            throw new DisabledException("errors.accountIsDisabled");
        }
        if (!user.isAccountNonLocked()) {
            log.debug("account access is locked for {}", user.getId());
            throw new LockedException("errors.accountIsLocked");
        }
        return TokenResponse.builder()
                .accessToken(jwtTokenService.createAccessToken(loggedInUser))
                .refreshToken(null)
                .build();
    }

    @Override
    public void logOut() {
        log.debug("logging out");

    }


    private User authenticateUser(LoginRequest request) {
        User user = null;
        try {
            user = userService.getByUsername(request.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword(), new ArrayList<>())
            );
            if (!user.isEnabled()) {
                throw new DisabledException("errors.accountIsDisabled");
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("errors.badCredentials", e);
        } catch (BadCredentialsException e) {
            if (user != null) {
                onLoginFailure(user);
            }
            throw new BadCredentialsException("errors.badCredentials", e);
        } catch (AuthenticationException e) {
            if (user != null) {
                onLoginFailure(user);
            }
            throw new BadCredentialsException("errors.badCredentials", e);
        }

    }


    private User updateUserOnLogin(User user) {
        if (user.isAccountNonLocked()) {
            return userService.updateUser(user, UserUpdateDto.builder()
                    .failedLoginAttempt(0L)
                    .build()
            );
        }

        log.debug("account access is locked for {}", user.getId());
        throw new LockedException("errors.accountIsLocked");
    }


    private void onLoginFailure(User user) {
        final var failedLoginAttempts = user.getFailedLoginAttempt() + 1;
        log.debug("user login failed, remaining attempts {}", properties.getUtils().maxLoginAttempts() - failedLoginAttempts);
        userService.updateUser(
                user,
                UserUpdateDto.builder()
                        .failedLoginAttempt(failedLoginAttempts)
                        .accountLocked(failedLoginAttempts >= properties.getUtils().maxLoginAttempts())
                        .build()
        );
        if (!user.isAccountNonLocked()) {
            log.debug("account is locked for {}", user.getId());
            throw new LockedException("errors.accountIsNowLocked");
        }
    }



}
