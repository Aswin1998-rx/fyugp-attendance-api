package com.fyugp.fyugp_attendance_api.service.token;


import com.fyugp.fyugp_attendance_api.Constants;
import com.fyugp.fyugp_attendance_api.config.AppProperties;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.exceptions.BadRequestException;
import com.fyugp.fyugp_attendance_api.models.token.TokenType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.repositories.TokenRepository;
import com.fyugp.fyugp_attendance_api.utils.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fyugp.fyugp_attendance_api.models.token.Token;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;


/**
 * Default token service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultTokenService implements TokenService {

    private final TokenRepository tokenRepository;
    private final AppProperties appProperties;

    @Override
    public Token create(User user, TokenType type) {
        log.info("creating token");

        var tokenUtil = appProperties.getUtils()
                .tokenUtils()
                .stream()
                .filter(token -> token.getType().equals(type)).findFirst()
                .orElseThrow(() -> new AppException("errors.tokenTypeExpiryNotConfigured", type));

        long limit;
        try {
            limit = tokenRepository.countByClientAndType(user, type);
        } catch (Exception e) {
            throw new AppException("errors.failedToGetToken", e);
        }

        if (tokenUtil.getLimit() != -1 && limit >= tokenUtil.getLimit()) {
            throw new BadRequestException("errors.otpLimitExceeded");
        }

        if (tokenUtil.getPerUserUnique().equals(true)) {
            try {
                tokenRepository.deleteByUserId(user.getId());
            } catch (Exception e) {
                throw new AppException("errors.failedToDeleteToken", e);
            }
        }


        Optional<Token> oldToken = tokenRepository.getExpiredTokenByClientAndType(user, type);
        if (oldToken.isPresent()) {
            Token oldToken1 = oldToken.get();
            oldToken1.setExpiry(oldToken1.getCreatedAt());
            try {

                return tokenRepository.save(oldToken1);
            } catch (Exception e) {
                throw new AppException("errors.failedToSaveToken", e);
            }
        }

        LocalDateTime expiryDate = LocalDateTime.now().plus(tokenUtil.getExpiryInMs(), ChronoUnit.MILLIS);
        Token token = new Token();
        token.setType(type);
        token.setExpiry(expiryDate);
        token.setUser(user);
        token.setToken(PasswordUtil.secureRandomNumber(Constants.OTP_LENGTH));
        try {

            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new AppException("errors.failedToSaveToken", e);
        }
    }

    @Override
    public boolean isTokenExpired(Token token) {
        log.debug("checking token expiry");
        return nonNull(token.getExpiry()) && LocalDateTime.now().isAfter(token.getExpiry());
    }

    @Override
    public Token getToken(String tokenString, TokenType type) {
        log.info("getting token {}", type);
        Optional<Token> token;
        try {
            token = tokenRepository.findByTokenAndType(tokenString, type);
        } catch (Exception e) {
            throw new AppException("errors.failedToGetToken", e);
        }
        return token.orElseThrow(() -> new EntityNotFoundException("errors.tokenNotFound"));
    }

    @Override
    public void deleteToken(@NonNull Token token) {
        log.info("deleting token {}", token.getType());
        try {
            tokenRepository.delete(token);
        } catch (Exception e) {
            throw new AppException("errors.failedToDeleteToken", e);
        }
    }

    @Override
    public void deleteToken(String token, TokenType type) {
        try {
            tokenRepository.deleteByTokenAndType(token, type);
        } catch (Exception e) {
            throw new AppException("errors.failedToDeleteToken", e);
        }
    }

    /**
     * Method for get all otp.
     *
     * @return all otp
     */
    @Override
    public List<Token> getAllTokens(TokenType type) {
        log.info("getting all tokens");
        try {
            return tokenRepository.findAllByType(type);
        } catch (Exception e) {
            throw new AppException("errors.failedToFindTokens", e);
        }
    }

    /**
     * Deleting user all tokens.
     *
     * @param user user
     */
    @Override
    public void deleteUserAllTokens(User user) {
        log.info("deleting user tokens");
        try {
            tokenRepository.deleteByUserId(user.getId());
        } catch (Exception e) {
          throw new AppException("errors.failedToDeleteToken", e);
        }
    }


}
