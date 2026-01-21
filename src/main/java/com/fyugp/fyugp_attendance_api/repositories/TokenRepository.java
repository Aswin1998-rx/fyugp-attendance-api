package com.fyugp.fyugp_attendance_api.repositories;

import com.fyugp.fyugp_attendance_api.models.token.Token;
import com.fyugp.fyugp_attendance_api.models.token.TokenType;
import com.fyugp.fyugp_attendance_api.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Token repository.
 */
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Transactional
    void deleteByUserId(long clientId);

    Optional<Token> findByTokenAndType(String token, TokenType type);

    @Transactional
    void deleteByTokenAndType(String token, TokenType type);

    @Modifying
    @Query("delete from Token t where t.expiry <= CURRENT_TIMESTAMP")
    @Transactional
    void deleteAllExpired();

    List<Token> findAllByType(TokenType otp);

    @Query("SELECT count(u) from Token u WHERE u.type = ?2 AND u.user=?1")
    short countByClientAndType(User client, TokenType type);

    @Query("SELECT u FROM Token u WHERE u.user = ?1 AND u.type = ?2 AND u.expiry > CURRENT_TIMESTAMP")
    Optional<Token> getExpiredTokenByClientAndType(User client, TokenType type);

}
