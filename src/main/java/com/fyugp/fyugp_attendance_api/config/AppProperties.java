package com.fyugp.fyugp_attendance_api.config;


import com.fyugp.fyugp_attendance_api.models.token.TokenType;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * App properties.
 */
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
public class AppProperties {

    private final Utils utils;
    private final ActiveDirectoryConfig activeDirectory;
    private final JwtConfig jwt;
    private final Cors cors;

    /**
     * The utility configuration.
     *
     */
    public record Utils(
            @Positive Long maxLoginAttempts,
            @NotNull long timeInSecondsToUnlockUser,
            @NotNull
            String timeZone,
            @NotNull
            @Email
            String trustedSender,
            @NotNull
            @NotEmpty
            Set<TokenUtils> tokenUtils
    ) {
    }

    /**
     * The object of this class contains token configurations.
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    public static final class TokenUtils {
        @NotNull
        private TokenType type;
        @Positive
        private Long expiryInMs;
        @NotNull
        private Boolean perUserUnique = true;
        private short limit = -1;
    }

    /**
     * Active directory Configuration.
     *
     * @param enabled Whether to enable active directory integration (defaults to false)
     * @param domain  base domain for the active directory
     * @param url     the ldap url of active directory
     */
    public record ActiveDirectoryConfig(
            boolean enabled,
            String domain,
            String url
    ) {
        @AssertTrue
        public boolean isValidUrl() {
            return !enabled || url != null;
        }

        @AssertTrue
        public boolean domainRequired() {
            return !enabled || domain != null;
        }
    }

    /**
     * The configuration class for jwt.
     *
     * @param secret     the fixed secret to use.
     * @param algorithm  the signature algorithm to use
     * @param expiryInMs the expiry of the token in milliseconds.
     */
    public record JwtConfig(
            @NotNull
            String secret,
            @NotNull
            SignatureAlgorithm algorithm,
            @Positive
            long expiryInMs,
            @Positive
            long expiryInMsRefreshToken
    ) {
    }

    /**
     * configuration values for cors.
     *
     * @param allowedOrigins allowedOrigins
     * @param allowedMethods allowedMethods
     * @param allowedHeaders allowedHeaders
     * @param exposedHeaders exposedHeaders
     * @param credentials    credentials
     */
    public record Cors(
            @NotEmpty
            ArrayList<String> allowedOrigins,
            @NotEmpty
            ArrayList<String> allowedMethods,
            @NotEmpty
            ArrayList<String> allowedHeaders,
            @NotNull
            ArrayList<String> exposedHeaders,
            boolean credentials
    ) {
    }

    private static boolean validateUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
