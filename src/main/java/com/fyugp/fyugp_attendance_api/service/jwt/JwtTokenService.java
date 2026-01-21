package com.fyugp.fyugp_attendance_api.service.jwt;


import com.fyugp.fyugp_attendance_api.models.user.User;
import jakarta.annotation.Nullable;

/**
 * The abstract specification for Jwt management services.
 */
public interface JwtTokenService {
    /**
     * Creates a jwt token with the provided user details.
     *
     * @param user the user for which the token should be created
     * @return jwt token.
     */
    String createAccessToken(User user);

    String createRefreshToken(User user);

    /**
     * Validates and parses a jwt token to the user details.
     *
     * @param token the token to be parsed
     * @return the user details in the jwt token or null if the jwt token is invalid in some way.
     */
    @Nullable
    User validateAndParse(String token);
}
