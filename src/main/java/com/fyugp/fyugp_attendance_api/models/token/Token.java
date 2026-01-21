package com.fyugp.fyugp_attendance_api.models.token;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Token.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "identity_token", uniqueConstraints = @UniqueConstraint(columnNames = {"token", "type"}))
public class Token extends BaseEntity {
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime expiry;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User user;
}
