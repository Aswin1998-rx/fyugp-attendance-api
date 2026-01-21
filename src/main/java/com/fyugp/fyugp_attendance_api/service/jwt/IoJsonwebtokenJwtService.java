package com.fyugp.fyugp_attendance_api.service.jwt;


import com.fyugp.fyugp_attendance_api.Constants;
import com.fyugp.fyugp_attendance_api.config.AppProperties;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * Jwt implementation using <a href="https://github.com/jwtk/jjwt">io.jsonwebtoken</a>.<br>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoJsonwebtokenJwtService implements JwtTokenService {

    private final AppProperties properties;
    private JwtParser parser;
    private SecretKey key;


    @PostConstruct
    private void init() {
        log.info("initializing jwt service");
        this.key = this.getKey();
        this.parser = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build();
    }

    @Override
    public String createRefreshToken(User user) {
        log.debug("creating jwt refresh token");
        var iat = new Date();
        var exp = new Date(iat.getTime() + properties.getJwt().expiryInMsRefreshToken());
        return Jwts.builder()
                .claim(Constants.JWT_USER_EMAIL_CLAIM_KEY, user.getEmail())
                .claim(Constants.JWT_USER_PRIVILEGE_CLAIM_KEY, new String[]{Constants.REFRESH_TOKEN})
                .setSubject(String.valueOf(user.getId()))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(this.key)
                .compact();
    }

    @Override
    @Transactional
    public String createAccessToken(User user) {
        log.debug("creating jwt access token");
        var iat = new Date();
        var exp = new Date(iat.getTime() + properties.getJwt().expiryInMs());
        return Jwts.builder()
                .claim(Constants.JWT_USER_EMAIL_CLAIM_KEY, user.getEmail())
                .claim(Constants.JWT_USER_PRIVILEGE_CLAIM_KEY, getAuthorities(user))
                .setSubject(String.valueOf(user.getId()))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(this.key)
                .compact();
    }

    @Override
    public User validateAndParse(String token) {
        log.debug("parsing/validating jwt token");
        try {
            final var claims = this.parser.parseClaimsJws(token).getBody();
            final var user = new User();
            user.setId(Long.parseLong(claims.getSubject()));
            user.setEmail(claims.get(Constants.JWT_USER_EMAIL_CLAIM_KEY, String.class));
            user.setAuthorities(
                    ((List<String>) claims.get(Constants.JWT_USER_PRIVILEGE_CLAIM_KEY, List.class))
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
            );
            return user;
        } catch (MalformedJwtException e) {
            log.error("invalid jwt token", e);
        } catch (SignatureException e) {
            log.error("invalid jwt signature", e);
        } catch (ExpiredJwtException e) {
            log.error("jwt expired", e);
        } catch (IllegalArgumentException e) {
            log.error("jwt claims string is empty", e);
        }
        return null;
    }

    // TODO: convert to RSA key pair
    private SecretKey getKey() {
        var key = Constants.BASE64_REGEX.matcher(properties.getJwt().secret()).matches()
                ? properties.getJwt().secret().getBytes(StandardCharsets.UTF_8)
                : Base64.getEncoder().encode(properties.getJwt().secret().getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, properties.getJwt().algorithm().getJcaName());
    }

    private String[] getAuthorities(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream().map(Privilege::getName))
                .distinct()
                .toArray(String[]::new);
    }
}
