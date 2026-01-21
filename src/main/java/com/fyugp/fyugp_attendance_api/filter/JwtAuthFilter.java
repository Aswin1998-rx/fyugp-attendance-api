package com.fyugp.fyugp_attendance_api.filter;



import com.fyugp.fyugp_attendance_api.Constants;
import com.fyugp.fyugp_attendance_api.service.jwt.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;




/**
 * An auth filter that checks for jwt bearer tokens in authorization header.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("authorizing request to api");
        var token = extractToken(request);
        if (token != null) {
            var user = tokenService.validateAndParse(token);
            if (user != null) {
                log.debug("user authorized");
                var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        log.debug("extracting jwt token from request Authorization header");
        var authHeader = request.getHeader(Constants.AUTHORIZATION_HEADER_NAME);
        return isNull(authHeader) || !authHeader.startsWith(Constants.BEARER_TOKEN_PREFIX)
                ? null
                : authHeader.substring(Constants.BEARER_TOKEN_PREFIX.length());
    }
}
