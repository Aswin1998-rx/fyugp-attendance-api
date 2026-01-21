package com.fyugp.fyugp_attendance_api.service.token;


import com.fyugp.fyugp_attendance_api.models.token.Token;
import com.fyugp.fyugp_attendance_api.models.token.TokenType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Token service.
 */
public interface TokenService {

    Token create(User user, TokenType password);

    boolean isTokenExpired(Token token);

    Token getToken(String token, TokenType password);

    void deleteToken(@NonNull Token token);

    void deleteToken(String token, TokenType password);

    List<Token> getAllTokens(TokenType type);

    void deleteUserAllTokens(User user);
}
